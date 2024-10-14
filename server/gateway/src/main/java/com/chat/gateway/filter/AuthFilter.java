package com.chat.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.chat.core.constants.HttpConstants;
import com.chat.core.constants.RedisConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.enums.ResCode;
import com.chat.core.enums.UserIdentity;
import com.chat.core.utils.JwtUtils;
import com.chat.gateway.properties.IgnoreWhiteProperties;
import com.chat.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

// 网关鉴权过滤器
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Resource
    private IgnoreWhiteProperties ignoreWhite;

    @Value("${jwt.secret}")
    private String secret;

    @Resource
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取http请求和url
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();

        // 跳过不需要验证的路径(例如登录注册)
        if (matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }

        // 从http请求头中获取token
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, "token不能为空");
        }

        Claims claims;
        try {
            // 获取token中信息
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                return unauthorizedResponse(exchange, "token已过期或验证不正确");
            }
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "token已过期或验证不正确");
        }

        // 获取jwt中的key
        String userKey = JwtUtils.getUserKey(claims);
        if (!redisService.isKey(getTokenKey(userKey))) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }

        //判断jwt中的信息是否完整
        String userid = JwtUtils.getUserId(claims);
        if (StrUtil.isEmpty(userid)) {
            return unauthorizedResponse(exchange, "token验证失败");
        }

        // 验证用户的权限信息
        LoginUserData user = redisService.get(getTokenKey(userKey), LoginUserData.class);
        if (url.contains(HttpConstants.ADMIN_URL_PREFIX) && !UserIdentity.ADMIN.getValue().equals(user.getIdentity())) {
            return unauthorizedResponse(exchange, "token验证失败");
        }
        if (url.contains(HttpConstants.USER_URL_PREFIX) && !UserIdentity.ORDINARY.getValue().equals(user.getIdentity())) {
            return unauthorizedResponse(exchange, "token验证失败");
        }

        return chain.filter(exchange);
    }

    // 判断指定url是否与指定列表中的某个字符串相匹配
    private boolean matches(String url, List<String> patternList) {
        if (StrUtil.isEmpty(url) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }

        for (String pattern : patternList) {
            if (isMatch(pattern, url)) {
                return true;
            }
        }

        return false;
    }

    // 判断url是否与规则匹配
    private boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    // 获取redis中的key
    private String getTokenKey(String token) {
        return RedisConstants.LOGIN_TOKEN_KEY + token;
    }

    // 从请求头中获取请求token
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpConstants.AUTHENTICATION);

        // 如果前端设置了token前缀 则裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        return token;
    }

    // 鉴权异常处理
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]: 请求路径:{}", exchange.getRequest().getPath());
        return webFluxResponseWriter(exchange.getResponse(), msg, ResCode.FAILED_UNAUTHORIZED.getCode());
    }

    // 拼装webflux模型响应
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, int code) {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Resp<?> result = Resp.fail(code, msg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    // 优先级 值越小越先执行
    @Override
    public int getOrder() {
        return -1;
    }
}

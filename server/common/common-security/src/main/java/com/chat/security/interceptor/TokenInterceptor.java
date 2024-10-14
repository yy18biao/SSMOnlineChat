package com.chat.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.security.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// token拦截器(运行于网关鉴权过滤器后)
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        System.out.println(token);
        if (StrUtil.isEmpty(token)) {
            return true;
        }

        Claims claims = tokenService.getClaims(token, secret);
        tokenService.extendToken(claims);
        return true;
    }

    // 在http请求中提取出token
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, "");
        }
        return token;
    }
}

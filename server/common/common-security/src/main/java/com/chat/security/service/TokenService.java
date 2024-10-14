package com.chat.security.service;

import cn.hutool.core.lang.UUID;
import com.chat.core.constants.RedisConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.utils.JwtUtils;
import com.chat.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// token服务类
@Service
@Slf4j
public class TokenService {
    @Resource
    private RedisService redisService;

    // 登录成功创建token
    public String createToken(String userId, String secret, Integer identity, String nickname, String photo) {
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString();
        claims.put("userId", userId);
        claims.put("userKey", userKey);
        String token = JwtUtils.createToken(claims, secret);

        // 将用户敏感信息对象与userId存放到redis
        String key = RedisConstants.LOGIN_TOKEN_KEY + userKey;
        LoginUserData loginUserData = new LoginUserData();
        loginUserData.setIdentity(identity);
        loginUserData.setNickname(nickname);
        loginUserData.setPhoto(photo);
        redisService.set(key, loginUserData, RedisConstants.EXP, TimeUnit.MINUTES);

        return token;
    }

    // 延长token的有效时间
    public void extendToken(Claims claims){
        String userKey = getUserKey(claims);
        if (userKey == null) {
            return;
        }
        String tokenKey = RedisConstants.LOGIN_TOKEN_KEY + userKey;

        // 判断是否需要延长时间
        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire < RedisConstants.REFRESH_TIME) {
            redisService.expire(tokenKey, RedisConstants.EXP, TimeUnit.MINUTES);
        }
    }

    // 根据token和secret获取用户登录的userKey从而获取到用户的数据
    public LoginUserData getLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return null;
        }
        return redisService.get(RedisConstants.LOGIN_TOKEN_KEY + userKey, LoginUserData.class);
    }

    // 根据token和secret获取用户登录的userKey从而删除redis中的相对应kv
    public boolean deleteLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return false;
        }
        return redisService.delete(RedisConstants.LOGIN_TOKEN_KEY + userKey);
    }

    // 获取token中payload的userId
    public String getUserId(Claims claims) {
        if (claims == null) return null;
        return String.valueOf(JwtUtils.getUserId(claims));
    }

    // 获取token数据中payload的userKey
    public String getUserKey(Claims claims) {
        if (claims == null) return null;
        return JwtUtils.getUserKey(claims);
    }
    private String getUserKey(String token, String secret) {
        Claims claims = getClaims(token, secret);
        if (claims == null) return null;
        return JwtUtils.getUserKey(claims);
    }

    // 获取token中的正文数据
    public Claims getClaims(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                log.error("解析token：{}, 出现异常", token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token：{}, 出现异常", token, e);
            return null;
        }
        return claims;
    }
}

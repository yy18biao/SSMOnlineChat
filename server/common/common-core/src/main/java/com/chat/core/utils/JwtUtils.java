package com.chat.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUtils {
    // 生成令牌
    // claims 用户的唯一标识
    // secret 密钥
    public static String createToken(Map<String, Object> claims, String secret) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // 从令牌中获取数据
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // 获取userKey的值
    public static String getUserKey(Claims claims) {
        return toStr(claims.get("userKey"));
    }

    // 将value转换为字符串类型
    private static String toStr(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}

package com.example.api.campusmart.util;

import com.example.api.campusmart.config.TestConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtUtil {

    private JwtUtil() {
    }

    public static Long parseUserId(String token) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(
                    TestConfig.getJwtSecret().getBytes(StandardCharsets.UTF_8));
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();
            Claims claims = parser.parseClaimsJws(token).getBody();
            return claims.get("userId", Long.class);
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("Token 已过期", e);
        } catch (Exception e) {
            throw new IllegalStateException("Token 解析失败: " + e.getMessage(), e);
        }
    }
}

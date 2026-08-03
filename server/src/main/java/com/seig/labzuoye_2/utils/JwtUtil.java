package com.seig.labzuoye_2.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET = "yourSecretKeyForJwtTokenGeneration1234567890!@#";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token - 0.12.x 版本
     */
    public String generateToken(Integer userId, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析token - 0.12.x 版本
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
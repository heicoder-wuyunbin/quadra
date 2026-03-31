package com.quadra.system.adapter.out.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * 管理端 JWT 工具类
 * 使用独立的 secret 和 scope，与用户端隔离
 */
@Component
public class JwtUtil {

    @Value("${admin.jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${admin.jwt.access-token-ttl-seconds}")
    private long accessTokenTtlSeconds;

    @Value("${admin.jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${admin.jwt.refresh-token-ttl-seconds}")
    private long refreshTokenTtlSeconds;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    // 启动时把字符串转成安全 Key
    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecret));
    }

    // 生成访问令牌
    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, accessKey, accessTokenTtlSeconds);
    }

    // 生成刷新令牌
    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, refreshKey, refreshTokenTtlSeconds);
    }

    // 通用生成
    private String generateToken(Map<String, Object> claims, SecretKey key, long ttlSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ttlSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // 验证访问令牌
    public Claims validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    // 验证刷新令牌
    public Claims validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    // 通用验证
    private Claims validateToken(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}

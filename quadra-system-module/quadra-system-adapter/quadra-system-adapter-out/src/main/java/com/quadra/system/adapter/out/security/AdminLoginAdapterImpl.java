package com.quadra.system.adapter.out.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.SysAdminDO;
import com.quadra.system.adapter.out.persistence.mapper.SysAdminMapper;
import com.quadra.system.adapter.out.security.jwt.JwtUtil;
import com.quadra.system.application.port.out.AdminLoginPort;
import com.quadra.system.domain.model.SysAdmin;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录适配器实现
 * 使用独立的 JWT secret，与用户端隔离
 */
@Component
public class AdminLoginAdapterImpl implements AdminLoginPort {

    private final SysAdminMapper sysAdminMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final String ACCESS_TOKEN_BLACKLIST_KEY_PREFIX = "admin:auth:blacklist:access:";

    public AdminLoginAdapterImpl(SysAdminMapper sysAdminMapper, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.sysAdminMapper = sysAdminMapper;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public SysAdmin findByUsername(String username) {
        LambdaQueryWrapper<SysAdminDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminDO::getUsername, username)
               .eq(SysAdminDO::getDeleted, 0);
        
        SysAdminDO adminDO = sysAdminMapper.selectOne(wrapper);
        if (adminDO == null) {
            return null;
        }

        return assembleAdmin(adminDO);
    }

    private SysAdmin assembleAdmin(SysAdminDO adminDO) {
        try {
            java.lang.reflect.Constructor<SysAdmin> constructor = SysAdmin.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SysAdmin admin = constructor.newInstance();
            
            java.lang.reflect.Field idField = SysAdmin.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(admin, adminDO.getId());

            java.lang.reflect.Field usernameField = SysAdmin.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(admin, adminDO.getUsername());

            java.lang.reflect.Field passwordField = SysAdmin.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(admin, adminDO.getPassword());

            java.lang.reflect.Field realNameField = SysAdmin.class.getDeclaredField("realName");
            realNameField.setAccessible(true);
            realNameField.set(admin, adminDO.getRealName());

            java.lang.reflect.Field statusField = SysAdmin.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(admin, adminDO.getStatus());

            return admin;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore SysAdmin from DB", e);
        }
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String generateAccessToken(Long adminId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("scope", "admin"); // 管理端 scope
        return jwtUtil.generateAccessToken(claims);
    }

    @Override
    public String generateRefreshToken(Long adminId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("scope", "admin");
        return jwtUtil.generateRefreshToken(claims);
    }

    @Override
    public Long parseAccessTokenAdminId(String accessToken) {
        String key = ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            throw new RuntimeException("Token revoked");
        }
        Claims claims = jwtUtil.validateAccessToken(accessToken);
        return claims.get("adminId", Long.class);
    }

    @Override
    public Long parseRefreshTokenAdminId(String refreshToken) {
        Claims claims = jwtUtil.validateRefreshToken(refreshToken);
        return claims.get("adminId", Long.class);
    }

    @Override
    public void blacklistAccessToken(String accessToken) {
        Claims claims = jwtUtil.validateAccessToken(accessToken);
        Date expiration = claims.getExpiration();
        if (expiration != null) {
            long ttlMillis = expiration.getTime() - System.currentTimeMillis();
            if (ttlMillis > 0) {
                String key = ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
                stringRedisTemplate.opsForValue().set(key, "1", Duration.ofMillis(ttlMillis));
            }
        }
    }
}

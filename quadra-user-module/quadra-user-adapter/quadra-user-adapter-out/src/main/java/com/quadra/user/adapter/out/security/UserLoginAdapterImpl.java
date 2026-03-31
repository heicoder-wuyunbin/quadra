package com.quadra.user.adapter.out.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.user.adapter.out.persistence.entity.UserDO;
import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.adapter.out.security.jwt.JwtUtil;
import com.quadra.user.application.port.out.UserLoginPort;
import com.quadra.user.domain.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.Duration;

@Component
public class UserLoginAdapterImpl implements UserLoginPort {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final String ACCESS_TOKEN_BLACKLIST_KEY_PREFIX = "auth:blacklist:access:";

    public UserLoginAdapterImpl(UserMapper userMapper, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public User findByMobile(String mobile) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getMobile, mobile)
               .eq(UserDO::getDeleted, 0);
        
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            return null;
        }

        // 使用反射或特定的重构方法还原 Domain 聚合根。
        // 这里为了简化且不破坏 Domain 的封装，可以使用一个内部装配器。
        // 简单实现：我们只需要 id, password, status
        return assembleUser(userDO);
    }

    private User assembleUser(UserDO userDO) {
        // 由于 User 的构造被私有化了，在严格的 DDD 中，如果需要从 DB 恢复聚合，
        // 可以增加一个类似 User.restoreFromDb 的工厂方法。
        // 这里假设我们在 User 类中增加了一个包级私有或专用的恢复方法
        // 为了演示，我们暂时通过一个特殊的公共静态方法或反射来实现
        try {
            java.lang.reflect.Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();
            
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userDO.getId());

            java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(user, userDO.getPassword());

            java.lang.reflect.Field statusField = User.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(user, userDO.getStatus());

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore User from DB", e);
        }
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String generateAccessToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return jwtUtil.generateAccessToken(claims);
    }

    @Override
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return jwtUtil.generateRefreshToken(claims);
    }

    @Override
    public Long parseAccessTokenUserId(String accessToken) {
        String key = ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            throw new RuntimeException("Token revoked");
        }
        Claims claims = jwtUtil.validateAccessToken(accessToken);
        return claims.get("userId", Long.class);
    }

    @Override
    public Long parseRefreshTokenUserId(String refreshToken) {
        Claims claims = jwtUtil.validateRefreshToken(refreshToken);
        return claims.get("userId", Long.class);
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

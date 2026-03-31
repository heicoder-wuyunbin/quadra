package com.quadra.user.application.port.out;

import com.quadra.user.domain.model.User;

public interface UserLoginPort {
    /**
     * 根据手机号查询用户聚合根（仅供内部登录逻辑使用）
     */
    User findByMobile(String mobile);
    
    /**
     * 校验密码是否匹配
     */
    boolean matches(String rawPassword, String encodedPassword);
    
    /**
     * 生成访问令牌
     */
    String generateAccessToken(Long userId);
    
    /**
     * 生成刷新令牌
     */
    String generateRefreshToken(Long userId);

    /**
     * 解析访问令牌中的用户ID
     */
    Long parseAccessTokenUserId(String accessToken);

    Long parseRefreshTokenUserId(String refreshToken);

    void blacklistAccessToken(String accessToken);
}

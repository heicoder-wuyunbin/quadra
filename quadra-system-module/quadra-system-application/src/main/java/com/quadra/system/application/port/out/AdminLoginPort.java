package com.quadra.system.application.port.out;

import com.quadra.system.domain.model.SysAdmin;

public interface AdminLoginPort {
    /**
     * 根据用户名查询管理员聚合根（仅供内部登录逻辑使用）
     */
    SysAdmin findByUsername(String username);
    
    /**
     * 校验密码是否匹配
     */
    boolean matches(String rawPassword, String encodedPassword);
    
    /**
     * 生成访问令牌（管理端）
     */
    String generateAccessToken(Long adminId);
    
    /**
     * 生成刷新令牌（管理端）
     */
    String generateRefreshToken(Long adminId);

    /**
     * 解析访问令牌中的管理员ID
     */
    Long parseAccessTokenAdminId(String accessToken);

    /**
     * 解析刷新令牌中的管理员ID
     */
    Long parseRefreshTokenAdminId(String refreshToken);

    /**
     * 将令牌加入黑名单
     */
    void blacklistAccessToken(String accessToken);
}

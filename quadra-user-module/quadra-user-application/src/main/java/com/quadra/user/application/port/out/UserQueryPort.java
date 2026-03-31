package com.quadra.user.application.port.out;

import com.quadra.user.application.port.in.dto.UserProfileDTO;

public interface UserQueryPort {
    /**
     * 从数据库直接查询用户聚合的读模型 DTO
     * 绕过 Domain 聚合根
     */
    UserProfileDTO findProfileById(Long userId);
}

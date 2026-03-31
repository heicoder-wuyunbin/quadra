package com.quadra.user.application.port.in.query;

import com.quadra.user.application.port.in.dto.UserProfileDTO;

public interface GetUserProfileQuery {
    /**
     * 查询用户资料
     * @param userId 用户ID
     * @return 用户资料DTO
     */
    UserProfileDTO getProfile(Long userId);
}

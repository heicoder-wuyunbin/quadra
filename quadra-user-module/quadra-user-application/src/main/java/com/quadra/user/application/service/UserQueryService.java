package com.quadra.user.application.service;

import com.quadra.user.application.port.in.dto.UserProfileDTO;
import com.quadra.user.application.port.in.query.GetUserProfileQuery;
import com.quadra.user.application.port.out.UserQueryPort;
import org.springframework.stereotype.Service;

@Service
public class UserQueryService implements GetUserProfileQuery {

    private final UserQueryPort userQueryPort;

    public UserQueryService(UserQueryPort userQueryPort) {
        this.userQueryPort = userQueryPort;
    }

    @Override
    public UserProfileDTO getProfile(Long userId) {
        // 直接通过读模型端口查询，不需要组装 Domain 对象
        return userQueryPort.findProfileById(userId);
    }
}

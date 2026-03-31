package com.quadra.system.adapter.out.client;

import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.UserAdminDTO;
import com.quadra.system.application.port.in.dto.UserDetailDTO;
import com.quadra.system.application.port.out.UserCommandPort;
import com.quadra.system.application.port.out.UserQueryPort;
import com.quadra.system.domain.exception.DomainException;
import org.springframework.stereotype.Repository;

@Repository
public class UserServiceAdapter implements UserQueryPort, UserCommandPort {

    private final UserServiceClient userServiceClient;

    public UserServiceAdapter(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public PageResult<UserAdminDTO> findUsers(String mobile, Integer status, int page, int size) {
        UserServiceResult<UserServicePageResult<UserAdminDTO>> result =
                userServiceClient.listUsers(mobile, status, page, size);
        UserServicePageResult<UserAdminDTO> payload = unwrap(result);
        if (payload == null) {
            return PageResult.of(java.util.List.of(), 0, page, size);
        }
        return PageResult.of(payload.records(), payload.total(), payload.pageNo(), payload.pageSize());
    }

    @Override
    public UserDetailDTO findUserDetailById(String id) {
        UserServiceResult<UserDetailDTO> result = userServiceClient.getUserDetail(id);
        return unwrap(result);
    }

    @Override
    public int updateStatus(String userId, Integer status) {
        UserServiceResult<Void> result =
                userServiceClient.updateStatus(userId, new UpdateUserStatusRequest(status));
        unwrap(result);
        return 1;
    }

    @Override
    public String resetPassword(String userId) {
        UserServiceResult<ResetPasswordResult> result = userServiceClient.resetPassword(userId);
        ResetPasswordResult payload = unwrap(result);
        return payload != null ? payload.newPassword() : null;
    }

    private <T> T unwrap(UserServiceResult<T> result) {
        if (result == null) {
            throw new DomainException("用户服务无响应");
        }
        if (!result.success()) {
            throw new DomainException(result.message() != null ? result.message() : "用户服务请求失败");
        }
        return result.data();
    }
}

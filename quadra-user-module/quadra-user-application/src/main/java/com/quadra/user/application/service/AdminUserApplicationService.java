package com.quadra.user.application.service;

import com.quadra.user.application.port.in.AdminUserCommandUseCase;
import com.quadra.user.application.port.in.AdminUserQueryUseCase;
import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.out.AdminUserCommandPort;
import com.quadra.user.application.port.out.AdminUserQueryPort;
import com.quadra.user.application.port.out.PasswordEncoderPort;
import com.quadra.user.domain.exception.DomainException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserApplicationService implements AdminUserQueryUseCase, AdminUserCommandUseCase {

    private final AdminUserQueryPort adminUserQueryPort;
    private final AdminUserCommandPort adminUserCommandPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public AdminUserApplicationService(
            AdminUserQueryPort adminUserQueryPort,
            AdminUserCommandPort adminUserCommandPort,
            PasswordEncoderPort passwordEncoderPort) {
        this.adminUserQueryPort = adminUserQueryPort;
        this.adminUserCommandPort = adminUserCommandPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public PageResult<AdminUserDTO> listUsers(String mobile, Integer status, int page, int size) {
        return adminUserQueryPort.findUsers(mobile, status, page, size);
    }

    @Override
    public AdminUserDetailDTO getUserDetail(Long id) {
        return adminUserQueryPort.findUserDetail(id);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new DomainException("用户状态不合法");
        }
        int updated = adminUserCommandPort.updateStatus(userId, status);
        if (updated == 0) {
            throw new DomainException("用户不存在");
        }
    }

    @Override
    public String resetPassword(Long userId) {
        String newPassword = "123456";
        String encoded = passwordEncoderPort.encode(newPassword);
        int updated = adminUserCommandPort.updatePassword(userId, encoded);
        if (updated == 0) {
            throw new DomainException("用户不存在");
        }
        return newPassword;
    }
}

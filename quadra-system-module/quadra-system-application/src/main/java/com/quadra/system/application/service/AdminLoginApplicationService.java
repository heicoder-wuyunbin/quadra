package com.quadra.system.application.service;

import com.quadra.system.application.port.in.AdminLoginUseCase;
import com.quadra.system.application.port.in.AdminLogoutUseCase;
import com.quadra.system.application.port.in.ParseAdminTokenUseCase;
import com.quadra.system.application.port.in.RefreshAdminTokenUseCase;
import com.quadra.system.application.port.in.command.AdminLoginCommand;
import com.quadra.system.application.port.in.dto.AdminLoginResultDTO;
import com.quadra.system.application.port.in.dto.AdminTokenResultDTO;
import com.quadra.system.application.port.out.AdminLoginPort;
import com.quadra.system.domain.exception.DomainException;
import com.quadra.system.domain.model.SysAdmin;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginApplicationService implements AdminLoginUseCase, AdminLogoutUseCase, ParseAdminTokenUseCase, RefreshAdminTokenUseCase {

    private final AdminLoginPort adminLoginPort;

    public AdminLoginApplicationService(AdminLoginPort adminLoginPort) {
        this.adminLoginPort = adminLoginPort;
    }

    @Override
    public AdminLoginResultDTO login(AdminLoginCommand command) {
        // 1. 查询管理员
        SysAdmin admin = adminLoginPort.findByUsername(command.username());
        if (admin == null) {
            throw new DomainException("用户名或密码错误");
        }

        // 2. 校验状态
        if (admin.getStatus() == 0) {
            throw new DomainException("账号已被禁用");
        }

        // 3. 校验密码
        if (!adminLoginPort.matches(command.password(), admin.getPassword())) {
            throw new DomainException("用户名或密码错误");
        }

        // 4. 生成 Token（管理端使用独立的 JWT Scope）
        String accessToken = adminLoginPort.generateAccessToken(admin.getId());
        String refreshToken = adminLoginPort.generateRefreshToken(admin.getId());

        return new AdminLoginResultDTO(accessToken, refreshToken, admin.getId(), admin.getUsername(), admin.getRealName());
    }

    @Override
    public Long parseAdminId(String accessToken) {
        return adminLoginPort.parseAccessTokenAdminId(accessToken);
    }

    @Override
    public AdminTokenResultDTO refresh(String refreshToken) {
        Long adminId = adminLoginPort.parseRefreshTokenAdminId(refreshToken);
        String accessToken = adminLoginPort.generateAccessToken(adminId);
        String newRefreshToken = adminLoginPort.generateRefreshToken(adminId);
        return new AdminTokenResultDTO(accessToken, newRefreshToken, adminId);
    }

    @Override
    public void logout(String accessToken) {
        adminLoginPort.blacklistAccessToken(accessToken);
    }
}

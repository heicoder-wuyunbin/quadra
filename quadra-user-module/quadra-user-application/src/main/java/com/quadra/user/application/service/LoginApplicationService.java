package com.quadra.user.application.service;

import com.quadra.user.application.port.in.LoginUseCase;
import com.quadra.user.application.port.in.LogoutUseCase;
import com.quadra.user.application.port.in.ParseAccessTokenUseCase;
import com.quadra.user.application.port.in.RefreshTokenUseCase;
import com.quadra.user.application.port.in.command.LoginCommand;
import com.quadra.user.application.port.in.dto.LoginResultDTO;
import com.quadra.user.application.port.out.UserLoginPort;
import com.quadra.user.domain.exception.DomainException;
import com.quadra.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class LoginApplicationService implements LoginUseCase, ParseAccessTokenUseCase, RefreshTokenUseCase, LogoutUseCase {

    private final UserLoginPort userLoginPort;

    public LoginApplicationService(UserLoginPort userLoginPort) {
        this.userLoginPort = userLoginPort;
    }

    @Override
    public LoginResultDTO login(LoginCommand command) {
        // 1. 查询用户
        User user = userLoginPort.findByMobile(command.mobile());
        if (user == null) {
            throw new DomainException("手机号或密码错误");
        }

        // 2. 校验状态
        if (user.getStatus() == 0) {
            throw new DomainException("账号已被禁用");
        }

        // 3. 校验密码
        if (!userLoginPort.matches(command.rawPassword(), user.getPassword())) {
            throw new DomainException("手机号或密码错误");
        }

        // 4. 生成 Token
        String accessToken = userLoginPort.generateAccessToken(user.getId());
        String refreshToken = userLoginPort.generateRefreshToken(user.getId());

        return new LoginResultDTO(accessToken, refreshToken, user.getId());
    }

    @Override
    public Long parseUserId(String accessToken) {
        return userLoginPort.parseAccessTokenUserId(accessToken);
    }

    @Override
    public LoginResultDTO refresh(String refreshToken) {
        Long userId = userLoginPort.parseRefreshTokenUserId(refreshToken);
        String accessToken = userLoginPort.generateAccessToken(userId);
        String newRefreshToken = userLoginPort.generateRefreshToken(userId);
        return new LoginResultDTO(accessToken, newRefreshToken, userId);
    }

    @Override
    public void logout(String accessToken) {
        userLoginPort.blacklistAccessToken(accessToken);
    }
}

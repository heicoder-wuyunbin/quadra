package com.quadra.user.adapter.in.web;

import com.quadra.user.adapter.in.web.common.Result;
import com.quadra.user.adapter.in.web.dto.RefreshTokenRequest;
import com.quadra.user.adapter.in.web.dto.RegisterRequest;
import com.quadra.user.adapter.in.web.dto.UpdateProfileRequest;
import com.quadra.user.adapter.in.web.dto.UpdateSettingRequest;
import com.quadra.user.application.port.in.LoginUseCase;
import com.quadra.user.application.port.in.LogoutUseCase;
import com.quadra.user.application.port.in.RegisterUserUseCase;
import com.quadra.user.application.port.in.RefreshTokenUseCase;
import com.quadra.user.application.port.in.UpdateProfileUseCase;
import com.quadra.user.application.port.in.UpdateSettingUseCase;
import com.quadra.user.application.port.in.command.LoginCommand;
import com.quadra.user.application.port.in.command.RegisterUserCommand;
import com.quadra.user.application.port.in.command.UpdateProfileCommand;
import com.quadra.user.application.port.in.command.UpdateSettingCommand;
import com.quadra.user.application.port.in.dto.LoginResultDTO;
import com.quadra.user.application.port.in.dto.UserProfileDTO;
import com.quadra.user.application.port.in.query.GetUserProfileQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "用户相关接口")
@RestController
@RequestMapping("/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserProfileQuery getUserProfileQuery;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdateSettingUseCase updateSettingUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, GetUserProfileQuery getUserProfileQuery, LoginUseCase loginUseCase, RefreshTokenUseCase refreshTokenUseCase, LogoutUseCase logoutUseCase, UpdateProfileUseCase updateProfileUseCase, UpdateSettingUseCase updateSettingUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserProfileQuery = getUserProfileQuery;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.updateSettingUseCase = updateSettingUseCase;
    }

    @Operation(summary = "用户注册", description = "使用手机号和密码进行注册")
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(request.mobile(), request.password());
        Long userId = registerUserUseCase.register(command);
        return Result.success(userId);
    }

    @Operation(summary = "用户登录", description = "使用手机号和密码进行登录，返回双Token")
    @PostMapping("/login")
    public Result<LoginResultDTO> login(@RequestBody RegisterRequest request) {
        LoginCommand command = new LoginCommand(request.mobile(), request.password());
        LoginResultDTO result = loginUseCase.login(command);
        return Result.success(result);
    }

    @Operation(summary = "刷新令牌", description = "使用refresh token换取新token对")
    @PostMapping("/refresh")
    public Result<LoginResultDTO> refresh(@RequestBody RefreshTokenRequest request) {
        LoginResultDTO result = refreshTokenUseCase.refresh(request.refreshToken());
        return Result.success(result);
    }

    @Operation(summary = "用户登出", description = "将当前access token加入登出黑名单")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7)
                : authorization;
        logoutUseCase.logout(accessToken);
        return Result.success();
    }

    @Operation(summary = "获取用户资料", description = "根据用户ID获取基础资料及扩展标签")
    @GetMapping("/{id}/profile")
    public Result<UserProfileDTO> getProfile(
            @Parameter(description = "用户ID") @PathVariable("id") Long id) {
        UserProfileDTO profile = getUserProfileQuery.getProfile(id);
        if (profile == null) {
            return Result.failure(com.quadra.user.adapter.in.web.common.ResultCode.NOT_FOUND, "用户不存在");
        }
        return Result.success(profile);
    }

    @Operation(summary = "更新用户资料", description = "根据用户ID更新资料，仅支持增量更新（传null的字段不更新）")
    @PutMapping("/{id}/profile")
    public Result<Void> updateProfile(
            @Parameter(description = "用户ID") @PathVariable("id") Long id,
            @RequestBody UpdateProfileRequest request) {
        
        UpdateProfileCommand command = new UpdateProfileCommand(
            id,
            request.nickname(),
            request.avatar(),
            request.gender(),
            request.birthday(),
            request.city(),
            request.income(),
            request.profession(),
            request.marriage(),
            request.coverPic(),
            request.tags()
        );
        
        updateProfileUseCase.updateProfile(command);
        return Result.success();
    }

    @Operation(summary = "更新偏好设置", description = "根据用户ID更新偏好设置，仅支持增量更新")
    @PutMapping("/{id}/setting")
    public Result<Void> updateSetting(
            @Parameter(description = "用户ID") @PathVariable("id") Long id,
            @RequestBody UpdateSettingRequest request) {
        
        UpdateSettingCommand command = new UpdateSettingCommand(
            id,
            request.likeNotification(),
            request.commentNotification(),
            request.systemNotification()
        );
        
        updateSettingUseCase.updateSetting(command);
        return Result.success();
    }
}

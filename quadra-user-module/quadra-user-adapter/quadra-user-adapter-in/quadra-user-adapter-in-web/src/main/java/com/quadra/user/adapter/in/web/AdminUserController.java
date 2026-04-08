package com.quadra.user.adapter.in.web;

import com.quadra.user.adapter.in.web.common.Result;
import com.quadra.user.adapter.in.web.common.ResultCode;
import com.quadra.user.adapter.in.web.dto.ResetPasswordResult;
import com.quadra.user.adapter.in.web.dto.UpdateUserStatusRequest;
import com.quadra.user.application.port.in.AdminUserCommandUseCase;
import com.quadra.user.application.port.in.AdminUserQueryUseCase;
import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminUser", description = "管理员用户管理接口")
@Slf4j
@RestController
@RequestMapping("/users/admin/users")
public class AdminUserController {

    private final AdminUserQueryUseCase adminUserQueryUseCase;
    private final AdminUserCommandUseCase adminUserCommandUseCase;

    public AdminUserController(
            AdminUserQueryUseCase adminUserQueryUseCase,
            AdminUserCommandUseCase adminUserCommandUseCase) {
        this.adminUserQueryUseCase = adminUserQueryUseCase;
        this.adminUserCommandUseCase = adminUserCommandUseCase;
    }

    @Operation(summary = "用户列表", description = "管理员分页查询用户列表")
    @GetMapping
    public Result<PageResult<AdminUserDTO>> listUsers(
            @Parameter(description = "手机号模糊搜索") @RequestParam(required = false) String mobile,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        PageResult<AdminUserDTO> result = adminUserQueryUseCase.listUsers(mobile, status, page, size);
        return Result.success(result);
    }

    @Operation(summary = "用户详情", description = "管理员查看用户详情")
    @GetMapping("/{id}")
    public Result<AdminUserDetailDTO> getUserDetail(
            @Parameter(description = "用户 ID") @PathVariable("id") String id) {
        Long userId = parseUserId(id);
        if (userId == null) {
            return Result.failure(ResultCode.BAD_REQUEST, "用户ID不合法");
        }
        log.info("获取用户详情，id={}", userId);
        AdminUserDetailDTO detail = adminUserQueryUseCase.getUserDetail(userId);
        if (detail == null) {
            log.warn("用户详情不存在，id={}", userId);
            return Result.failure(ResultCode.NOT_FOUND, "用户不存在");
        }
        log.info("获取用户详情成功，id={}, mobile={}", userId, detail.mobile());
        return Result.success(detail);
    }

    @Operation(summary = "更新用户状态", description = "启用/禁用用户账号")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable("id") String id,
            @RequestBody UpdateUserStatusRequest request) {
        Long userId = parseUserId(id);
        if (userId == null) {
            return Result.failure(ResultCode.BAD_REQUEST, "用户ID不合法");
        }
        adminUserCommandUseCase.updateStatus(userId, request.status());
        return Result.success();
    }

    @Operation(summary = "重置密码", description = "重置用户密码为默认值")
    @PostMapping("/{id}/reset-password")
    public Result<ResetPasswordResult> resetPassword(
            @Parameter(description = "用户ID") @PathVariable("id") String id) {
        Long userId = parseUserId(id);
        if (userId == null) {
            return Result.failure(ResultCode.BAD_REQUEST, "用户ID不合法");
        }
        String newPassword = adminUserCommandUseCase.resetPassword(userId);
        return Result.success(new ResetPasswordResult(newPassword));
    }

    private Long parseUserId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException ex) {
            log.warn("用户ID格式非法，id={}", id);
            return null;
        }
    }
}

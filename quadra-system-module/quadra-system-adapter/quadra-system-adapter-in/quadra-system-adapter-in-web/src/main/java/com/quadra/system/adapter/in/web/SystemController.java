package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.adapter.in.web.common.ResultCode;
import com.quadra.system.adapter.in.web.dto.*;
import com.quadra.system.application.port.in.*;
import com.quadra.system.application.port.in.command.*;
import com.quadra.system.application.port.in.dto.*;
import com.quadra.system.application.port.in.query.GetDailyAnalysisQuery;
import com.quadra.system.application.port.in.query.GetMenuTreeQuery;
import com.quadra.system.application.port.in.query.ListAdminsQuery;
import com.quadra.system.application.port.in.query.ListUsersQuery;
import com.quadra.system.application.service.SystemApplicationService;
import com.quadra.system.adapter.in.web.aspect.LogOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "System", description = "系统管理相关接口")
@RestController
@RequestMapping("/system")
public class SystemController {

    private final AdminLoginUseCase adminLoginUseCase;
    private final RefreshAdminTokenUseCase refreshAdminTokenUseCase;
    private final AdminLogoutUseCase adminLogoutUseCase;
    private final ParseAdminTokenUseCase parseAdminTokenUseCase;
    private final AssignRoleToAdminUseCase assignRoleToAdminUseCase;
    private final GrantMenuToRoleUseCase grantMenuToRoleUseCase;
    private final SystemApplicationService systemApplicationService;
    private final ListAdminsQuery listAdminsQuery;
    private final GetMenuTreeQuery getMenuTreeQuery;
    private final GetDailyAnalysisQuery getDailyAnalysisQuery;
    private final ListUsersQuery listUsersQuery;
    private final SaveLoginLogUseCase saveLoginLogUseCase;

    public SystemController(
            AdminLoginUseCase adminLoginUseCase,
            RefreshAdminTokenUseCase refreshAdminTokenUseCase,
            AdminLogoutUseCase adminLogoutUseCase,
            ParseAdminTokenUseCase parseAdminTokenUseCase,
            AssignRoleToAdminUseCase assignRoleToAdminUseCase,
            GrantMenuToRoleUseCase grantMenuToRoleUseCase,
            SystemApplicationService systemApplicationService,
            ListAdminsQuery listAdminsQuery,
            GetMenuTreeQuery getMenuTreeQuery,
            GetDailyAnalysisQuery getDailyAnalysisQuery,
            ListUsersQuery listUsersQuery,
            SaveLoginLogUseCase saveLoginLogUseCase) {
        this.adminLoginUseCase = adminLoginUseCase;
        this.refreshAdminTokenUseCase = refreshAdminTokenUseCase;
        this.adminLogoutUseCase = adminLogoutUseCase;
        this.parseAdminTokenUseCase = parseAdminTokenUseCase;
        this.assignRoleToAdminUseCase = assignRoleToAdminUseCase;
        this.grantMenuToRoleUseCase = grantMenuToRoleUseCase;
        this.systemApplicationService = systemApplicationService;
        this.listAdminsQuery = listAdminsQuery;
        this.getMenuTreeQuery = getMenuTreeQuery;
        this.getDailyAnalysisQuery = getDailyAnalysisQuery;
        this.listUsersQuery = listUsersQuery;
        this.saveLoginLogUseCase = saveLoginLogUseCase;
    }

    // ==================== 管理员登录/登出 ====================

    @Operation(summary = "管理员登录", description = "使用用户名和密码进行登录，返回双Token")
    @PostMapping("/admin/login")
    public Result<AdminLoginResultDTO> login(@RequestBody AdminLoginRequest request, HttpServletRequest httpRequest) {
        AdminLoginCommand command = new AdminLoginCommand(request.username(), request.password());
        try {
            AdminLoginResultDTO result = adminLoginUseCase.login(command);
            saveLoginLogUseCase.save(new SaveLoginLogCommand(
                    result.adminId(),
                    result.username(),
                    httpRequest.getRemoteAddr(),
                    "Unknown",
                    httpRequest.getHeader("User-Agent"),
                    "SUCCESS",
                    null
            ));
            return Result.success(result);
        } catch (Exception e) {
            saveLoginLogUseCase.save(new SaveLoginLogCommand(
                    null,
                    request.username(),
                    httpRequest.getRemoteAddr(),
                    "Unknown",
                    httpRequest.getHeader("User-Agent"),
                    "FAILED",
                    e.getMessage()
            ));
            throw e;
        }
    }

    @Operation(summary = "刷新令牌", description = "使用refresh token换取新token对")
    @PostMapping("/admin/refresh")
    public Result<AdminTokenResultDTO> refresh(@RequestBody RefreshTokenRequest request) {
        AdminTokenResultDTO result = refreshAdminTokenUseCase.refresh(request.refreshToken());
        return Result.success(result);
    }

    @Operation(summary = "管理员登出", description = "将当前access token加入登出黑名单")
    @PostMapping("/admin/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7)
                : authorization;
        adminLogoutUseCase.logout(accessToken);
        return Result.success();
    }

    // ==================== 管理员 CRUD ====================

    @Operation(summary = "创建管理员", description = "创建新的管理员账号")
    @PostMapping("/admins")
    public Result<Long> createAdmin(@RequestBody CreateAdminRequest request) {
        CreateAdminCommand command = new CreateAdminCommand(
                request.username(), request.password(), request.realName()
        );
        Long adminId = systemApplicationService.createAdmin(command);
        return Result.success(adminId);
    }

    @Operation(summary = "更新管理员", description = "更新管理员信息")
    @PutMapping("/admins/{id}")
    public Result<Void> updateAdmin(
            @Parameter(description = "管理员 ID") @PathVariable("id") Long id,
            @RequestBody UpdateAdminRequest request) {
        UpdateAdminCommand command = new UpdateAdminCommand(id, request.realName());
        systemApplicationService.updateAdmin(command);
        return Result.success();
    }

    @Operation(summary = "更新管理员密码", description = "更新管理员密码")
    @PutMapping("/admins/{id}/password")
    public Result<Void> updateAdminPassword(
            @Parameter(description = "管理员 ID") @PathVariable("id") Long id,
            @RequestBody UpdateAdminPasswordRequest request) {
        UpdateAdminPasswordCommand command = new UpdateAdminPasswordCommand(id, request.password());
        systemApplicationService.updateAdminPassword(command);
        return Result.success();
    }

    @Operation(summary = "更新管理员状态", description = "启用/禁用管理员账号")
    @PutMapping("/admins/{id}/status")
    public Result<Void> updateAdminStatus(
            @Parameter(description = "管理员 ID") @PathVariable("id") Long id,
            @RequestBody UpdateAdminStatusRequest request) {
        systemApplicationService.updateAdminStatus(id, request.status());
        return Result.success();
    }

    @Operation(summary = "批量更新管理员状态", description = "批量启用/禁用管理员账号")
    @PutMapping("/admins/status/batch")
    public Result<Void> batchUpdateAdminStatus(
            @RequestBody BatchAdminIdsRequest request) {
        systemApplicationService.batchUpdateAdminStatus(request.adminIds(), request.status());
        return Result.success();
    }

    @Operation(summary = "批量删除管理员", description = "批量软删除管理员")
    @DeleteMapping("/admins/batch")
    public Result<Void> batchDeleteAdmins(
            @RequestBody BatchAdminIdsRequest request) {
        systemApplicationService.batchDeleteAdmins(request.adminIds());
        return Result.success();
    }

    @Operation(summary = "获取管理员列表", description = "分页查询管理员列表")
    @GetMapping("/admins")
    public Result<PageResult<AdminDTO>> listAdmins(
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        PageResult<AdminDTO> result = listAdminsQuery.listAdmins(status, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取用户列表", description = "分页查询用户列表（管理端）")
    @GetMapping("/users")
    public Result<PageResult<UserAdminDTO>> listUsers(
            @Parameter(description = "手机号模糊搜索") @RequestParam(required = false) String mobile,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        PageResult<UserAdminDTO> result = listUsersQuery.listUsers(mobile, status, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取用户详情", description = "根据用户ID获取详细信息（管理端）")
    @GetMapping("/users/{id}")
    public Result<UserDetailDTO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable("id") String id) {
        UserDetailDTO detail = listUsersQuery.getUserDetailById(id);
        if (detail == null) {
            return Result.failure(ResultCode.NOT_FOUND, "用户不存在");
        }
        return Result.success(detail);
    }

    @Operation(summary = "更新用户状态", description = "启用/禁用用户账号（管理端）")
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable("id") String id,
            @RequestBody UpdateUserStatusRequest request) {
        systemApplicationService.updateUserStatus(id, request.status());
        return Result.success();
    }

    @Operation(summary = "重置用户密码", description = "重置用户密码为默认值（管理端）")
    @PostMapping("/users/{id}/reset-password")
    public Result<ResetPasswordResult> resetUserPassword(
            @Parameter(description = "用户ID") @PathVariable("id") String id) {
        String newPassword = systemApplicationService.resetUserPassword(id);
        return Result.success(new ResetPasswordResult(newPassword));
    }

    // ==================== 角色授权 ====================

    @Operation(summary = "分配角色", description = "为管理员分配角色")
    @PostMapping("/admin/roles")
    public Result<Void> assignRole(@RequestBody AssignRoleRequest request) {
        AssignRoleCommand command = new AssignRoleCommand(request.adminId(), request.roleIds());
        assignRoleToAdminUseCase.assignRole(command);
        return Result.success();
    }

    // ==================== 角色管理 ====================

    @Operation(summary = "创建角色", description = "创建新的角色")
    @PostMapping("/roles")
    public Result<Long> createRole(@RequestBody CreateRoleRequest request) {
        CreateRoleCommand command = new CreateRoleCommand(
                request.roleCode(), request.roleName(), request.description()
        );
        Long roleId = systemApplicationService.createRole(command);
        return Result.success(roleId);
    }

    @Operation(summary = "授予菜单权限", description = "为角色授予菜单权限")
    @PostMapping("/roles/menus")
    public Result<Void> grantMenu(@RequestBody GrantMenuRequest request) {
        GrantMenuCommand command = new GrantMenuCommand(request.roleId(), request.menuIds());
        grantMenuToRoleUseCase.grantMenu(command);
        return Result.success();
    }

    // ==================== 菜单管理 ====================

    @Operation(summary = "创建菜单", description = "创建新的菜单或权限点")
    @PostMapping("/menus")
    public Result<Long> createMenu(@RequestBody CreateMenuRequest request) {
        CreateMenuCommand command = new CreateMenuCommand(
                request.parentId(), request.menuName(), request.menuType(),
                request.permissionCode(), request.path(), request.icon(), request.sortOrder()
        );
        Long menuId = systemApplicationService.createMenu(command);
        return Result.success(menuId);
    }

    @LogOperation(module = "系统管理", action = "获取菜单树")
    @Operation(summary = "获取菜单树", description = "获取完整的菜单树结构")
    @GetMapping("/menus/tree")
    public Result<List<MenuTreeDTO>> getMenuTree() {
        List<MenuTreeDTO> result = getMenuTreeQuery.getMenuTree();
        return Result.success(result);
    }

    // ==================== 数据分析 ====================

    @Operation(summary = "获取每日分析数据", description = "按日期查询每日统计分析数据")
    @GetMapping("/analysis/daily")
    public Result<DailyAnalysisDTO> getDailyAnalysis(
            @Parameter(description = "日期") @RequestParam String date) {
        DailyAnalysisDTO result = getDailyAnalysisQuery.getByDate(LocalDate.parse(date));
        return Result.success(result);
    }
}

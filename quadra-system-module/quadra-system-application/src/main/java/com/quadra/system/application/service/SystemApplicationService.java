package com.quadra.system.application.service;

import com.quadra.system.application.port.in.AssignRoleToAdminUseCase;
import com.quadra.system.application.port.in.GrantMenuToRoleUseCase;
import com.quadra.system.application.port.in.GenerateDailyAnalysisUseCase;
import com.quadra.system.application.port.in.command.AssignRoleCommand;
import com.quadra.system.application.port.in.command.CreateAdminCommand;
import com.quadra.system.application.port.in.command.CreateRoleCommand;
import com.quadra.system.application.port.in.command.CreateMenuCommand;
import com.quadra.system.application.port.in.command.GrantMenuCommand;
import com.quadra.system.application.port.in.command.UpdateAdminCommand;
import com.quadra.system.application.port.in.command.UpdateAdminPasswordCommand;
import com.quadra.system.application.port.out.AdminRepositoryPort;
import com.quadra.system.application.port.out.RoleRepositoryPort;
import com.quadra.system.application.port.out.MenuRepositoryPort;
import com.quadra.system.application.port.out.AnalysisRepositoryPort;
import com.quadra.system.application.port.out.EventPublisherPort;
import com.quadra.system.application.port.out.UserCommandPort;
import com.quadra.system.domain.exception.DomainException;
import com.quadra.system.domain.model.SysAdmin;
import com.quadra.system.domain.model.SysRole;
import com.quadra.system.domain.model.SysMenu;
import com.quadra.system.domain.model.SysDataAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SystemApplicationService implements AssignRoleToAdminUseCase, GrantMenuToRoleUseCase, GenerateDailyAnalysisUseCase {

    private final AdminRepositoryPort adminRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final MenuRepositoryPort menuRepositoryPort;
    private final AnalysisRepositoryPort analysisRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final UserCommandPort userCommandPort;

    public SystemApplicationService(
            AdminRepositoryPort adminRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            MenuRepositoryPort menuRepositoryPort,
            AnalysisRepositoryPort analysisRepositoryPort,
            EventPublisherPort eventPublisherPort,
            UserCommandPort userCommandPort) {
        this.adminRepositoryPort = adminRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.menuRepositoryPort = menuRepositoryPort;
        this.analysisRepositoryPort = analysisRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.userCommandPort = userCommandPort;
    }

    // ==================== 管理员 CRUD ====================

    public Long createAdmin(CreateAdminCommand command) {
        // 检查用户名是否已存在
        SysAdmin existing = adminRepositoryPort.findByUsername(command.username());
        if (existing != null) {
            throw new DomainException("用户名已存在");
        }

        Long adminId = adminRepositoryPort.nextId();
        // 密码加密由 Adapter 层处理，这里假设已经加密
        SysAdmin admin = SysAdmin.create(adminId, command.username(), command.password(), command.realName());

        adminRepositoryPort.save(admin);

        // 发布领域事件
        if (!admin.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(admin.getDomainEvents());
            admin.clearDomainEvents();
        }

        return adminId;
    }

    public void updateAdmin(UpdateAdminCommand command) {
        SysAdmin admin = adminRepositoryPort.findById(command.adminId());
        if (admin == null) {
            throw new DomainException("管理员不存在");
        }

        admin.updateInfo(command.realName(), admin.getAvatar());
        adminRepositoryPort.update(admin);
    }

    public void updateAdminPassword(UpdateAdminPasswordCommand command) {
        SysAdmin admin = adminRepositoryPort.findById(command.adminId());
        if (admin == null) {
            throw new DomainException("管理员不存在");
        }

        admin.updatePassword(command.newPassword());
        adminRepositoryPort.update(admin);
    }

    public void updateAdminStatus(Long adminId, Integer status) {
        if (adminId == 1) {
            throw new DomainException("超级管理员不能被禁用");
        }

        SysAdmin admin = adminRepositoryPort.findById(adminId);
        if (admin == null) {
            throw new DomainException("管理员不存在");
        }

        if (status == 1) {
            admin.enable();
        } else {
            admin.disable();
        }

        adminRepositoryPort.update(admin);
    }

    // ==================== 用户管理 ====================

    public void updateUserStatus(String userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new DomainException("用户状态不合法");
        }
        int updated = userCommandPort.updateStatus(userId, status);
        if (updated == 0) {
            throw new DomainException("用户不存在");
        }
    }

    public String resetUserPassword(String userId) {
        String newPassword = userCommandPort.resetPassword(userId);
        if (newPassword == null || newPassword.isBlank()) {
            throw new DomainException("用户不存在");
        }
        return newPassword;
    }

    // ==================== 角色 CRUD ====================

    public Long createRole(CreateRoleCommand command) {
        // 检查角色编码是否已存在
        SysRole existing = roleRepositoryPort.findByRoleCode(command.roleCode());
        if (existing != null) {
            throw new DomainException("角色编码已存在");
        }

        Long roleId = roleRepositoryPort.nextId();
        SysRole role = SysRole.create(roleId, command.roleCode(), command.roleName(), command.description());

        roleRepositoryPort.save(role);
        return roleId;
    }

    // ==================== 菜单 CRUD ====================

    public Long createMenu(CreateMenuCommand command) {
        Long menuId = menuRepositoryPort.nextId();
        SysMenu menu = SysMenu.create(
                menuId,
                command.parentId(),
                command.menuName(),
                command.menuType(),
                command.permissionCode(),
                command.path(),
                command.icon(),
                command.sortOrder()
        );

        menuRepositoryPort.save(menu);
        return menuId;
    }

    // ==================== 授权操作 ====================

    @Override
    @Transactional
    public void assignRole(AssignRoleCommand command) {
        SysAdmin admin = adminRepositoryPort.findById(command.adminId());
        if (admin == null) {
            throw new DomainException("管理员不存在");
        }

        // 删除原有角色关联
        adminRepositoryPort.deleteAdminRoleRelation(command.adminId());

        // 插入新的角色关联
        if (command.roleIds() != null && !command.roleIds().isEmpty()) {
            adminRepositoryPort.insertAdminRoleRelation(command.adminId(), command.roleIds());
        }
    }

    @Override
    @Transactional
    public void grantMenu(GrantMenuCommand command) {
        SysRole role = roleRepositoryPort.findById(command.roleId());
        if (role == null) {
            throw new DomainException("角色不存在");
        }

        // 删除原有菜单关联
        roleRepositoryPort.deleteRoleMenuRelation(command.roleId());

        // 插入新的菜单关联
        if (command.menuIds() != null && !command.menuIds().isEmpty()) {
            roleRepositoryPort.insertRoleMenuRelation(command.roleId(), command.menuIds());
        }
    }

    // ==================== 数据分析 ====================

    @Override
    public void generateDailyAnalysis(LocalDate date) {
        // 检查是否已存在该日期的统计
        SysDataAnalysis existing = analysisRepositoryPort.findByRecordDate(date);
        if (existing != null) {
            // 已存在，更新数据（幂等）
            // TODO: 实际项目中需要调用其他微服务获取统计数据
            existing.update(0, 0, 0, 0, 0);
            analysisRepositoryPort.update(existing);
            return;
        }

        // 创建新的统计记录
        Long id = analysisRepositoryPort.nextId();
        // TODO: 实际项目中需要调用其他微服务获取统计数据
        SysDataAnalysis analysis = SysDataAnalysis.generate(id, date, 0, 0, 0, 0, 0);
        analysisRepositoryPort.save(analysis);
    }
}

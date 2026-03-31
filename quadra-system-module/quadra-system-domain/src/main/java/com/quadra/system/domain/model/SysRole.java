package com.quadra.system.domain.model;

import com.quadra.system.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色聚合根
 */
public class SysRole {
    
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status; // 0-禁用, 1-正常
    private Integer version;
    private Integer deleted; // 0-未删除, 1-已删除
    
    // 菜单ID列表（聚合内维护）
    private List<Long> menuIds = new ArrayList<>();

    // 禁用默认无参构造
    private SysRole() {}

    /**
     * 工厂方法：创建角色
     */
    public static SysRole create(Long id, String roleCode, String roleName, String description) {
        if (id == null || id <= 0) {
            throw new DomainException("角色ID必须有效");
        }
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new DomainException("角色编码不能为空");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new DomainException("角色名称不能为空");
        }

        SysRole role = new SysRole();
        role.id = id;
        role.roleCode = roleCode;
        role.roleName = roleName;
        role.description = description;
        role.status = 1;
        role.version = 0;
        role.deleted = 0;

        return role;
    }

    /**
     * 启用角色
     */
    public void enable() {
        if (this.status == 1) {
            throw new DomainException("角色已处于启用状态");
        }
        this.status = 1;
    }

    /**
     * 禁用角色
     */
    public void disable() {
        if (this.status == 0) {
            throw new DomainException("角色已处于禁用状态");
        }
        this.status = 0;
    }

    /**
     * 授予菜单权限
     */
    public void grantMenus(List<Long> menuIds) {
        if (menuIds == null) {
            this.menuIds = new ArrayList<>();
        } else {
            this.menuIds = new ArrayList<>(menuIds);
        }
    }

    /**
     * 更新基本信息
     */
    public void updateInfo(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    // Getters
    public Long getId() { return id; }
    public String getRoleCode() { return roleCode; }
    public String getRoleName() { return roleName; }
    public String getDescription() { return description; }
    public Integer getStatus() { return status; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public List<Long> getMenuIds() { return menuIds; }
}

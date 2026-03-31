package com.quadra.system.domain.model;

import com.quadra.system.domain.event.AdminCreatedEvent;
import com.quadra.system.domain.event.DomainEvent;
import com.quadra.system.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员聚合根
 */
public class SysAdmin {
    
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String avatar;
    private Integer status; // 0-禁用, 1-正常
    private Integer version;
    private Integer deleted; // 0-未删除, 1-已删除
    
    // 角色ID列表（聚合内维护）
    private List<Long> roleIds = new ArrayList<>();
    
    // 领域事件容器
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // 禁用默认无参构造（外部禁止直接 new）
    private SysAdmin() {}

    /**
     * 工厂方法：创建管理员
     */
    public static SysAdmin create(Long id, String username, String encryptedPassword, String realName) {
        if (id == null || id <= 0) {
            throw new DomainException("管理员ID必须有效");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new DomainException("用户名不能为空");
        }
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new DomainException("密码不能为空");
        }

        SysAdmin admin = new SysAdmin();
        admin.id = id;
        admin.username = username;
        admin.password = encryptedPassword;
        admin.realName = realName;
        admin.status = 1; // 默认正常
        admin.version = 0;
        admin.deleted = 0;

        // 发布领域事件
        admin.domainEvents.add(new AdminCreatedEvent(id, username));

        return admin;
    }

    /**
     * 启用管理员
     */
    public void enable() {
        if (this.status == 1) {
            throw new DomainException("管理员已处于启用状态");
        }
        this.status = 1;
    }

    /**
     * 禁用管理员
     */
    public void disable() {
        if (this.status == 0) {
            throw new DomainException("管理员已处于禁用状态");
        }
        this.status = 0;
    }

    /**
     * 分配角色
     */
    public void assignRoles(List<Long> roleIds) {
        if (roleIds == null) {
            this.roleIds = new ArrayList<>();
        } else {
            this.roleIds = new ArrayList<>(roleIds);
        }
    }

    /**
     * 更新基本信息
     */
    public void updateInfo(String realName, String avatar) {
        this.realName = realName;
        this.avatar = avatar;
    }

    /**
     * 更新密码
     */
    public void updatePassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new DomainException("密码不能为空");
        }
        this.password = encryptedPassword;
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRealName() { return realName; }
    public String getAvatar() { return avatar; }
    public Integer getStatus() { return status; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public List<Long> getRoleIds() { return roleIds; }
    public List<DomainEvent> getDomainEvents() { return domainEvents; }
}

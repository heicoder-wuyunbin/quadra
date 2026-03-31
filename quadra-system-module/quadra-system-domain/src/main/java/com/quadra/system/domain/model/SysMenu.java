package com.quadra.system.domain.model;

import com.quadra.system.domain.exception.DomainException;

/**
 * 菜单/权限资源聚合根
 * 树形结构设计，统一管理前端路由菜单和后端接口权限
 */
public class SysMenu {
    
    private Long id;
    private Long parentId; // 父级ID，0表示顶级节点
    private String menuName;
    private Integer menuType; // 1-目录，2-菜单，3-按钮(接口权限)
    private String permissionCode; // 权限标识代码
    private String path; // 前端路由地址
    private String icon;
    private Integer sortOrder;
    private Integer status; // 0-隐藏/禁用，1-显示/正常

    // 禁用默认无参构造
    private SysMenu() {}

    /**
     * 工厂方法：创建菜单
     */
    public static SysMenu create(Long id, Long parentId, String menuName, Integer menuType, 
                                  String permissionCode, String path, String icon, Integer sortOrder) {
        if (id == null || id <= 0) {
            throw new DomainException("菜单ID必须有效");
        }
        if (parentId == null) {
            parentId = 0L; // 默认顶级节点
        }
        if (menuName == null || menuName.trim().isEmpty()) {
            throw new DomainException("菜单名称不能为空");
        }
        if (menuType == null || (menuType != 1 && menuType != 2 && menuType != 3)) {
            throw new DomainException("菜单类型必须为1-目录、2-菜单或3-按钮");
        }

        SysMenu menu = new SysMenu();
        menu.id = id;
        menu.parentId = parentId;
        menu.menuName = menuName;
        menu.menuType = menuType;
        menu.permissionCode = permissionCode;
        menu.path = path;
        menu.icon = icon;
        menu.sortOrder = sortOrder != null ? sortOrder : 0;
        menu.status = 1;

        return menu;
    }

    /**
     * 隐藏菜单
     */
    public void hide() {
        this.status = 0;
    }

    /**
     * 显示菜单
     */
    public void show() {
        this.status = 1;
    }

    /**
     * 更新菜单信息
     */
    public void updateInfo(String menuName, String path, String icon, Integer sortOrder) {
        if (menuName != null) {
            this.menuName = menuName;
        }
        this.path = path;
        this.icon = icon;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getParentId() { return parentId; }
    public String getMenuName() { return menuName; }
    public Integer getMenuType() { return menuType; }
    public String getPermissionCode() { return permissionCode; }
    public String getPath() { return path; }
    public String getIcon() { return icon; }
    public Integer getSortOrder() { return sortOrder; }
    public Integer getStatus() { return status; }
}

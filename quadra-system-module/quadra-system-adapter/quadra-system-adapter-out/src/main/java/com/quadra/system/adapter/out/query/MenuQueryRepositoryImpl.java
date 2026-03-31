package com.quadra.system.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.*;
import com.quadra.system.adapter.out.persistence.mapper.*;
import com.quadra.system.application.port.in.dto.MenuTreeDTO;
import com.quadra.system.application.port.out.MenuQueryPort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MenuQueryRepositoryImpl implements MenuQueryPort {

    private final SysMenuMapper sysMenuMapper;
    private final SysAdminRoleMapper sysAdminRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public MenuQueryRepositoryImpl(
            SysMenuMapper sysMenuMapper,
            SysAdminRoleMapper sysAdminRoleMapper,
            SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
        this.sysAdminRoleMapper = sysAdminRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public List<MenuTreeDTO> findAllMenus() {
        List<SysMenuDO> allMenus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenuDO>()
                        .eq(SysMenuDO::getStatus, 1)
                        .orderByAsc(SysMenuDO::getSortOrder)
        );
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<MenuTreeDTO> findMenusByAdminId(Long adminId) {
        // 1. 查询管理员的角色
        LambdaQueryWrapper<SysAdminRoleDO> arWrapper = new LambdaQueryWrapper<>();
        arWrapper.eq(SysAdminRoleDO::getAdminId, adminId);
        List<SysAdminRoleDO> adminRoles = sysAdminRoleMapper.selectList(arWrapper);

        if (adminRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = adminRoles.stream()
                .map(SysAdminRoleDO::getRoleId)
                .collect(Collectors.toList());

        // 2. 查询角色的菜单
        LambdaQueryWrapper<SysRoleMenuDO> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.in(SysRoleMenuDO::getRoleId, roleIds);
        List<SysRoleMenuDO> roleMenus = sysRoleMenuMapper.selectList(rmWrapper);

        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenuDO::getMenuId)
                .collect(Collectors.toSet());

        // 3. 查询菜单详情
        List<SysMenuDO> menus = sysMenuMapper.selectBatchIds(menuIds);

        // 过滤出状态正常的菜单
        List<SysMenuDO> activeMenus = menus.stream()
                .filter(m -> m.getStatus() == 1)
                .sorted(Comparator.comparing(SysMenuDO::getSortOrder))
                .collect(Collectors.toList());

        return buildMenuTree(activeMenus, 0L);
    }

    private List<MenuTreeDTO> buildMenuTree(List<SysMenuDO> allMenus, Long parentId) {
        List<MenuTreeDTO> tree = new ArrayList<>();
        for (SysMenuDO menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                MenuTreeDTO node = new MenuTreeDTO(
                        menu.getId(),
                        menu.getParentId(),
                        menu.getMenuName(),
                        menu.getMenuType(),
                        menu.getPermissionCode(),
                        menu.getPath(),
                        menu.getIcon(),
                        menu.getSortOrder(),
                        buildMenuTree(allMenus, menu.getId())
                );
                tree.add(node);
            }
        }
        return tree;
    }
}

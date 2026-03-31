package com.quadra.system.adapter.out.persistence;

import com.quadra.system.adapter.out.persistence.entity.SysMenuDO;
import com.quadra.system.adapter.out.persistence.mapper.SysMenuMapper;
import com.quadra.system.application.port.out.MenuRepositoryPort;
import com.quadra.system.domain.model.SysMenu;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuRepositoryImpl implements MenuRepositoryPort {

    private final SysMenuMapper sysMenuMapper;

    public MenuRepositoryImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public Long nextId() {
        return System.currentTimeMillis();
    }

    @Override
    public void save(SysMenu menu) {
        SysMenuDO menuDO = toMenuDO(menu);
        sysMenuMapper.insert(menuDO);
    }

    @Override
    public void update(SysMenu menu) {
        SysMenuDO menuDO = toMenuDO(menu);
        sysMenuMapper.updateById(menuDO);
    }

    @Override
    public SysMenu findById(Long id) {
        SysMenuDO menuDO = sysMenuMapper.selectById(id);
        if (menuDO == null) {
            return null;
        }
        return toMenu(menuDO);
    }

    @Override
    public List<SysMenu> findAll() {
        List<SysMenuDO> list = sysMenuMapper.selectList(null);
        List<SysMenu> menus = new ArrayList<>();
        for (SysMenuDO menuDO : list) {
            menus.add(toMenu(menuDO));
        }
        return menus;
    }

    @Override
    public List<SysMenu> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysMenuDO> list = sysMenuMapper.selectBatchIds(ids);
        List<SysMenu> menus = new ArrayList<>();
        for (SysMenuDO menuDO : list) {
            menus.add(toMenu(menuDO));
        }
        return menus;
    }

    private SysMenuDO toMenuDO(SysMenu menu) {
        SysMenuDO menuDO = new SysMenuDO();
        menuDO.setId(menu.getId());
        menuDO.setParentId(menu.getParentId());
        menuDO.setMenuName(menu.getMenuName());
        menuDO.setMenuType(menu.getMenuType());
        menuDO.setPermissionCode(menu.getPermissionCode());
        menuDO.setPath(menu.getPath());
        menuDO.setIcon(menu.getIcon());
        menuDO.setSortOrder(menu.getSortOrder());
        menuDO.setStatus(menu.getStatus());
        return menuDO;
    }

    private SysMenu toMenu(SysMenuDO menuDO) {
        try {
            java.lang.reflect.Constructor<SysMenu> constructor = SysMenu.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SysMenu menu = constructor.newInstance();
            
            java.lang.reflect.Field idField = SysMenu.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(menu, menuDO.getId());

            java.lang.reflect.Field parentIdField = SysMenu.class.getDeclaredField("parentId");
            parentIdField.setAccessible(true);
            parentIdField.set(menu, menuDO.getParentId());

            java.lang.reflect.Field menuNameField = SysMenu.class.getDeclaredField("menuName");
            menuNameField.setAccessible(true);
            menuNameField.set(menu, menuDO.getMenuName());

            java.lang.reflect.Field menuTypeField = SysMenu.class.getDeclaredField("menuType");
            menuTypeField.setAccessible(true);
            menuTypeField.set(menu, menuDO.getMenuType());

            java.lang.reflect.Field permissionCodeField = SysMenu.class.getDeclaredField("permissionCode");
            permissionCodeField.setAccessible(true);
            permissionCodeField.set(menu, menuDO.getPermissionCode());

            java.lang.reflect.Field pathField = SysMenu.class.getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(menu, menuDO.getPath());

            java.lang.reflect.Field iconField = SysMenu.class.getDeclaredField("icon");
            iconField.setAccessible(true);
            iconField.set(menu, menuDO.getIcon());

            java.lang.reflect.Field sortOrderField = SysMenu.class.getDeclaredField("sortOrder");
            sortOrderField.setAccessible(true);
            sortOrderField.set(menu, menuDO.getSortOrder());

            java.lang.reflect.Field statusField = SysMenu.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(menu, menuDO.getStatus());

            return menu;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore SysMenu from DB", e);
        }
    }
}

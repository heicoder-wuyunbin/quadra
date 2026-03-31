package com.quadra.system.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.SysRoleDO;
import com.quadra.system.adapter.out.persistence.entity.SysRoleMenuDO;
import com.quadra.system.adapter.out.persistence.mapper.SysRoleMapper;
import com.quadra.system.adapter.out.persistence.mapper.SysRoleMenuMapper;
import com.quadra.system.application.port.out.RoleRepositoryPort;
import com.quadra.system.domain.model.SysRole;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepositoryPort {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public RoleRepositoryImpl(SysRoleMapper sysRoleMapper, SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public Long nextId() {
        return System.currentTimeMillis();
    }

    @Override
    public void save(SysRole role) {
        SysRoleDO roleDO = toRoleDO(role);
        sysRoleMapper.insert(roleDO);
    }

    @Override
    public void update(SysRole role) {
        SysRoleDO roleDO = toRoleDO(role);
        sysRoleMapper.updateById(roleDO);
    }

    @Override
    public SysRole findById(Long id) {
        SysRoleDO roleDO = sysRoleMapper.selectById(id);
        if (roleDO == null) {
            return null;
        }
        return toRole(roleDO);
    }

    @Override
    public SysRole findByRoleCode(String roleCode) {
        LambdaQueryWrapper<SysRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDO::getRoleCode, roleCode)
               .eq(SysRoleDO::getDeleted, 0);
        
        SysRoleDO roleDO = sysRoleMapper.selectOne(wrapper);
        if (roleDO == null) {
            return null;
        }
        return toRole(roleDO);
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDO::getRoleId, roleId);
        
        List<SysRoleMenuDO> list = sysRoleMenuMapper.selectList(wrapper);
        List<Long> menuIds = new ArrayList<>();
        for (SysRoleMenuDO rm : list) {
            menuIds.add(rm.getMenuId());
        }
        return menuIds;
    }

    @Override
    public void deleteRoleMenuRelation(Long roleId) {
        LambdaQueryWrapper<SysRoleMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDO::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);
    }

    @Override
    public void insertRoleMenuRelation(Long roleId, List<Long> menuIds) {
        for (Long menuId : menuIds) {
            SysRoleMenuDO rm = new SysRoleMenuDO();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            sysRoleMenuMapper.insert(rm);
        }
    }

    private SysRoleDO toRoleDO(SysRole role) {
        SysRoleDO roleDO = new SysRoleDO();
        roleDO.setId(role.getId());
        roleDO.setRoleCode(role.getRoleCode());
        roleDO.setRoleName(role.getRoleName());
        roleDO.setDescription(role.getDescription());
        roleDO.setStatus(role.getStatus());
        roleDO.setVersion(role.getVersion());
        roleDO.setDeleted(role.getDeleted());
        return roleDO;
    }

    private SysRole toRole(SysRoleDO roleDO) {
        try {
            java.lang.reflect.Constructor<SysRole> constructor = SysRole.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SysRole role = constructor.newInstance();
            
            java.lang.reflect.Field idField = SysRole.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(role, roleDO.getId());

            java.lang.reflect.Field roleCodeField = SysRole.class.getDeclaredField("roleCode");
            roleCodeField.setAccessible(true);
            roleCodeField.set(role, roleDO.getRoleCode());

            java.lang.reflect.Field roleNameField = SysRole.class.getDeclaredField("roleName");
            roleNameField.setAccessible(true);
            roleNameField.set(role, roleDO.getRoleName());

            java.lang.reflect.Field descriptionField = SysRole.class.getDeclaredField("description");
            descriptionField.setAccessible(true);
            descriptionField.set(role, roleDO.getDescription());

            java.lang.reflect.Field statusField = SysRole.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(role, roleDO.getStatus());

            java.lang.reflect.Field versionField = SysRole.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(role, roleDO.getVersion());

            java.lang.reflect.Field deletedField = SysRole.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(role, roleDO.getDeleted());

            return role;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore SysRole from DB", e);
        }
    }
}

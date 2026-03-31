package com.quadra.system.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.SysAdminDO;
import com.quadra.system.adapter.out.persistence.entity.SysAdminRoleDO;
import com.quadra.system.adapter.out.persistence.mapper.SysAdminMapper;
import com.quadra.system.adapter.out.persistence.mapper.SysAdminRoleMapper;
import com.quadra.system.application.port.out.AdminRepositoryPort;
import com.quadra.system.domain.model.SysAdmin;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminRepositoryImpl implements AdminRepositoryPort {

    private final SysAdminMapper sysAdminMapper;
    private final SysAdminRoleMapper sysAdminRoleMapper;

    public AdminRepositoryImpl(SysAdminMapper sysAdminMapper, SysAdminRoleMapper sysAdminRoleMapper) {
        this.sysAdminMapper = sysAdminMapper;
        this.sysAdminRoleMapper = sysAdminRoleMapper;
    }

    @Override
    public Long nextId() {
        return System.currentTimeMillis();
    }

    @Override
    public void save(SysAdmin admin) {
        SysAdminDO adminDO = toAdminDO(admin);
        sysAdminMapper.insert(adminDO);
    }

    @Override
    public void update(SysAdmin admin) {
        SysAdminDO adminDO = toAdminDO(admin);
        sysAdminMapper.updateById(adminDO);
    }

    @Override
    public void delete(SysAdmin admin) {
        SysAdminDO adminDO = toAdminDO(admin);
        adminDO.setDeleted(1);
        sysAdminMapper.updateById(adminDO);
    }

    @Override
    public SysAdmin findById(Long id) {
        SysAdminDO adminDO = sysAdminMapper.selectById(id);
        if (adminDO == null) {
            return null;
        }
        return toAdmin(adminDO);
    }

    @Override
    public SysAdmin findByUsername(String username) {
        LambdaQueryWrapper<SysAdminDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminDO::getUsername, username)
               .eq(SysAdminDO::getDeleted, 0);
        
        SysAdminDO adminDO = sysAdminMapper.selectOne(wrapper);
        if (adminDO == null) {
            return null;
        }
        return toAdmin(adminDO);
    }

    @Override
    public List<Long> findRoleIdsByAdminId(Long adminId) {
        LambdaQueryWrapper<SysAdminRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminRoleDO::getAdminId, adminId);
        
        List<SysAdminRoleDO> list = sysAdminRoleMapper.selectList(wrapper);
        List<Long> roleIds = new ArrayList<>();
        for (SysAdminRoleDO ar : list) {
            roleIds.add(ar.getRoleId());
        }
        return roleIds;
    }

    @Override
    public void deleteAdminRoleRelation(Long adminId) {
        LambdaQueryWrapper<SysAdminRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminRoleDO::getAdminId, adminId);
        sysAdminRoleMapper.delete(wrapper);
    }

    @Override
    public void insertAdminRoleRelation(Long adminId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysAdminRoleDO ar = new SysAdminRoleDO();
            ar.setAdminId(adminId);
            ar.setRoleId(roleId);
            sysAdminRoleMapper.insert(ar);
        }
    }

    private SysAdminDO toAdminDO(SysAdmin admin) {
        SysAdminDO adminDO = new SysAdminDO();
        adminDO.setId(admin.getId());
        adminDO.setUsername(admin.getUsername());
        adminDO.setPassword(admin.getPassword());
        adminDO.setRealName(admin.getRealName());
        adminDO.setAvatar(admin.getAvatar());
        adminDO.setStatus(admin.getStatus());
        adminDO.setVersion(admin.getVersion());
        adminDO.setDeleted(admin.getDeleted());
        return adminDO;
    }

    private SysAdmin toAdmin(SysAdminDO adminDO) {
        try {
            java.lang.reflect.Constructor<SysAdmin> constructor = SysAdmin.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SysAdmin admin = constructor.newInstance();
            
            java.lang.reflect.Field idField = SysAdmin.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(admin, adminDO.getId());

            java.lang.reflect.Field usernameField = SysAdmin.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(admin, adminDO.getUsername());

            java.lang.reflect.Field passwordField = SysAdmin.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(admin, adminDO.getPassword());

            java.lang.reflect.Field realNameField = SysAdmin.class.getDeclaredField("realName");
            realNameField.setAccessible(true);
            realNameField.set(admin, adminDO.getRealName());

            java.lang.reflect.Field avatarField = SysAdmin.class.getDeclaredField("avatar");
            avatarField.setAccessible(true);
            avatarField.set(admin, adminDO.getAvatar());

            java.lang.reflect.Field statusField = SysAdmin.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(admin, adminDO.getStatus());

            java.lang.reflect.Field versionField = SysAdmin.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(admin, adminDO.getVersion());

            java.lang.reflect.Field deletedField = SysAdmin.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(admin, adminDO.getDeleted());

            return admin;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore SysAdmin from DB", e);
        }
    }
}

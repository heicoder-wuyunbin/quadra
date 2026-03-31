package com.quadra.system.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.system.adapter.out.persistence.entity.SysAdminDO;
import com.quadra.system.adapter.out.persistence.entity.SysAdminRoleDO;
import com.quadra.system.adapter.out.persistence.mapper.SysAdminMapper;
import com.quadra.system.adapter.out.persistence.mapper.SysAdminRoleMapper;
import com.quadra.system.application.port.in.dto.AdminDTO;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.out.AdminQueryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminQueryRepositoryImpl implements AdminQueryPort {

    private final SysAdminMapper sysAdminMapper;
    private final SysAdminRoleMapper sysAdminRoleMapper;

    public AdminQueryRepositoryImpl(SysAdminMapper sysAdminMapper, SysAdminRoleMapper sysAdminRoleMapper) {
        this.sysAdminMapper = sysAdminMapper;
        this.sysAdminRoleMapper = sysAdminRoleMapper;
    }

    @Override
    public PageResult<AdminDTO> findAdmins(Integer status, int page, int size) {
        LambdaQueryWrapper<SysAdminDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminDO::getDeleted, 0);
        if (status != null) {
            wrapper.eq(SysAdminDO::getStatus, status);
        }
        wrapper.orderByDesc(SysAdminDO::getCreatedAt);

        Page<SysAdminDO> pageParam = new Page<>(page, size);
        Page<SysAdminDO> result = sysAdminMapper.selectPage(pageParam, wrapper);

        List<AdminDTO> records = new ArrayList<>();
        for (SysAdminDO adminDO : result.getRecords()) {
            List<Long> roleIds = findRoleIdsByAdminId(adminDO.getId());
            records.add(new AdminDTO(
                    adminDO.getId(),
                    adminDO.getUsername(),
                    adminDO.getRealName(),
                    adminDO.getAvatar(),
                    adminDO.getStatus(),
                    roleIds
            ));
        }

        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public AdminDTO findAdminById(Long id) {
        SysAdminDO adminDO = sysAdminMapper.selectById(id);
        if (adminDO == null) {
            return null;
        }
        List<Long> roleIds = findRoleIdsByAdminId(id);
        return new AdminDTO(
                adminDO.getId(),
                adminDO.getUsername(),
                adminDO.getRealName(),
                adminDO.getAvatar(),
                adminDO.getStatus(),
                roleIds
        );
    }

    private List<Long> findRoleIdsByAdminId(Long adminId) {
        LambdaQueryWrapper<SysAdminRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminRoleDO::getAdminId, adminId);
        List<SysAdminRoleDO> list = sysAdminRoleMapper.selectList(wrapper);
        List<Long> roleIds = new ArrayList<>();
        for (SysAdminRoleDO ar : list) {
            roleIds.add(ar.getRoleId());
        }
        return roleIds;
    }
}

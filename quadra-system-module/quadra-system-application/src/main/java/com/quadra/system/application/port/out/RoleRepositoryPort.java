package com.quadra.system.application.port.out;

import com.quadra.system.domain.model.SysRole;
import java.util.List;

public interface RoleRepositoryPort {
    Long nextId();
    void save(SysRole role);
    void update(SysRole role);
    SysRole findById(Long id);
    SysRole findByRoleCode(String roleCode);
    List<Long> findMenuIdsByRoleId(Long roleId);
    void deleteRoleMenuRelation(Long roleId);
    void insertRoleMenuRelation(Long roleId, List<Long> menuIds);
}

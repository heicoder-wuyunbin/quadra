package com.quadra.system.application.port.out;

import com.quadra.system.domain.model.SysAdmin;
import java.util.List;

public interface AdminRepositoryPort {
    Long nextId();
    void save(SysAdmin admin);
    void update(SysAdmin admin);
    SysAdmin findById(Long id);
    SysAdmin findByUsername(String username);
    List<Long> findRoleIdsByAdminId(Long adminId);
    void deleteAdminRoleRelation(Long adminId);
    void insertAdminRoleRelation(Long adminId, List<Long> roleIds);
}

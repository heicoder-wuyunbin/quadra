package com.quadra.system.application.port.out;

import com.quadra.system.domain.model.SysMenu;
import java.util.List;

public interface MenuRepositoryPort {
    Long nextId();
    void save(SysMenu menu);
    void update(SysMenu menu);
    SysMenu findById(Long id);
    List<SysMenu> findAll();
    List<SysMenu> findByIds(List<Long> ids);
}

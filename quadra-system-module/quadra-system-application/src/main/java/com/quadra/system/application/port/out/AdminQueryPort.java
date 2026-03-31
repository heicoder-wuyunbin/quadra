package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.dto.AdminDTO;
import com.quadra.system.application.port.in.dto.PageResult;

public interface AdminQueryPort {
    PageResult<AdminDTO> findAdmins(Integer status, int page, int size);
    AdminDTO findAdminById(Long id);
}

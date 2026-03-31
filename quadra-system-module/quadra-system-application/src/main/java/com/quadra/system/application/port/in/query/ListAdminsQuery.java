package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.AdminDTO;
import com.quadra.system.application.port.in.dto.PageResult;

public interface ListAdminsQuery {
    PageResult<AdminDTO> listAdmins(Integer status, int page, int size);
    AdminDTO getAdminById(Long id);
}

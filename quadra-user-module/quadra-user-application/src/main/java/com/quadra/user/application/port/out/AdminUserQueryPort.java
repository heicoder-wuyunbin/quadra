package com.quadra.user.application.port.out;

import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;

public interface AdminUserQueryPort {
    PageResult<AdminUserDTO> findUsers(String mobile, Integer status, int page, int size);
    AdminUserDetailDTO findUserDetail(Long id);
}

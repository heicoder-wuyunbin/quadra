package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;

public interface AdminUserQueryUseCase {
    PageResult<AdminUserDTO> listUsers(String mobile, Integer status, int page, int size);
    AdminUserDetailDTO getUserDetail(Long id);
}

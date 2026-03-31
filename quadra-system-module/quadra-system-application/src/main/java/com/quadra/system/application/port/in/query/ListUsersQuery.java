package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.UserAdminDTO;
import com.quadra.system.application.port.in.dto.UserDetailDTO;

public interface ListUsersQuery {
    PageResult<UserAdminDTO> listUsers(String mobile, Integer status, int page, int size);
    UserDetailDTO getUserDetailById(Long id);
}

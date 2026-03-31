package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.UserAdminDTO;
import com.quadra.system.application.port.in.dto.UserDetailDTO;

public interface UserQueryPort {
    PageResult<UserAdminDTO> findUsers(String mobile, Integer status, int page, int size);
    UserDetailDTO findUserDetailById(Long id);
}

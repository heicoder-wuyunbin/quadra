package com.quadra.user.application.port.out;

import com.quadra.user.application.port.in.dto.BlacklistItemDTO;
import com.quadra.user.application.port.in.dto.PageResult;

public interface BlacklistQueryPort {
    PageResult<BlacklistItemDTO> listByUserId(Long userId, int pageNo, int pageSize);
}

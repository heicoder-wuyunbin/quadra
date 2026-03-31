package com.quadra.user.application.port.in.query;

import com.quadra.user.application.port.in.dto.BlacklistItemDTO;
import com.quadra.user.application.port.in.dto.PageResult;

public interface ListMyBlacklistQuery {
    PageResult<BlacklistItemDTO> listBlacklist(Long userId, int pageNo, int pageSize);
}

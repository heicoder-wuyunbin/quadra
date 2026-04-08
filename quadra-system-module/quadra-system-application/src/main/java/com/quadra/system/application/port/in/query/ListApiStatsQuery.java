package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.ApiStatDTO;
import com.quadra.system.application.port.in.dto.PageResult;

public interface ListApiStatsQuery {

    PageResult<ApiStatDTO> list(
            String keyword,
            String method,
            int page,
            int size
    );
}


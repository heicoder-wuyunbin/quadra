package com.quadra.social.application.port.out;

import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.dto.VisitorDTO;

public interface VisitorQueryPort {
    /**
     * 查询访客列表（分页）
     */
    PageResult<VisitorDTO> findVisitors(Long userId, int pageNo, int pageSize);
}

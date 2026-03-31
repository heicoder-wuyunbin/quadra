package com.quadra.social.application.port.in.query;

import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.dto.VisitorDTO;

public interface ListVisitorsQuery {
    PageResult<VisitorDTO> listVisitors(Long userId, int pageNo, int pageSize);
}

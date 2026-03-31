package com.quadra.social.application.service;

import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.dto.VisitorDTO;
import com.quadra.social.application.port.in.query.ListVisitorsQuery;
import com.quadra.social.application.port.out.VisitorQueryPort;
import org.springframework.stereotype.Service;

@Service
public class VisitorQueryService implements ListVisitorsQuery {

    private final VisitorQueryPort visitorQueryPort;

    public VisitorQueryService(VisitorQueryPort visitorQueryPort) {
        this.visitorQueryPort = visitorQueryPort;
    }

    @Override
    public PageResult<VisitorDTO> listVisitors(Long userId, int pageNo, int pageSize) {
        return visitorQueryPort.findVisitors(userId, pageNo, pageSize);
    }
}

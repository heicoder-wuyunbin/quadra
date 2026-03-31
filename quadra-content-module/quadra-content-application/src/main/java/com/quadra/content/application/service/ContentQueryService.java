package com.quadra.content.application.service;

import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;
import com.quadra.content.application.port.in.query.PullMyTimelineQuery;
import com.quadra.content.application.port.out.TimelineQueryPort;
import org.springframework.stereotype.Service;

@Service
public class ContentQueryService implements PullMyTimelineQuery {

    private final TimelineQueryPort timelineQueryPort;

    public ContentQueryService(TimelineQueryPort timelineQueryPort) {
        this.timelineQueryPort = timelineQueryPort;
    }

    @Override
    public PageResult<TimelineItemDTO> pullTimeline(Long userId, int pageNo, int pageSize) {
        return timelineQueryPort.queryTimeline(userId, pageNo, pageSize);
    }
}

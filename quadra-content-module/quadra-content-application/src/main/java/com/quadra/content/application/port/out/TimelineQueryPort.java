package com.quadra.content.application.port.out;

import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;

/**
 * 时间线查询端口
 */
public interface TimelineQueryPort {
    /**
     * 查询用户时间线
     */
    PageResult<TimelineItemDTO> queryTimeline(Long userId, int pageNo, int pageSize);
}

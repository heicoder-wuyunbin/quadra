package com.quadra.content.application.port.in.query;

import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;

/**
 * 拉取我的时间线查询
 */
public interface PullMyTimelineQuery {
    /**
     * 查询用户的时间线
     * @param userId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 时间线分页结果
     */
    PageResult<TimelineItemDTO> pullTimeline(Long userId, int pageNo, int pageSize);
}

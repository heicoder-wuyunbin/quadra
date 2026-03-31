package com.quadra.content.application.port.out;

import java.util.List;

/**
 * 时间线仓储端口（写入 inbox）
 */
public interface TimelineRepositoryPort {
    /**
     * 批量写入粉丝收件箱
     * @param movementId 动态ID
     * @param authorId 发布者ID
     * @param followerIds 粉丝ID列表
     */
    void batchInsertInbox(Long movementId, Long authorId, List<Long> followerIds);
}

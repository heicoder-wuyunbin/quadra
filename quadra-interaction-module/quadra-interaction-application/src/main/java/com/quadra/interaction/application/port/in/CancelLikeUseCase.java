package com.quadra.interaction.application.port.in;

import com.quadra.interaction.domain.model.TargetType;

/**
 * 取消点赞用例
 */
public interface CancelLikeUseCase {
    /**
     * 取消点赞
     */
    void cancel(Long userId, TargetType targetType, Long targetId);
}

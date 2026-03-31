package com.quadra.interaction.application.port.in;

/**
 * 点赞目标用例
 */
public interface LikeTargetUseCase {
    /**
     * 对目标进行点赞
     * @return 互动ID
     */
    Long like(Long userId, com.quadra.interaction.domain.model.TargetType targetType, Long targetId);
}

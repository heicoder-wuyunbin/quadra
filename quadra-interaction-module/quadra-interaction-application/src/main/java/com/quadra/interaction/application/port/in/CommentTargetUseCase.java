package com.quadra.interaction.application.port.in;

import com.quadra.interaction.domain.model.TargetType;

/**
 * 评论目标用例
 */
public interface CommentTargetUseCase {
    /**
     * 对目标进行评论
     * @return 评论ID
     */
    Long comment(Long userId, TargetType targetType, Long targetId, String content, Long replyToId);
}

package com.quadra.interaction.application.port.in.command;

import com.quadra.interaction.domain.model.TargetType;

/**
 * 评论目标命令
 */
public record CommentTargetCommand(
    Long userId,
    TargetType targetType,
    Long targetId,
    String content,
    Long replyToId
) {}

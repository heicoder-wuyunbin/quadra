package com.quadra.interaction.application.port.in.command;

import com.quadra.interaction.domain.model.TargetType;

/**
 * 取消点赞命令
 */
public record CancelLikeCommand(
    Long userId,
    TargetType targetType,
    Long targetId
) {}

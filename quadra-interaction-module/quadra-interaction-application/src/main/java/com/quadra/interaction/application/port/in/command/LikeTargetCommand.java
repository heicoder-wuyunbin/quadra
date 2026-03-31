package com.quadra.interaction.application.port.in.command;

import com.quadra.interaction.domain.model.TargetType;

/**
 * 点赞目标命令
 */
public record LikeTargetCommand(
    Long userId,
    TargetType targetType,
    Long targetId
) {}

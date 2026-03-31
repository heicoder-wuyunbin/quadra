package com.quadra.content.application.port.in.command;

/**
 * 删除图文动态指令
 */
public record DeleteMovementCommand(
    Long movementId,
    Long userId
) {}

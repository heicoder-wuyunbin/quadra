package com.quadra.user.application.port.in.command;

public record UpdateSettingCommand(
    Long userId,
    Integer likeNotification,
    Integer commentNotification,
    Integer systemNotification
) {}

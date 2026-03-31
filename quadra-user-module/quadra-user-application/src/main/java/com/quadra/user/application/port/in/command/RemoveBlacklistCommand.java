package com.quadra.user.application.port.in.command;

public record RemoveBlacklistCommand(Long userId, Long targetUserId) {}

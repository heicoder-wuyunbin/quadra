package com.quadra.user.application.port.in.command;

public record AddBlacklistCommand(Long userId, Long targetUserId) {}

package com.quadra.social.application.port.in.command;

public record UnfollowUserCommand(Long userId, Long targetUserId) {
}

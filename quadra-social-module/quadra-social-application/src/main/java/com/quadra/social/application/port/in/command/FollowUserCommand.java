package com.quadra.social.application.port.in.command;

public record FollowUserCommand(Long userId, Long targetUserId) {
}

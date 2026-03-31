package com.quadra.social.application.port.in.command;

public record SwipeLikeCommand(Long userId, Long targetUserId, String likeType) {
}

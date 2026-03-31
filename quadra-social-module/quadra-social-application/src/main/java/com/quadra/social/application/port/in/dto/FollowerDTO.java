package com.quadra.social.application.port.in.dto;

import java.time.LocalDateTime;

public record FollowerDTO(
    Long userId,
    String nickname,
    String avatar,
    LocalDateTime followedAt
) {
}

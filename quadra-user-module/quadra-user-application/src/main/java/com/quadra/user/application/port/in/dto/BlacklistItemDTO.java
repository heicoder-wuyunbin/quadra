package com.quadra.user.application.port.in.dto;

import java.time.LocalDateTime;

public record BlacklistItemDTO(
        Long id,
        Long targetUserId,
        LocalDateTime createTime
) {
}

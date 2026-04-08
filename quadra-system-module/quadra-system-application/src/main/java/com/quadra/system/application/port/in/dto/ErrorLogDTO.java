package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

public record ErrorLogDTO(
        Long id,
        String level,
        String service,
        String message,
        String stackTrace,
        Long userId,
        String requestId,
        String url,
        String params,
        Boolean handled,
        String handledBy,
        LocalDateTime handledAt,
        LocalDateTime createdAt
) {
}

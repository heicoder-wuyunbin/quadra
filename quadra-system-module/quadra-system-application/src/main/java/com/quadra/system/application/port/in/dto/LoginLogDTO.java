package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

public record LoginLogDTO(
        Long id,
        Long adminId,
        String adminName,
        String ip,
        String location,
        String userAgent,
        String status,
        String reason,
        LocalDateTime createdAt
) {
}

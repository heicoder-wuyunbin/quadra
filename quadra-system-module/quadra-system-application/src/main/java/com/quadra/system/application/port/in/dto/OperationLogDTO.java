package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

public record OperationLogDTO(
        Long id,
        Long adminId,
        String adminName,
        String module,
        String action,
        Long targetId,
        Integer responseStatus,
        Integer executeTime,
        String ipAddress,
        String userAgent,
        String requestParams,
        LocalDateTime createdAt
) {
}

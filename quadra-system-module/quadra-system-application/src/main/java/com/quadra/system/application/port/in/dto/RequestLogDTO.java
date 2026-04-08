package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

public record RequestLogDTO(
        Long id,
        String service,
        String traceId,
        Long adminId,
        String method,
        String path,
        String queryString,
        Integer statusCode,
        Integer durationMs,
        String ipAddress,
        String userAgent,
        String requestHeaders,
        String requestBody,
        String responseBody,
        LocalDateTime createdAt
) {
}


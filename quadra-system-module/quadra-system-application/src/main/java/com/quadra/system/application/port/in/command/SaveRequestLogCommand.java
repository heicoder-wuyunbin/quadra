package com.quadra.system.application.port.in.command;

/**
 * 保存接口访问日志
 */
public record SaveRequestLogCommand(
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
        String responseBody
) {
}


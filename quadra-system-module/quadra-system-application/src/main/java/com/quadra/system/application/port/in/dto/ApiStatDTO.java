package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

/**
 * 接口统计（来自 sys_request_log 聚合）
 */
public record ApiStatDTO(
        String id,
        String method,
        String path,
        Long count,
        Long avgTime,
        Long p95Time,
        Double errorRate,
        LocalDateTime lastCalledAt
) {
}


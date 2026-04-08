package com.quadra.system.application.port.in.command;

public record SaveOperationLogCommand(
    Long adminId,
    String adminName,
    String module,
    String action,
    Long targetId,
    String ipAddress,
    String userAgent,
    String requestParams,
    Integer responseStatus,
    Integer executeTime
) {}

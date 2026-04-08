package com.quadra.system.application.port.in.command;

public record SaveLoginLogCommand(
    Long adminId,
    String adminName,
    String ip,
    String location,
    String userAgent,
    String status,
    String reason
) {}

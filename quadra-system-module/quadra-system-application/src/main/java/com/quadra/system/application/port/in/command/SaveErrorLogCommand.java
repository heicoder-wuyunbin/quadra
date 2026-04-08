package com.quadra.system.application.port.in.command;

public record SaveErrorLogCommand(
    String level,
    String service,
    String message,
    String stackTrace,
    Long userId,
    String requestId,
    String url,
    String params
) {}

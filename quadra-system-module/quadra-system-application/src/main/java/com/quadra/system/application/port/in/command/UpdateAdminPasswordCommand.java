package com.quadra.system.application.port.in.command;

public record UpdateAdminPasswordCommand(
    Long adminId,
    String newPassword
) {}

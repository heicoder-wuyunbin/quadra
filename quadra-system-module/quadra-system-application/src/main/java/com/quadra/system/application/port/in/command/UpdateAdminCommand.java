package com.quadra.system.application.port.in.command;

public record UpdateAdminCommand(
    Long adminId,
    String realName
) {}

package com.quadra.system.application.port.in.command;

public record CreateAdminCommand(
    String username,
    String password,
    String realName
) {}

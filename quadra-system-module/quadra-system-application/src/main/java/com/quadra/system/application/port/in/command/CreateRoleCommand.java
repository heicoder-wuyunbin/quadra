package com.quadra.system.application.port.in.command;

public record CreateRoleCommand(
    String roleCode,
    String roleName,
    String description
) {}

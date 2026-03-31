package com.quadra.system.application.port.in.command;

import java.util.List;

public record AssignRoleCommand(
    Long adminId,
    List<Long> roleIds
) {}

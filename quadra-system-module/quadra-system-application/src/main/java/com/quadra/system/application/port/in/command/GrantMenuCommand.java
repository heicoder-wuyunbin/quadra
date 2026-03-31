package com.quadra.system.application.port.in.command;

import java.util.List;

public record GrantMenuCommand(
    Long roleId,
    List<Long> menuIds
) {}

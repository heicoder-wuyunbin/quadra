package com.quadra.system.application.port.in.command;

public record CreateMenuCommand(
    Long parentId,
    String menuName,
    Integer menuType,
    String permissionCode,
    String path,
    String icon,
    Integer sortOrder
) {}

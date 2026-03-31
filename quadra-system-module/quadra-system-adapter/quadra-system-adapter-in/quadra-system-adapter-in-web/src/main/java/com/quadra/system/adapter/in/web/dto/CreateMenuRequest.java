package com.quadra.system.adapter.in.web.dto;

public record CreateMenuRequest(
    Long parentId,
    String menuName,
    Integer menuType,
    String permissionCode,
    String path,
    String icon,
    Integer sortOrder
) {}

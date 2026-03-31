package com.quadra.system.application.port.in.dto;

import java.util.List;

public record MenuTreeDTO(
    Long id,
    Long parentId,
    String menuName,
    Integer menuType,
    String permissionCode,
    String path,
    String icon,
    Integer sortOrder,
    List<MenuTreeDTO> children
) {}

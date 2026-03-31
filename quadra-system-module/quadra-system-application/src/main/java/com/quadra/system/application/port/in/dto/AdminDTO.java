package com.quadra.system.application.port.in.dto;

import java.util.List;

public record AdminDTO(
    Long id,
    String username,
    String realName,
    String avatar,
    Integer status,
    List<Long> roleIds
) {}

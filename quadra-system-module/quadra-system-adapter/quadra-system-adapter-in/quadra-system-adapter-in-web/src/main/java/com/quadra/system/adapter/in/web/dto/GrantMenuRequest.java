package com.quadra.system.adapter.in.web.dto;

import java.util.List;

public record GrantMenuRequest(
    Long roleId,
    List<Long> menuIds
) {}

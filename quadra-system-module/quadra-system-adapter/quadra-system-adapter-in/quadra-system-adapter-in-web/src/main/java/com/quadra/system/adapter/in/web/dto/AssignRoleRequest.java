package com.quadra.system.adapter.in.web.dto;

import java.util.List;

public record AssignRoleRequest(
    Long adminId,
    List<Long> roleIds
) {}

package com.quadra.system.adapter.in.web.dto;

public record CreateRoleRequest(
    String roleCode,
    String roleName,
    String description
) {}

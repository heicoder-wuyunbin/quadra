package com.quadra.system.adapter.in.web.dto;

public record CreateAdminRequest(
    String username,
    String password,
    String realName
) {}

package com.quadra.user.application.port.in.dto;

import java.time.LocalDateTime;

public record AdminUserDTO(
        String id,
        String mobile,
        String nickname,
        Integer gender,
        String city,
        Integer status,
        LocalDateTime createdAt
) {}

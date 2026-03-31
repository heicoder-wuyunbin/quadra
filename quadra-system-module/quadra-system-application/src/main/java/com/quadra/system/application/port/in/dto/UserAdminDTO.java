package com.quadra.system.application.port.in.dto;

import java.time.LocalDateTime;

public record UserAdminDTO(
        Long id,
        String mobile,
        String nickname,
        Integer gender,
        String city,
        Integer status,
        LocalDateTime createdAt
) {}

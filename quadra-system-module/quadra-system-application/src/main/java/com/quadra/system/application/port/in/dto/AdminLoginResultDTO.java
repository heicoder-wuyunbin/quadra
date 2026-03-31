package com.quadra.system.application.port.in.dto;

public record AdminLoginResultDTO(
    String accessToken,
    String refreshToken,
    Long adminId,
    String username,
    String realName
) {}

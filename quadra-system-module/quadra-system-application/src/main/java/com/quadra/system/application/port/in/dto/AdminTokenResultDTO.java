package com.quadra.system.application.port.in.dto;

public record AdminTokenResultDTO(
    String accessToken,
    String refreshToken,
    Long adminId
) {}

package com.quadra.user.application.port.in.dto;

public record LoginResultDTO(
    String accessToken,
    String refreshToken,
    String userId
) {}

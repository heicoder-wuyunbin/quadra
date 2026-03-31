package com.quadra.user.application.port.in;

public interface ParseAccessTokenUseCase {
    Long parseUserId(String accessToken);
}

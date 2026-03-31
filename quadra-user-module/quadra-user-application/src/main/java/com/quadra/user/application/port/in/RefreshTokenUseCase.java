package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.dto.LoginResultDTO;

public interface RefreshTokenUseCase {
    LoginResultDTO refresh(String refreshToken);
}

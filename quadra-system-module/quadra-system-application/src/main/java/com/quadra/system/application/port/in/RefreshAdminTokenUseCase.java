package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.dto.AdminTokenResultDTO;

public interface RefreshAdminTokenUseCase {
    AdminTokenResultDTO refresh(String refreshToken);
}

package com.quadra.system.application.port.in;

public interface ParseAdminTokenUseCase {
    Long parseAdminId(String accessToken);
}

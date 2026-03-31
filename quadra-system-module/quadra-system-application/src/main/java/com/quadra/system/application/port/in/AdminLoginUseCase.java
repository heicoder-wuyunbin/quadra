package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.AdminLoginCommand;
import com.quadra.system.application.port.in.dto.AdminLoginResultDTO;

public interface AdminLoginUseCase {
    AdminLoginResultDTO login(AdminLoginCommand command);
}

package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.LoginCommand;
import com.quadra.user.application.port.in.dto.LoginResultDTO;

public interface LoginUseCase {
    LoginResultDTO login(LoginCommand command);
}

package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    /**
     * 注册用户
     * @param command 注册指令
     * @return 注册成功的用户ID
     */
    Long register(RegisterUserCommand command);
}

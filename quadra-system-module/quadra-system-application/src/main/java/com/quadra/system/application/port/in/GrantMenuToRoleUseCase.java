package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.GrantMenuCommand;

public interface GrantMenuToRoleUseCase {
    void grantMenu(GrantMenuCommand command);
}

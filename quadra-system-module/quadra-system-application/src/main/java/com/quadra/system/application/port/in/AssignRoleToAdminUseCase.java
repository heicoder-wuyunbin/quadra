package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.AssignRoleCommand;

public interface AssignRoleToAdminUseCase {
    void assignRole(AssignRoleCommand command);
}

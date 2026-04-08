package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.SaveLoginLogCommand;

public interface SaveLoginLogUseCase {
    void save(SaveLoginLogCommand command);
}

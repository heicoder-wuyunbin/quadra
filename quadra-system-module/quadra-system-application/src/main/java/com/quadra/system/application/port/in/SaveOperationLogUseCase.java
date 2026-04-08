package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.SaveOperationLogCommand;

public interface SaveOperationLogUseCase {
    void save(SaveOperationLogCommand command);
}

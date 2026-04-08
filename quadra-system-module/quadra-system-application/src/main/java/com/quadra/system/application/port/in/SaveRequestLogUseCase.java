package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.SaveRequestLogCommand;

public interface SaveRequestLogUseCase {
    void save(SaveRequestLogCommand command);
}


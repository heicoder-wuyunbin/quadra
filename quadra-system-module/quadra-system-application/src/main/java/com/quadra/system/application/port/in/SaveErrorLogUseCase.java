package com.quadra.system.application.port.in;

import com.quadra.system.application.port.in.command.SaveErrorLogCommand;

public interface SaveErrorLogUseCase {
    void save(SaveErrorLogCommand command);
}

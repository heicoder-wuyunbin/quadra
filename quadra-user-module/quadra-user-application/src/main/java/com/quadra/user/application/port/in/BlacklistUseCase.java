package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.AddBlacklistCommand;
import com.quadra.user.application.port.in.command.RemoveBlacklistCommand;

public interface BlacklistUseCase {
    void addBlacklist(AddBlacklistCommand command);
    void removeBlacklist(RemoveBlacklistCommand command);
}

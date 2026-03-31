package com.quadra.user.application.service;

import com.quadra.user.application.port.in.BlacklistUseCase;
import com.quadra.user.application.port.in.command.AddBlacklistCommand;
import com.quadra.user.application.port.in.command.RemoveBlacklistCommand;
import com.quadra.user.application.port.out.BlacklistRepositoryPort;
import com.quadra.user.domain.exception.DomainException;
import com.quadra.user.domain.model.blacklist.UserBlacklist;
import org.springframework.stereotype.Service;

@Service
public class BlacklistApplicationService implements BlacklistUseCase {

    private final BlacklistRepositoryPort blacklistRepositoryPort;

    public BlacklistApplicationService(BlacklistRepositoryPort blacklistRepositoryPort) {
        this.blacklistRepositoryPort = blacklistRepositoryPort;
    }

    @Override
    public void addBlacklist(AddBlacklistCommand command) {
        if (blacklistRepositoryPort.exists(command.userId(), command.targetUserId())) {
            throw new DomainException("已经拉黑该用户");
        }

        Long id = blacklistRepositoryPort.nextId();
        UserBlacklist blacklist = UserBlacklist.create(id, command.userId(), command.targetUserId());
        
        blacklistRepositoryPort.save(blacklist);
    }

    @Override
    public void removeBlacklist(RemoveBlacklistCommand command) {
        blacklistRepositoryPort.remove(command.userId(), command.targetUserId());
    }
}

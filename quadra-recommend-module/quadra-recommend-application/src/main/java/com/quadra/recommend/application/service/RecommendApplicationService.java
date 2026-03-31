package com.quadra.recommend.application.service;

import com.quadra.recommend.application.port.in.RecordActionUseCase;
import com.quadra.recommend.application.port.in.command.RecordActionCommand;
import com.quadra.recommend.application.port.out.ActionRepositoryPort;
import com.quadra.recommend.domain.model.UserActionLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐应用服务
 * 实现记录用户行为用例
 */
@Service
public class RecommendApplicationService implements RecordActionUseCase {

    private final ActionRepositoryPort actionRepositoryPort;

    public RecommendApplicationService(ActionRepositoryPort actionRepositoryPort) {
        this.actionRepositoryPort = actionRepositoryPort;
    }

    @Override
    @Transactional
    public Long recordAction(RecordActionCommand command) {
        Long id = actionRepositoryPort.nextId();
        
        UserActionLog actionLog = UserActionLog.record(
            id,
            command.userId(),
            command.targetType(),
            command.targetId(),
            command.actionType(),
            command.weight()
        );
        
        actionRepositoryPort.save(actionLog);
        return id;
    }

    @Override
    @Transactional
    public void recordActions(List<RecordActionCommand> commands) {
        List<UserActionLog> actionLogs = commands.stream()
            .map(command -> {
                Long id = actionRepositoryPort.nextId();
                return UserActionLog.record(
                    id,
                    command.userId(),
                    command.targetType(),
                    command.targetId(),
                    command.actionType(),
                    command.weight()
                );
            })
            .collect(Collectors.toList());
        
        actionRepositoryPort.saveAll(actionLogs);
    }
}

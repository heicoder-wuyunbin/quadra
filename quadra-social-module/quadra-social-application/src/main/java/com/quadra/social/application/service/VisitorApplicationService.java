package com.quadra.social.application.service;

import com.quadra.social.application.port.in.RecordVisitorUseCase;
import com.quadra.social.application.port.in.command.RecordVisitorCommand;
import com.quadra.social.application.port.out.VisitorRepositoryPort;
import com.quadra.social.domain.model.UserVisitor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorApplicationService implements RecordVisitorUseCase {

    private final VisitorRepositoryPort visitorRepositoryPort;

    public VisitorApplicationService(VisitorRepositoryPort visitorRepositoryPort) {
        this.visitorRepositoryPort = visitorRepositoryPort;
    }

    @Override
    @Transactional
    public void recordVisitor(RecordVisitorCommand command) {
        // 生成ID
        Long id = visitorRepositoryPort.nextId();
        
        // 创建访客记录
        UserVisitor visitor = UserVisitor.record(
            id, 
            command.userId(), 
            command.visitorId(), 
            command.source(), 
            command.score()
        );
        
        // 保存
        visitorRepositoryPort.save(visitor);
    }
}

package com.quadra.social.application.service;

import com.quadra.social.application.port.in.SwipeLikeUseCase;
import com.quadra.social.application.port.in.command.SwipeLikeCommand;
import com.quadra.social.application.port.in.dto.MatchResultDTO;
import com.quadra.social.application.port.out.EventPublisherPort;
import com.quadra.social.application.port.out.MatchRepositoryPort;
import com.quadra.social.domain.event.DomainEvent;
import com.quadra.social.domain.model.UserMatchLike;
import com.quadra.social.domain.model.UserMatchLike.ActionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MatchApplicationService implements SwipeLikeUseCase {

    private final MatchRepositoryPort matchRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public MatchApplicationService(MatchRepositoryPort matchRepositoryPort, EventPublisherPort eventPublisherPort) {
        this.matchRepositoryPort = matchRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    @Transactional
    public MatchResultDTO swipe(SwipeLikeCommand command) {
        // 1. 解析行为类型
        ActionType actionType = "LIKE".equalsIgnoreCase(command.likeType()) 
            ? ActionType.LIKE : ActionType.DISLIKE;
        
        // 2. 创建滑动记录
        Long id = matchRepositoryPort.nextId();
        UserMatchLike matchLike = UserMatchLike.swipe(id, command.userId(), command.targetUserId(), actionType);
        
        // 3. 保存滑动记录
        matchRepositoryPort.save(matchLike);
        
        // 4. 如果是LIKE，检查对方是否也LIKE了自己
        if (actionType == ActionType.LIKE) {
            UserMatchLike targetLike = matchRepositoryPort.findTargetLikeUser(
                command.targetUserId(), command.userId());
            
            if (targetLike != null && targetLike.getActionType() == ActionType.LIKE && targetLike.getMatchTime() == null) {
                // 双方互相喜欢，匹配成功
                LocalDateTime matchTime = LocalDateTime.now();
                
                // 更新当前用户记录的matchTime
                matchLike.markAsMatched();
                matchRepositoryPort.update(matchLike);
                
                // 更新对方记录的matchTime
                targetLike.markAsMatched();
                matchRepositoryPort.update(targetLike);
                
                // 发布匹配事件到Outbox
                Long matchId = matchRepositoryPort.nextId();
                matchLike.publishMatchEvent(matchId);
                
                if (!matchLike.getDomainEvents().isEmpty()) {
                    eventPublisherPort.publish(matchLike.getDomainEvents());
                    matchLike.clearDomainEvents();
                }
                
                return MatchResultDTO.matched(matchId, command.targetUserId(), matchTime);
            }
        }
        
        return MatchResultDTO.notMatched();
    }
}

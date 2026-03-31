package com.quadra.content.application.service;

import com.quadra.content.application.port.in.DeleteMovementUseCase;
import com.quadra.content.application.port.in.PublishMovementUseCase;
import com.quadra.content.application.port.in.command.DeleteMovementCommand;
import com.quadra.content.application.port.in.command.PublishMovementCommand;
import com.quadra.content.application.port.out.EventPublisherPort;
import com.quadra.content.application.port.out.FollowerQueryPort;
import com.quadra.content.application.port.out.MovementRepositoryPort;
import com.quadra.content.application.port.out.TimelineRepositoryPort;
import com.quadra.content.domain.exception.DomainException;
import com.quadra.content.domain.model.Media;
import com.quadra.content.domain.model.Movement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;

@Service
public class ContentApplicationService implements PublishMovementUseCase, DeleteMovementUseCase {

    private final MovementRepositoryPort movementRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final TimelineRepositoryPort timelineRepositoryPort;
    private final FollowerQueryPort followerQueryPort;

    public ContentApplicationService(
            MovementRepositoryPort movementRepositoryPort,
            EventPublisherPort eventPublisherPort,
            TimelineRepositoryPort timelineRepositoryPort,
            FollowerQueryPort followerQueryPort) {
        this.movementRepositoryPort = movementRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.timelineRepositoryPort = timelineRepositoryPort;
        this.followerQueryPort = followerQueryPort;
    }

    @Override
    @Transactional
    public Long publishMovement(PublishMovementCommand command) {
        // 1. 获取全局唯一ID
        Long movementId = movementRepositoryPort.nextId();
        
        // 2. 转换媒体信息
        List<Media> medias = new ArrayList<>();
        if (command.medias() != null) {
            for (PublishMovementCommand.MediaInfo info : command.medias()) {
                if ("IMAGE".equals(info.type())) {
                    medias.add(Media.image(info.url(), info.thumbnail(), info.width(), info.height()));
                } else if ("VIDEO".equals(info.type())) {
                    medias.add(Media.video(info.url(), info.thumbnail(), info.width(), info.height()));
                }
            }
        }

        // 3. Domain 聚合根创建与校验，产生初始事件
        Movement movement = Movement.publish(
            movementId,
            command.userId(),
            command.textContent(),
            medias,
            command.state()
        );
        
        // 设置位置信息（可选）
        if (command.longitude() != null && command.latitude() != null) {
            movement.setLocation(command.longitude(), command.latitude(), command.locationName());
        }

        // 4. 持久化聚合根
        movementRepositoryPort.save(movement);

        // 5. 提取并持久化领域事件到 Outbox 表 (同事务)
        if (!movement.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(movement.getDomainEvents());
            movement.clearDomainEvents();
        }

        // 6. 扇出写入粉丝收件箱（推模式）
        // TODO: 应该异步执行，这里简化为同步调用
        try {
            List<Long> followerIds = followerQueryPort.getFollowerIds(command.userId());
            LinkedHashSet<Long> recipientIds = new LinkedHashSet<>();
            if (followerIds != null) {
                recipientIds.addAll(followerIds);
            }
            recipientIds.add(command.userId());
            if (!recipientIds.isEmpty()) {
                timelineRepositoryPort.batchInsertInbox(movementId, command.userId(), new ArrayList<>(recipientIds));
            }
        } catch (Exception e) {
            // 扇出失败不影响主流程，后续通过 Outbox 重试
        }

        return movementId;
    }

    @Override
    @Transactional
    public void deleteMovement(DeleteMovementCommand command) {
        // 1. 根据 ID 获取聚合根
        Movement movement = movementRepositoryPort.findById(command.movementId());
        if (movement == null) {
            throw new DomainException("动态不存在");
        }
        
        // 2. 校验权限
        if (!movement.getUserId().equals(command.userId())) {
            throw new DomainException("无权删除该动态");
        }

        // 3. 委托给领域聚合根处理业务逻辑
        movement.delete();

        // 4. 持久化更新
        movementRepositoryPort.update(movement);
    }
}

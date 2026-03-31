package com.quadra.interaction.application.service;

import com.quadra.interaction.application.port.in.CancelLikeUseCase;
import com.quadra.interaction.application.port.in.CommentTargetUseCase;
import com.quadra.interaction.application.port.in.DeleteCommentUseCase;
import com.quadra.interaction.application.port.in.LikeTargetUseCase;
import com.quadra.interaction.application.port.out.EventPublisherPort;
import com.quadra.interaction.application.port.out.InteractionRepositoryPort;
import com.quadra.interaction.domain.exception.DomainException;
import com.quadra.interaction.domain.model.Interaction;
import com.quadra.interaction.domain.model.TargetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionApplicationService implements LikeTargetUseCase, CancelLikeUseCase, CommentTargetUseCase, DeleteCommentUseCase {

    private final InteractionRepositoryPort interactionRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public InteractionApplicationService(
            InteractionRepositoryPort interactionRepositoryPort,
            EventPublisherPort eventPublisherPort) {
        this.interactionRepositoryPort = interactionRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    @Transactional
    public Long like(Long userId, TargetType targetType, Long targetId) {
        // 1. 检查是否已点赞（幂等检查在 Repository 层通过唯一索引保证）
        if (interactionRepositoryPort.existsLike(userId, targetType, targetId)) {
            throw new DomainException("已经点赞过了");
        }

        // 2. 获取全局唯一ID
        Long interactionId = interactionRepositoryPort.nextId();

        // 3. Domain 聚合根创建与校验
        Interaction interaction = Interaction.like(interactionId, userId, targetType, targetId);

        // 4. 持久化聚合根（Repository 层处理 DuplicateKeyException 幂等）
        interactionRepositoryPort.save(interaction);

        // 5. 提取并持久化领域事件到 Outbox 表
        if (!interaction.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(interaction.getDomainEvents());
            interaction.clearDomainEvents();
        }

        return interactionId;
    }

    @Override
    @Transactional
    public void cancel(Long userId, TargetType targetType, Long targetId) {
        // 1. 查找点赞记录
        Interaction interaction = interactionRepositoryPort.findLike(userId, targetType, targetId);
        if (interaction == null) {
            throw new DomainException("未找到点赞记录");
        }

        // 2. 执行取消点赞（领域逻辑）
        interaction.cancelLike();

        // 3. 更新持久化
        interactionRepositoryPort.cancelLike(userId, targetType, targetId);

        // 4. 发布取消事件
        if (!interaction.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(interaction.getDomainEvents());
            interaction.clearDomainEvents();
        }
    }

    @Override
    @Transactional
    public Long comment(Long userId, TargetType targetType, Long targetId, String content, Long replyToId) {
        // 1. 获取全局唯一ID
        Long interactionId = interactionRepositoryPort.nextId();

        // 2. Domain 聚合根创建与校验
        Interaction interaction = Interaction.comment(interactionId, userId, targetType, targetId, content, replyToId);

        // 3. 持久化聚合根
        interactionRepositoryPort.save(interaction);

        // 4. 提取并持久化领域事件到 Outbox 表
        if (!interaction.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(interaction.getDomainEvents());
            interaction.clearDomainEvents();
        }

        return interactionId;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Interaction interaction = interactionRepositoryPort.findById(commentId);
        if (interaction == null) {
            throw new DomainException("评论不存在");
        }
        if (!interaction.getUserId().equals(userId)) {
            throw new DomainException("无权删除该评论");
        }

        interaction.deleteComment();
        interactionRepositoryPort.update(interaction);

        if (!interaction.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(interaction.getDomainEvents());
            interaction.clearDomainEvents();
        }
    }
}

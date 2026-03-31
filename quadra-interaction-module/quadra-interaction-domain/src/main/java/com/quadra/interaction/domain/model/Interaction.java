package com.quadra.interaction.domain.model;

import com.quadra.interaction.domain.event.DomainEvent;
import com.quadra.interaction.domain.event.InteractionCreatedEvent;
import com.quadra.interaction.domain.event.InteractionCanceledEvent;
import com.quadra.interaction.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 互动聚合根
 * 统一行为模型：点赞(LIKE)和评论(COMMENT)共用一张表
 */
public class Interaction {
    private Long id;
    private Long userId;
    private Long targetId;
    private TargetType targetType;
    private ActionType actionType;
    private String content;        // 评论内容（点赞时为空）
    private Long replyToId;        // 回复的评论ID（二级评论）
    private Integer version;
    private Integer deleted;       // 逻辑删除：0-正常，1-已取消
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 领域事件容器
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // 禁用默认无参构造（外部禁止直接 new）
    private Interaction() {}

    /**
     * 工厂方法：点赞
     */
    public static Interaction like(Long id, Long userId, TargetType targetType, Long targetId) {
        if (id == null || id <= 0) {
            throw new DomainException("互动ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (targetType == null) {
            throw new DomainException("目标类型不能为空");
        }
        if (targetId == null || targetId <= 0) {
            throw new DomainException("目标ID必须有效");
        }

        Interaction interaction = new Interaction();
        interaction.id = id;
        interaction.userId = userId;
        interaction.targetId = targetId;
        interaction.targetType = targetType;
        interaction.actionType = ActionType.LIKE;
        interaction.content = null; // 点赞无内容
        interaction.replyToId = null;
        interaction.version = 0;
        interaction.deleted = 0;
        interaction.createdAt = LocalDateTime.now();
        interaction.updatedAt = interaction.createdAt;

        // 发布领域事件
        interaction.domainEvents.add(new InteractionCreatedEvent(
            id, userId, targetId, targetType, ActionType.LIKE
        ));

        return interaction;
    }

    /**
     * 工厂方法：评论
     */
    public static Interaction comment(Long id, Long userId, TargetType targetType, Long targetId, 
                                       String content, Long replyToId) {
        if (id == null || id <= 0) {
            throw new DomainException("互动ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (targetType == null) {
            throw new DomainException("目标类型不能为空");
        }
        if (targetId == null || targetId <= 0) {
            throw new DomainException("目标ID必须有效");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new DomainException("评论内容不能为空");
        }
        if (content.length() > 500) {
            throw new DomainException("评论内容不能超过500字");
        }

        Interaction interaction = new Interaction();
        interaction.id = id;
        interaction.userId = userId;
        interaction.targetId = targetId;
        interaction.targetType = targetType;
        interaction.actionType = ActionType.COMMENT;
        interaction.content = content.trim();
        interaction.replyToId = replyToId; // 可为空（一级评论）
        interaction.version = 0;
        interaction.deleted = 0;
        interaction.createdAt = LocalDateTime.now();
        interaction.updatedAt = interaction.createdAt;

        // 发布领域事件
        interaction.domainEvents.add(new InteractionCreatedEvent(
            id, userId, targetId, targetType, ActionType.COMMENT
        ));

        return interaction;
    }

    /**
     * 取消点赞（逻辑删除）
     */
    public void cancelLike() {
        if (this.actionType != ActionType.LIKE) {
            throw new DomainException("只有点赞操作可以被取消");
        }
        if (this.deleted == 1) {
            throw new DomainException("该点赞已被取消");
        }
        
        this.deleted = 1;
        this.updatedAt = LocalDateTime.now();
        
        // 发布取消事件
        this.domainEvents.add(new InteractionCanceledEvent(
            this.id, this.userId, this.targetId, this.targetType, this.actionType
        ));
    }

    /**
     * 删除评论（逻辑删除）
     */
    public void deleteComment() {
        if (this.actionType != ActionType.COMMENT) {
            throw new DomainException("只有评论操作可以被删除");
        }
        if (this.deleted == 1) {
            throw new DomainException("该评论已被删除");
        }
        
        this.deleted = 1;
        this.updatedAt = LocalDateTime.now();
        
        // 发布取消事件
        this.domainEvents.add(new InteractionCanceledEvent(
            this.id, this.userId, this.targetId, this.targetType, this.actionType
        ));
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTargetId() { return targetId; }
    public TargetType getTargetType() { return targetType; }
    public ActionType getActionType() { return actionType; }
    public String getContent() { return content; }
    public Long getReplyToId() { return replyToId; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<DomainEvent> getDomainEvents() { return domainEvents; }
}

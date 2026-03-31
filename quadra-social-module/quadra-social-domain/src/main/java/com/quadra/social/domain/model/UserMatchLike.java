package com.quadra.social.domain.model;

import com.quadra.social.domain.event.DomainEvent;
import com.quadra.social.domain.event.MatchCreatedEvent;
import com.quadra.social.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserMatchLike {
    private Long id;
    private Long userId;           // 操作人用户ID
    private Long targetUserId;     // 被评价人用户ID
    private ActionType actionType; // 行为类型
    private LocalDateTime matchTime; // 互相匹配成功的时间
    private Integer version;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 领域事件容器
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public enum ActionType {
        LIKE,    // 喜欢(右滑)
        DISLIKE  // 不喜欢(左滑)
    }

    private UserMatchLike() {}

    /**
     * 工厂方法：滑动操作
     */
    public static UserMatchLike swipe(Long id, Long userId, Long targetUserId, ActionType actionType) {
        if (userId == null || targetUserId == null) {
            throw new DomainException("用户ID或目标用户ID不能为空");
        }
        if (userId.equals(targetUserId)) {
            throw new DomainException("不能对自己进行滑动操作");
        }
        if (actionType == null) {
            throw new DomainException("行为类型不能为空");
        }

        UserMatchLike matchLike = new UserMatchLike();
        matchLike.id = id;
        matchLike.userId = userId;
        matchLike.targetUserId = targetUserId;
        matchLike.actionType = actionType;
        matchLike.version = 0;
        matchLike.deleted = 0;
        matchLike.createdAt = LocalDateTime.now();
        matchLike.updatedAt = LocalDateTime.now();
        return matchLike;
    }

    /**
     * 标记为匹配成功
     */
    public void markAsMatched() {
        this.matchTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 当双方互相喜欢时，发布匹配事件
     */
    public void publishMatchEvent(Long matchId) {
        this.domainEvents.add(new MatchCreatedEvent(matchId, this.userId, this.targetUserId));
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return domainEvents;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTargetUserId() { return targetUserId; }
    public ActionType getActionType() { return actionType; }
    public LocalDateTime getMatchTime() { return matchTime; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

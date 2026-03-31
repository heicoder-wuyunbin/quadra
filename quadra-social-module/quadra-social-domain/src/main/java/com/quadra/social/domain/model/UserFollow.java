package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import java.time.LocalDateTime;

public class UserFollow {
    private Long id;
    private Long userId;          // 粉丝(操作人)
    private Long targetUserId;    // 被关注的人(博主)
    private Integer version;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserFollow() {}

    /**
     * 工厂方法：关注用户
     */
    public static UserFollow follow(Long id, Long userId, Long targetUserId) {
        if (userId == null || targetUserId == null) {
            throw new DomainException("用户ID或目标用户ID不能为空");
        }
        if (userId.equals(targetUserId)) {
            throw new DomainException("不能关注自己");
        }

        UserFollow userFollow = new UserFollow();
        userFollow.id = id;
        userFollow.userId = userId;
        userFollow.targetUserId = targetUserId;
        userFollow.version = 0;
        userFollow.deleted = 0;
        userFollow.createdAt = LocalDateTime.now();
        userFollow.updatedAt = LocalDateTime.now();
        return userFollow;
    }

    /**
     * 取消关注
     */
    public void unfollow() {
        this.deleted = 1;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTargetUserId() { return targetUserId; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

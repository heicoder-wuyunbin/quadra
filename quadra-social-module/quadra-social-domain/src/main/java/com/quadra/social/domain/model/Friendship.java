package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import java.time.LocalDateTime;

public class Friendship {
    private Long id;
    private Long userId;       // 用户ID
    private Long friendId;     // 好友用户ID
    private Integer version;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Friendship() {}

    /**
     * 工厂方法：创建好友关系
     */
    public static Friendship create(Long id, Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new DomainException("用户ID或好友ID不能为空");
        }
        if (userId.equals(friendId)) {
            throw new DomainException("不能加自己为好友");
        }

        Friendship friendship = new Friendship();
        friendship.id = id;
        friendship.userId = userId;
        friendship.friendId = friendId;
        friendship.version = 0;
        friendship.deleted = 0;
        friendship.createdAt = LocalDateTime.now();
        friendship.updatedAt = LocalDateTime.now();
        return friendship;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getFriendId() { return friendId; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

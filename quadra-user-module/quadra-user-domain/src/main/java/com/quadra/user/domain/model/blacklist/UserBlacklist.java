package com.quadra.user.domain.model.blacklist;

import com.quadra.user.domain.exception.DomainException;
import java.time.LocalDateTime;

public class UserBlacklist {
    private Long id;
    private Long userId;
    private Long targetUserId;
    private LocalDateTime createTime;

    private UserBlacklist() {}

    public static UserBlacklist create(Long id, Long userId, Long targetUserId) {
        if (userId == null || targetUserId == null) {
            throw new DomainException("用户ID或目标用户ID不能为空");
        }
        if (userId.equals(targetUserId)) {
            throw new DomainException("不能拉黑自己");
        }

        UserBlacklist blacklist = new UserBlacklist();
        blacklist.id = id;
        blacklist.userId = userId;
        blacklist.targetUserId = targetUserId;
        blacklist.createTime = LocalDateTime.now();
        return blacklist;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getTargetUserId() { return targetUserId; }
    public LocalDateTime getCreateTime() { return createTime; }
}

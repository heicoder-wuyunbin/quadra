package com.quadra.content.domain.model;

import com.quadra.content.domain.exception.DomainException;

import java.time.LocalDateTime;

/**
 * 图文动态收件箱实体
 * 用于推模式时间线，记录用户收到的动态 ID 列表
 */
public class MovementInboxItem {
    private Long id;
    private Long userId;          // 收件人用户 ID
    private Long movementId;      // 动态 ID
    private Long publisherId;     // 发布者用户 ID
    private LocalDateTime inboxTime; // 入箱时间
    private Integer version;
    private Integer deleted;

    // 禁用默认无参构造
    private MovementInboxItem() {}

    /**
     * 工厂方法：创建收件箱项
     * @param id 主键 ID
     * @param userId 收件人用户 ID
     * @param movementId 动态 ID
     * @param publisherId 发布者用户 ID
     */
    public static MovementInboxItem create(Long id, Long userId, Long movementId, Long publisherId) {
        if (id == null || id <= 0) {
            throw new DomainException("收件箱项 ID 必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("收件人用户 ID 必须有效");
        }
        if (movementId == null || movementId <= 0) {
            throw new DomainException("动态 ID 必须有效");
        }
        if (publisherId == null || publisherId <= 0) {
            throw new DomainException("发布者用户 ID 必须有效");
        }

        MovementInboxItem item = new MovementInboxItem();
        item.id = id;
        item.userId = userId;
        item.movementId = movementId;
        item.publisherId = publisherId;
        item.inboxTime = LocalDateTime.now();
        item.version = 0;
        item.deleted = 0;

        return item;
    }

    /**
     * 逻辑删除收件箱项
     */
    public void delete() {
        if (this.deleted == 1) {
            throw new DomainException("收件箱项已被删除");
        }
        this.deleted = 1;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getMovementId() { return movementId; }
    public Long getPublisherId() { return publisherId; }
    public LocalDateTime getInboxTime() { return inboxTime; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
}

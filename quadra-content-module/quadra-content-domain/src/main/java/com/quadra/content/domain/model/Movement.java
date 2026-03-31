package com.quadra.content.domain.model;

import com.quadra.content.domain.event.DomainEvent;
import com.quadra.content.domain.event.MovementPublishedEvent;
import com.quadra.content.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 图文动态聚合根
 */
public class Movement {
    private Long id;
    private Long userId;
    private String textContent;
    private List<Media> medias;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String locationName;
    private Integer state; // 0-未审核，1-通过，2-驳回
    private Integer likeCount;
    private Integer commentCount;
    private Integer version;
    private Integer deleted;

    // 领域事件容器
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // 禁用默认无参构造（外部禁止直接 new）
    private Movement() {}

    /**
     * 工厂方法：发布图文动态
     * 校验：文本内容和媒体资源至少一项非空
     */
    public static Movement publish(Long id, Long userId, String textContent, List<Media> medias, Integer state) {
        // 校验ID
        if (id == null || id <= 0) {
            throw new DomainException("动态ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        
        // 校验内容：文本或媒体至少一项非空
        boolean hasText = textContent != null && !textContent.trim().isEmpty();
        boolean hasMedia = medias != null && !medias.isEmpty();
        if (!hasText && !hasMedia) {
            throw new DomainException("动态内容不能为空，文本或媒体至少需要一项");
        }

        Movement movement = new Movement();
        movement.id = id;
        movement.userId = userId;
        movement.textContent = textContent;
        movement.medias = medias != null ? new ArrayList<>(medias) : new ArrayList<>();
        movement.state = state != null ? state : 0; // 默认未审核
        movement.likeCount = 0;
        movement.commentCount = 0;
        movement.version = 0;
        movement.deleted = 0;

        // 发布领域事件
        movement.domainEvents.add(new MovementPublishedEvent(id, userId));

        return movement;
    }

    /**
     * 删除动态（逻辑删除）
     */
    public void delete() {
        if (this.deleted == 1) {
            throw new DomainException("动态已被删除");
        }
        this.deleted = 1;
    }

    /**
     * 更新审核状态
     */
    public void updateState(Integer newState) {
        if (this.state == 1 && newState != 1) {
            throw new DomainException("已通过的动态不能修改审核状态");
        }
        this.state = newState;
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTextContent() { return textContent; }
    public List<Media> getMedias() { return medias; }
    public BigDecimal getLongitude() { return longitude; }
    public BigDecimal getLatitude() { return latitude; }
    public String getLocationName() { return locationName; }
    public Integer getState() { return state; }
    public Integer getLikeCount() { return likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public List<DomainEvent> getDomainEvents() { return domainEvents; }

    // Setters for location (optional fields)
    public void setLocation(BigDecimal longitude, BigDecimal latitude, String locationName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationName = locationName;
    }
}

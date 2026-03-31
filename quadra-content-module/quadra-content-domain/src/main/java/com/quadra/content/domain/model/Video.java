package com.quadra.content.domain.model;

import com.quadra.content.domain.event.DomainEvent;
import com.quadra.content.domain.event.VideoPublishedEvent;
import com.quadra.content.domain.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

/**
 * 短视频聚合根
 */
public class Video {
    private Long id;
    private Long userId;
    private String textContent;
    private String videoUrl;
    private String coverUrl;
    private Integer duration;
    private Integer likeCount;
    private Integer commentCount;
    private Integer version;
    private Integer deleted;

    // 领域事件容器
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // 禁用默认无参构造（外部禁止直接 new）
    private Video() {}

    /**
     * 工厂方法：发布短视频
     */
    public static Video publish(Long id, Long userId, String videoUrl, String coverUrl, Integer duration, String description) {
        if (id == null || id <= 0) {
            throw new DomainException("视频ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            throw new DomainException("视频URL不能为空");
        }
        if (coverUrl == null || coverUrl.trim().isEmpty()) {
            throw new DomainException("封面URL不能为空");
        }

        Video video = new Video();
        video.id = id;
        video.userId = userId;
        video.videoUrl = videoUrl;
        video.coverUrl = coverUrl;
        video.duration = duration != null ? duration : 0;
        video.textContent = description;
        video.likeCount = 0;
        video.commentCount = 0;
        video.version = 0;
        video.deleted = 0;

        // 发布领域事件
        video.domainEvents.add(new VideoPublishedEvent(id, userId));

        return video;
    }

    /**
     * 删除视频（逻辑删除）
     */
    public void delete() {
        if (this.deleted == 1) {
            throw new DomainException("视频已被删除");
        }
        this.deleted = 1;
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTextContent() { return textContent; }
    public String getVideoUrl() { return videoUrl; }
    public String getCoverUrl() { return coverUrl; }
    public Integer getDuration() { return duration; }
    public Integer getLikeCount() { return likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public List<DomainEvent> getDomainEvents() { return domainEvents; }
}

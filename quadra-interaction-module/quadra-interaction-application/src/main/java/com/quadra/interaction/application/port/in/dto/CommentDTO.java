package com.quadra.interaction.application.port.in.dto;

import java.time.LocalDateTime;

/**
 * 评论DTO（读模型）
 */
public class CommentDTO {
    private Long id;
    private Long userId;
    private Long targetId;
    private String targetType;
    private String content;
    private Long replyToId;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

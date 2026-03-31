package com.quadra.user.domain.model.question;

import com.quadra.user.domain.exception.DomainException;
import java.time.LocalDateTime;

public class StrangerQuestion {
    private Long id;
    private Long userId;
    private String question;
    private Integer sortOrder;
    private Integer status; // 0-禁用, 1-启用
    private LocalDateTime createTime;
    
    private StrangerQuestion() {}

    public static StrangerQuestion create(Long id, Long userId, String question, Integer sortOrder) {
        if (question == null || question.trim().isEmpty()) {
            throw new DomainException("破冰问题不能为空");
        }
        if (question.length() > 50) {
            throw new DomainException("破冰问题不能超过50个字符");
        }

        StrangerQuestion sq = new StrangerQuestion();
        sq.id = id;
        sq.userId = userId;
        sq.question = question;
        sq.sortOrder = sortOrder != null ? sortOrder : 0;
        sq.status = 1;
        sq.createTime = LocalDateTime.now();
        return sq;
    }

    public void updateContent(String question, Integer sortOrder) {
        if (question != null && !question.trim().isEmpty()) {
            if (question.length() > 50) {
                throw new DomainException("破冰问题不能超过50个字符");
            }
            this.question = question;
        }
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }

    public void disable() {
        this.status = 0;
    }

    public void enable() {
        this.status = 1;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getQuestion() { return question; }
    public Integer getSortOrder() { return sortOrder; }
    public Integer getStatus() { return status; }
    public LocalDateTime getCreateTime() { return createTime; }
}

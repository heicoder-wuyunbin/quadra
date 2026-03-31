package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserVisitor {
    private Long id;
    private Long userId;         // 被访问主页的用户ID
    private Long visitorId;      // 访客用户ID
    private LocalDate visitDate; // 访问日期
    private LocalDateTime visitTime; // 具体访问时间
    private String source;       // 访问来源
    private BigDecimal score;    // 访客缘分得分
    private Integer version;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserVisitor() {}

    /**
     * 工厂方法：记录访客
     */
    public static UserVisitor record(Long id, Long userId, Long visitorId, String source, BigDecimal score) {
        if (userId == null || visitorId == null) {
            throw new DomainException("用户ID或访客ID不能为空");
        }
        if (userId.equals(visitorId)) {
            throw new DomainException("不能记录自己访问自己主页");
        }

        UserVisitor visitor = new UserVisitor();
        visitor.id = id;
        visitor.userId = userId;
        visitor.visitorId = visitorId;
        visitor.visitDate = LocalDate.now();
        visitor.visitTime = LocalDateTime.now();
        visitor.source = source;
        visitor.score = score != null ? score : BigDecimal.ZERO;
        visitor.version = 0;
        visitor.deleted = 0;
        visitor.createdAt = LocalDateTime.now();
        visitor.updatedAt = LocalDateTime.now();
        return visitor;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getVisitorId() { return visitorId; }
    public LocalDate getVisitDate() { return visitDate; }
    public LocalDateTime getVisitTime() { return visitTime; }
    public String getSource() { return source; }
    public BigDecimal getScore() { return score; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

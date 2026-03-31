package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 缘分推荐用户结果聚合根
 * 推荐引擎定时生成的缓存结果
 */
public class RecommendUser {
    
    private Long id;
    private Long userId;
    private Long recommendTargetId;
    private BigDecimal score;
    private LocalDate recommendDate;
    private Integer version;
    private LocalDateTime createdAt;

    // 禁用默认无参构造
    private RecommendUser() {}

    /**
     * 工厂方法：创建推荐用户结果
     */
    public static RecommendUser create(Long id, Long userId, Long recommendTargetId, 
                                        BigDecimal score, LocalDate recommendDate) {
        if (id == null || id <= 0) {
            throw new DomainException("推荐结果ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (recommendTargetId == null || recommendTargetId <= 0) {
            throw new DomainException("推荐目标ID必须有效");
        }
        if (score == null) {
            throw new DomainException("推荐得分不能为空");
        }
        if (recommendDate == null) {
            throw new DomainException("推荐日期不能为空");
        }

        RecommendUser result = new RecommendUser();
        result.id = id;
        result.userId = userId;
        result.recommendTargetId = recommendTargetId;
        result.score = score;
        result.recommendDate = recommendDate;
        result.version = 0;
        result.createdAt = LocalDateTime.now();

        return result;
    }

    /**
     * 更新推荐得分
     */
    public void updateScore(BigDecimal score) {
        if (score != null) {
            this.score = score;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRecommendTargetId() { return recommendTargetId; }
    public BigDecimal getScore() { return score; }
    public LocalDate getRecommendDate() { return recommendDate; }
    public Integer getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

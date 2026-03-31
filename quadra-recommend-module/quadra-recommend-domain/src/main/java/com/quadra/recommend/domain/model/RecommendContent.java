package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 内容推荐结果聚合根
 * 首页推荐时间线的计算结果
 */
public class RecommendContent {
    
    private Long id;
    private Long userId;
    private Long targetId;
    private TargetType targetType;
    private BigDecimal score;
    private Integer version;
    private LocalDateTime createdAt;

    // 禁用默认无参构造
    private RecommendContent() {}

    /**
     * 工厂方法：创建内容推荐结果
     */
    public static RecommendContent create(Long id, Long userId, Long targetId, 
                                          String targetType, BigDecimal score) {
        if (id == null || id <= 0) {
            throw new DomainException("推荐结果ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (targetId == null || targetId <= 0) {
            throw new DomainException("目标内容ID必须有效");
        }
        if (!TargetType.isValid(targetType)) {
            throw new DomainException("非法的目标类型: " + targetType);
        }
        if (score == null) {
            throw new DomainException("推荐得分不能为空");
        }

        RecommendContent result = new RecommendContent();
        result.id = id;
        result.userId = userId;
        result.targetId = targetId;
        result.targetType = TargetType.fromString(targetType);
        result.score = score;
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
    public Long getTargetId() { return targetId; }
    public TargetType getTargetType() { return targetType; }
    public BigDecimal getScore() { return score; }
    public Integer getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

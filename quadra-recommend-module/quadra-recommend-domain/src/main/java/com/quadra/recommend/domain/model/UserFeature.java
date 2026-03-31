package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户特征画像聚合根
 * 包含统计标签和供本地大模型计算的 Embedding 向量
 */
public class UserFeature {
    
    private Long id;
    private Long userId;
    private Integer activeScore;
    private Map<String, Object> tagsSummary;
    private String aiEmbedding; // JSON 格式的向量数据
    private Integer version;
    private LocalDateTime updatedAt;

    // 禁用默认无参构造
    private UserFeature() {}

    /**
     * 工厂方法：创建用户特征画像
     */
    public static UserFeature create(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new DomainException("特征ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }

        UserFeature feature = new UserFeature();
        feature.id = id;
        feature.userId = userId;
        feature.activeScore = 0;
        feature.version = 0;
        feature.updatedAt = LocalDateTime.now();

        return feature;
    }

    /**
     * 构建特征数据
     * @param activeScore 活跃度得分
     * @param tagsSummary 偏好标签分布
     * @param aiEmbedding AI 生成的稠密向量
     */
    public void build(Integer activeScore, Map<String, Object> tagsSummary, String aiEmbedding) {
        this.activeScore = activeScore != null ? activeScore : this.activeScore;
        this.tagsSummary = tagsSummary != null ? tagsSummary : this.tagsSummary;
        this.aiEmbedding = aiEmbedding != null ? aiEmbedding : this.aiEmbedding;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新活跃度得分
     */
    public void updateActiveScore(Integer score) {
        if (score != null) {
            this.activeScore = score;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新标签摘要
     */
    public void updateTagsSummary(Map<String, Object> tagsSummary) {
        if (tagsSummary != null) {
            this.tagsSummary = tagsSummary;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新 AI 向量
     */
    public void updateAiEmbedding(String aiEmbedding) {
        if (aiEmbedding != null) {
            this.aiEmbedding = aiEmbedding;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getActiveScore() { return activeScore; }
    public Map<String, Object> getTagsSummary() { return tagsSummary; }
    public String getAiEmbedding() { return aiEmbedding; }
    public Integer getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

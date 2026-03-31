package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 内容特征画像聚合根
 * 包含内容标签特征和 AI 向量
 */
public class ContentFeature {
    
    private Long id;
    private Long targetId;
    private TargetType targetType;
    private Integer heatScore;
    private Map<String, Object> tagsSummary;
    private String aiEmbedding; // JSON 格式的向量数据
    private Integer version;
    private LocalDateTime updatedAt;

    // 禁用默认无参构造
    private ContentFeature() {}

    /**
     * 工厂方法：创建内容特征画像
     */
    public static ContentFeature create(Long id, Long targetId, String targetType) {
        if (id == null || id <= 0) {
            throw new DomainException("特征ID必须有效");
        }
        if (targetId == null || targetId <= 0) {
            throw new DomainException("目标ID必须有效");
        }
        if (!TargetType.isValid(targetType)) {
            throw new DomainException("非法的目标类型: " + targetType);
        }

        ContentFeature feature = new ContentFeature();
        feature.id = id;
        feature.targetId = targetId;
        feature.targetType = TargetType.fromString(targetType);
        feature.heatScore = 0;
        feature.version = 0;
        feature.updatedAt = LocalDateTime.now();

        return feature;
    }

    /**
     * 构建特征数据
     * @param heatScore 热度得分
     * @param tagsSummary 内容标签特征
     * @param aiEmbedding AI 生成的稠密向量
     */
    public void build(Integer heatScore, Map<String, Object> tagsSummary, String aiEmbedding) {
        this.heatScore = heatScore != null ? heatScore : this.heatScore;
        this.tagsSummary = tagsSummary != null ? tagsSummary : this.tagsSummary;
        this.aiEmbedding = aiEmbedding != null ? aiEmbedding : this.aiEmbedding;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新热度得分
     */
    public void updateHeatScore(Integer score) {
        if (score != null) {
            this.heatScore = score;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getTargetId() { return targetId; }
    public TargetType getTargetType() { return targetType; }
    public Integer getHeatScore() { return heatScore; }
    public Map<String, Object> getTagsSummary() { return tagsSummary; }
    public String getAiEmbedding() { return aiEmbedding; }
    public Integer getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

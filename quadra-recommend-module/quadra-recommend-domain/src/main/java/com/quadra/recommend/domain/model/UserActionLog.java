package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为日志聚合根
 * 记录用户对目标的结构化行为，作为推荐系统的燃料
 */
public class UserActionLog {
    
    private Long id;
    private Long userId;
    private TargetType targetType;
    private Long targetId;
    private ActionType actionType;
    private BigDecimal weight;
    private Integer version;
    private LocalDateTime createdAt;

    // 禁用默认无参构造（外部禁止直接 new）
    private UserActionLog() {}

    /**
     * 工厂方法：记录用户行为
     * @param id 主键ID
     * @param userId 行为产生者用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param actionType 行为类型
     * @param weight 行为权重分数（可选，为null时使用默认权重）
     */
    public static UserActionLog record(Long id, Long userId, String targetType, Long targetId, 
                                       String actionType, BigDecimal weight) {
        // 参数校验
        if (id == null || id <= 0) {
            throw new DomainException("行为日志ID必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户ID必须有效");
        }
        if (targetId == null || targetId <= 0) {
            throw new DomainException("目标ID必须有效");
        }

        // 校验行为类型合法性
        if (!ActionType.isValid(actionType)) {
            throw new DomainException("非法的行为类型: " + actionType + "，合法类型为: VIEW, LIKE, SKIP, DISLIKE");
        }

        // 校验目标类型合法性
        if (!TargetType.isValid(targetType)) {
            throw new DomainException("非法的目标类型: " + targetType + "，合法类型为: USER, MOVEMENT, VIDEO");
        }

        UserActionLog log = new UserActionLog();
        log.id = id;
        log.userId = userId;
        log.targetType = TargetType.fromString(targetType);
        log.targetId = targetId;
        log.actionType = ActionType.fromString(actionType);
        
        // 权重处理：如果传入权重则使用传入值，否则使用默认权重
        if (weight != null) {
            log.weight = weight;
        } else {
            log.weight = BigDecimal.valueOf(log.actionType.getWeight());
        }
        
        log.version = 0;
        log.createdAt = LocalDateTime.now();

        return log;
    }

    /**
     * 工厂方法：记录用户行为（使用默认权重）
     */
    public static UserActionLog record(Long id, Long userId, String targetType, Long targetId, String actionType) {
        return record(id, userId, targetType, targetId, actionType, null);
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public TargetType getTargetType() { return targetType; }
    public Long getTargetId() { return targetId; }
    public ActionType getActionType() { return actionType; }
    public BigDecimal getWeight() { return weight; }
    public Integer getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

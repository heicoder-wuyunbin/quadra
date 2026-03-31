package com.quadra.recommend.domain.model;

/**
 * 行为类型枚举
 * 定义合法的用户行为类型及其权重分数
 */
public enum ActionType {
    VIEW(1.0, "浏览"),
    LIKE(5.0, "喜欢"),
    SKIP(-1.0, "跳过"),
    DISLIKE(-5.0, "不感兴趣");

    private final double weight;
    private final String description;

    ActionType(double weight, String description) {
        this.weight = weight;
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查行为类型是否合法
     */
    public static boolean isValid(String actionType) {
        if (actionType == null) {
            return false;
        }
        for (ActionType type : values()) {
            if (type.name().equals(actionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据名称获取枚举
     */
    public static ActionType fromString(String actionType) {
        for (ActionType type : values()) {
            if (type.name().equals(actionType)) {
                return type;
            }
        }
        return null;
    }
}

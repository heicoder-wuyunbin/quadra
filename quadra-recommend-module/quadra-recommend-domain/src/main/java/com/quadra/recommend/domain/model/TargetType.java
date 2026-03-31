package com.quadra.recommend.domain.model;

/**
 * 目标类型枚举
 * 定义推荐目标的内容类型
 */
public enum TargetType {
    USER("用户"),
    MOVEMENT("动态"),
    VIDEO("视频");

    private final String description;

    TargetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查目标类型是否合法
     */
    public static boolean isValid(String targetType) {
        if (targetType == null) {
            return false;
        }
        for (TargetType type : values()) {
            if (type.name().equals(targetType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据名称获取枚举
     */
    public static TargetType fromString(String targetType) {
        for (TargetType type : values()) {
            if (type.name().equals(targetType)) {
                return type;
            }
        }
        return null;
    }
}

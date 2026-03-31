package com.quadra.recommend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ActionType 枚举测试
 */
@DisplayName("ActionType 枚举测试")
class ActionTypeTest {

    @Test
    @DisplayName("验证有效的行为类型")
    void testValidActionTypes() {
        assertTrue(ActionType.isValid("VIEW"));
        assertTrue(ActionType.isValid("LIKE"));
        assertTrue(ActionType.isValid("SKIP"));
        assertTrue(ActionType.isValid("DISLIKE"));
    }

    @Test
    @DisplayName("验证无效的行为类型")
    void testInvalidActionType() {
        assertFalse(ActionType.isValid("INVALID"));
        assertFalse(ActionType.isValid(null));
    }

    @Test
    @DisplayName("从字符串创建行为类型")
    void testFromString() {
        assertEquals(ActionType.VIEW, ActionType.fromString("VIEW"));
        assertEquals(ActionType.LIKE, ActionType.fromString("LIKE"));
    }

    @Test
    @DisplayName("获取权重")
    void testGetWeight() {
        assertTrue(ActionType.VIEW.getWeight() > 0);
        assertTrue(ActionType.LIKE.getWeight() > ActionType.VIEW.getWeight());
    }
}

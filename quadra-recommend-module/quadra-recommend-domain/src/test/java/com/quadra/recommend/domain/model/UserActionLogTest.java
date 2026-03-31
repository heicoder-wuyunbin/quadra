package com.quadra.recommend.domain.model;

import com.quadra.recommend.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserActionLog 聚合根单元测试
 */
@DisplayName("UserActionLog 聚合根测试")
class UserActionLogTest {

    @Test
    @DisplayName("记录用户行为成功（默认权重）")
    void recordActionWithDefaultWeight() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        String targetType = "USER";
        Long targetId = 200L;
        String actionType = "VIEW";

        // When
        UserActionLog log = UserActionLog.record(id, userId, targetType, targetId, actionType);

        // Then
        assertNotNull(log);
        assertEquals(id, log.getId());
        assertEquals(userId, log.getUserId());
        assertEquals(TargetType.USER, log.getTargetType());
        assertEquals(ActionType.VIEW, log.getActionType());
        assertNotNull(log.getWeight());
    }

    @Test
    @DisplayName("记录用户行为成功（指定权重）")
    void recordActionWithCustomWeight() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        String targetType = "MOVEMENT";
        Long targetId = 200L;
        String actionType = "LIKE";
        BigDecimal weight = new BigDecimal("2.5");

        // When
        UserActionLog log = UserActionLog.record(id, userId, targetType, targetId, actionType, weight);

        // Then
        assertEquals(weight, log.getWeight());
    }

    @Test
    @DisplayName("记录用户行为 - 非法行为类型应抛出异常")
    void recordActionWithInvalidActionType() {
        // Then
        assertThrows(DomainException.class, 
            () -> UserActionLog.record(1L, 100L, "USER", 200L, "INVALID"));
    }

    @Test
    @DisplayName("记录用户行为 - 非法目标类型应抛出异常")
    void recordActionWithInvalidTargetType() {
        // Then
        assertThrows(DomainException.class, 
            () -> UserActionLog.record(1L, 100L, "INVALID", 200L, "VIEW"));
    }
}

package com.quadra.content.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MovementInboxItem 实体单元测试
 */
@DisplayName("MovementInboxItem 实体测试")
class MovementInboxItemTest {

    @Test
    @DisplayName("创建收件箱项成功")
    void createInboxItemSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        Long movementId = 200L;
        Long publisherId = 101L;

        // When
        MovementInboxItem item = MovementInboxItem.create(id, userId, movementId, publisherId);

        // Then
        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(userId, item.getUserId());
        assertEquals(movementId, item.getMovementId());
        assertEquals(publisherId, item.getPublisherId());
        assertEquals(0, item.getDeleted());
        assertNotNull(item.getInboxTime());
    }

    @Test
    @DisplayName("创建收件箱项 - ID 无效应抛出异常")
    void createInboxItemWithInvalidId() {
        // Then
        assertThrows(Exception.class, 
            () -> MovementInboxItem.create(null, 100L, 200L, 101L));
    }

    @Test
    @DisplayName("删除收件箱项成功")
    void deleteInboxItemSuccessfully() {
        // Given
        MovementInboxItem item = MovementInboxItem.create(1L, 100L, 200L, 101L);

        // When
        item.delete();

        // Then
        assertEquals(1, item.getDeleted());
    }
}

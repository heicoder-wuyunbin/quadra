package com.quadra.content.domain.model;

import com.quadra.content.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Movement 聚合根单元测试
 */
@DisplayName("Movement 聚合根测试")
class MovementTest {

    @Test
    @DisplayName("成功发布图文动态（仅文本）")
    void publishMovementWithTextOnly() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        String textContent = "这是我的第一条动态";

        // When
        Movement movement = Movement.publish(id, userId, textContent, null, 0);

        // Then
        assertNotNull(movement);
        assertEquals(id, movement.getId());
        assertEquals(userId, movement.getUserId());
        assertEquals(textContent, movement.getTextContent());
        assertEquals(0, movement.getState());
        assertEquals(0, movement.getLikeCount());
        assertEquals(0, movement.getCommentCount());
        assertEquals(0, movement.getVersion());
        assertEquals(0, movement.getDeleted());
        assertFalse(movement.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("成功发布图文动态（文本 + 图片）")
    void publishMovementWithTextAndImages() {
        // Given
        Long id = 2L;
        Long userId = 100L;
        String textContent = "分享美景";
        List<Media> medias = List.of(
            Media.image("https://example.com/img1.jpg", "https://example.com/thumb1.jpg", 1920, 1080),
            Media.image("https://example.com/img2.jpg", "https://example.com/thumb2.jpg", 1920, 1080)
        );

        // When
        Movement movement = Movement.publish(id, userId, textContent, medias, 1);

        // Then
        assertNotNull(movement);
        assertEquals(2, movement.getMedias().size());
        assertEquals("IMAGE", movement.getMedias().get(0).getType());
    }

    @Test
    @DisplayName("发布动态 - 内容为空应抛出异常")
    void publishMovementWithEmptyContent() {
        // Given
        Long id = 3L;
        Long userId = 100L;
        String textContent = "";
        List<Media> medias = null;

        // Then
        DomainException exception = assertThrows(
            DomainException.class,
            () -> Movement.publish(id, userId, textContent, medias, 0)
        );
        assertTrue(exception.getMessage().contains("内容不能为空"));
    }

    @Test
    @DisplayName("发布动态 - ID 无效应抛出异常")
    void publishMovementWithInvalidId() {
        // Given
        Long invalidId = null;
        Long userId = 100L;

        // Then
        assertThrows(DomainException.class, 
            () -> Movement.publish(invalidId, userId, "test", null, 0));
    }

    @Test
    @DisplayName("删除动态成功")
    void deleteMovementSuccessfully() {
        // Given
        Movement movement = Movement.publish(1L, 100L, "test", null, 0);

        // When
        movement.delete();

        // Then
        assertEquals(1, movement.getDeleted());
    }

    @Test
    @DisplayName("删除已删除的动态应抛出异常")
    void deleteAlreadyDeletedMovement() {
        // Given
        Movement movement = Movement.publish(1L, 100L, "test", null, 0);
        movement.delete();

        // Then
        assertThrows(DomainException.class, () -> movement.delete());
    }

    @Test
    @DisplayName("更新审核状态成功")
    void updateStateSuccessfully() {
        // Given
        Movement movement = Movement.publish(1L, 100L, "test", null, 0);

        // When
        movement.updateState(1);

        // Then
        assertEquals(1, movement.getState());
    }

    @Test
    @DisplayName("已通过的动态不能修改审核状态")
    void updateStateOfApprovedMovement() {
        // Given
        Movement movement = Movement.publish(1L, 100L, "test", null, 1);

        // Then
        assertThrows(DomainException.class, () -> movement.updateState(0));
    }

    @Test
    @DisplayName("设置位置信息")
    void setLocationSuccessfully() {
        // Given
        Movement movement = Movement.publish(1L, 100L, "test", null, 0);

        // When
        movement.setLocation(new java.math.BigDecimal("116.4074"), 
                           new java.math.BigDecimal("39.9042"), 
                           "北京市");

        // Then
        assertEquals(new java.math.BigDecimal("116.4074"), movement.getLongitude());
        assertEquals(new java.math.BigDecimal("39.9042"), movement.getLatitude());
        assertEquals("北京市", movement.getLocationName());
    }
}

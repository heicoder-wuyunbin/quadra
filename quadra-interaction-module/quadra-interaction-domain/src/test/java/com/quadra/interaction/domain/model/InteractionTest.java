package com.quadra.interaction.domain.model;

import com.quadra.interaction.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Interaction 聚合根单元测试
 */
@DisplayName("Interaction 聚合根测试")
class InteractionTest {

    @Test
    @DisplayName("点赞成功")
    void likeSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        TargetType targetType = TargetType.MOVEMENT;
        Long targetId = 200L;

        // When
        Interaction interaction = Interaction.like(id, userId, targetType, targetId);

        // Then
        assertNotNull(interaction);
        assertEquals(id, interaction.getId());
        assertEquals(userId, interaction.getUserId());
        assertEquals(targetId, interaction.getTargetId());
        assertEquals(targetType, interaction.getTargetType());
        assertEquals(ActionType.LIKE, interaction.getActionType());
        assertNull(interaction.getContent());
        assertEquals(0, interaction.getDeleted());
        assertFalse(interaction.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("评论成功")
    void commentSuccessfully() {
        // Given
        Long id = 2L;
        Long userId = 100L;
        TargetType targetType = TargetType.VIDEO;
        Long targetId = 200L;
        String content = "很好的视频";
        Long replyToId = null;

        // When
        Interaction interaction = Interaction.comment(id, userId, targetType, targetId, content, replyToId);

        // Then
        assertNotNull(interaction);
        assertEquals(ActionType.COMMENT, interaction.getActionType());
        assertEquals(content, interaction.getContent());
        assertFalse(interaction.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("评论 - 内容为空应抛出异常")
    void commentWithEmptyContent() {
        // Then
        assertThrows(DomainException.class, 
            () -> Interaction.comment(1L, 100L, TargetType.MOVEMENT, 200L, "", null));
    }

    @Test
    @DisplayName("取消点赞成功")
    void cancelLikeSuccessfully() {
        // Given
        Interaction interaction = Interaction.like(1L, 100L, TargetType.MOVEMENT, 200L);

        // When
        interaction.cancelLike();

        // Then
        assertEquals(1, interaction.getDeleted());
        assertFalse(interaction.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("删除评论成功")
    void deleteCommentSuccessfully() {
        // Given
        Interaction interaction = Interaction.comment(1L, 100L, TargetType.MOVEMENT, 200L, "test", null);

        // When
        interaction.deleteComment();

        // Then
        assertEquals(1, interaction.getDeleted());
    }

    @Test
    @DisplayName("非点赞操作不能取消点赞")
    void cancelLikeOnComment() {
        // Given
        Interaction interaction = Interaction.comment(1L, 100L, TargetType.MOVEMENT, 200L, "test", null);

        // Then
        assertThrows(DomainException.class, () -> interaction.cancelLike());
    }

    @Test
    @DisplayName("非评论操作不能删除评论")
    void deleteCommentOnLike() {
        // Given
        Interaction interaction = Interaction.like(1L, 100L, TargetType.MOVEMENT, 200L);

        // Then
        assertThrows(DomainException.class, () -> interaction.deleteComment());
    }
}

package com.quadra.interaction.application.service;

import com.quadra.interaction.application.port.in.CancelLikeUseCase;
import com.quadra.interaction.application.port.in.CommentTargetUseCase;
import com.quadra.interaction.application.port.in.DeleteCommentUseCase;
import com.quadra.interaction.application.port.in.LikeTargetUseCase;
import com.quadra.interaction.application.port.out.EventPublisherPort;
import com.quadra.interaction.application.port.out.InteractionRepositoryPort;
import com.quadra.interaction.domain.exception.DomainException;
import com.quadra.interaction.domain.model.Interaction;
import com.quadra.interaction.domain.model.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * InteractionApplicationService 单元测试
 */
@DisplayName("InteractionApplicationService 测试")
class InteractionApplicationServiceTest {

    @Mock
    private InteractionRepositoryPort interactionRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    private InteractionApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new InteractionApplicationService(interactionRepositoryPort, eventPublisherPort);
    }

    @Test
    @DisplayName("点赞成功")
    void likeSuccessfully() {
        // Given
        Long userId = 100L;
        Long targetId = 200L;
        TargetType targetType = TargetType.MOVEMENT;
        Long interactionId = 1L;

        when(interactionRepositoryPort.existsLike(userId, targetType, targetId)).thenReturn(false);
        when(interactionRepositoryPort.nextId()).thenReturn(interactionId);
        doNothing().when(interactionRepositoryPort).save(any(Interaction.class));

        // When
        Long result = service.like(userId, targetType, targetId);

        // Then
        assertEquals(interactionId, result);
        verify(interactionRepositoryPort).save(any(Interaction.class));
        verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("重复点赞应抛出异常")
    void likeAlreadyLiked() {
        // Given
        Long userId = 100L;
        Long targetId = 200L;
        TargetType targetType = TargetType.MOVEMENT;

        when(interactionRepositoryPort.existsLike(userId, targetType, targetId)).thenReturn(true);

        // Then
        assertThrows(DomainException.class, () -> service.like(userId, targetType, targetId));
    }

    @Test
    @DisplayName("取消点赞成功")
    void cancelLikeSuccessfully() {
        // Given
        Long userId = 100L;
        Long targetId = 200L;
        TargetType targetType = TargetType.MOVEMENT;
        Interaction interaction = Interaction.like(1L, userId, targetType, targetId);

        when(interactionRepositoryPort.findLike(userId, targetType, targetId)).thenReturn(interaction);

        // When
        service.cancel(userId, targetType, targetId);

        // Then
        verify(interactionRepositoryPort).cancelLike(userId, targetType, targetId);
        verify(eventPublisherPort).publish(any());
        assertEquals(1, interaction.getDeleted());
    }

    @Test
    @DisplayName("取消点赞 - 未找到记录应抛出异常")
    void cancelLikeNotFound() {
        // Given
        Long userId = 100L;
        Long targetId = 200L;
        TargetType targetType = TargetType.MOVEMENT;

        when(interactionRepositoryPort.findLike(userId, targetType, targetId)).thenReturn(null);

        // Then
        assertThrows(DomainException.class, () -> service.cancel(userId, targetType, targetId));
    }

    @Test
    @DisplayName("评论成功")
    void commentSuccessfully() {
        // Given
        Long userId = 100L;
        Long targetId = 200L;
        TargetType targetType = TargetType.MOVEMENT;
        String content = "测试评论";
        Long interactionId = 1L;

        when(interactionRepositoryPort.nextId()).thenReturn(interactionId);
        doNothing().when(interactionRepositoryPort).save(any(Interaction.class));

        // When
        Long result = service.comment(userId, targetType, targetId, content, null);

        // Then
        assertEquals(interactionId, result);
        verify(interactionRepositoryPort).save(any(Interaction.class));
        verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("删除评论成功")
    void deleteCommentSuccessfully() {
        // Given
        Long userId = 100L;
        Long commentId = 1L;
        Interaction interaction = Interaction.comment(commentId, userId, TargetType.MOVEMENT, 200L, "test", null);

        when(interactionRepositoryPort.findById(commentId)).thenReturn(interaction);

        // When
        service.deleteComment(userId, commentId);

        // Then
        verify(interactionRepositoryPort).update(interaction);
        assertEquals(1, interaction.getDeleted());
    }

    @Test
    @DisplayName("删除评论 - 无权删除应抛出异常")
    void deleteCommentUnauthorized() {
        // Given
        Long userId = 100L;
        Long ownerId = 200L;
        Long commentId = 1L;
        Interaction interaction = Interaction.comment(commentId, ownerId, TargetType.MOVEMENT, 300L, "test", null);

        when(interactionRepositoryPort.findById(commentId)).thenReturn(interaction);

        // Then
        assertThrows(DomainException.class, () -> service.deleteComment(userId, commentId));
    }
}

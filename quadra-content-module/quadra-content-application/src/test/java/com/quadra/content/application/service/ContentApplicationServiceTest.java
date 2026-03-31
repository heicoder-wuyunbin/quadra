package com.quadra.content.application.service;

import com.quadra.content.application.port.in.command.DeleteMovementCommand;
import com.quadra.content.application.port.in.command.PublishMovementCommand;
import com.quadra.content.application.port.out.EventPublisherPort;
import com.quadra.content.application.port.out.FollowerQueryPort;
import com.quadra.content.application.port.out.MovementRepositoryPort;
import com.quadra.content.application.port.out.TimelineRepositoryPort;
import com.quadra.content.domain.exception.DomainException;
import com.quadra.content.domain.model.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ContentApplicationService 单元测试
 */
@DisplayName("ContentApplicationService 测试")
class ContentApplicationServiceTest {

    @Mock
    private MovementRepositoryPort movementRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private TimelineRepositoryPort timelineRepositoryPort;

    @Mock
    private FollowerQueryPort followerQueryPort;

    private ContentApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ContentApplicationService(
            movementRepositoryPort,
            eventPublisherPort,
            timelineRepositoryPort,
            followerQueryPort
        );
    }

    @Test
    @DisplayName("发布图文动态成功")
    void publishMovementSuccessfully() {
        // Given
        Long userId = 100L;
        Long movementId = 1L;
        String textContent = "测试动态";

        PublishMovementCommand command = new PublishMovementCommand(
            userId,
            textContent,
            null,
            null,
            null,
            null,
            0
        );

        when(movementRepositoryPort.nextId()).thenReturn(movementId);
        doNothing().when(movementRepositoryPort).save(any(Movement.class));
        when(followerQueryPort.getFollowerIds(userId)).thenReturn(List.of(200L, 300L));

        // When
        Long result = service.publishMovement(command);

        // Then
        assertEquals(movementId, result);
        verify(movementRepositoryPort).save(any(Movement.class));
        verify(eventPublisherPort).publish(any());
        verify(timelineRepositoryPort).batchInsertInbox(
            eq(movementId), eq(userId), any(List.class)
        );
    }

    @Test
    @DisplayName("发布动态 - 扇出失败不影响主流程")
    void publishMovementWithFanoutFailure() {
        // Given
        Long userId = 100L;
        Long movementId = 1L;

        PublishMovementCommand command = new PublishMovementCommand(
            userId,
            "test",
            null,
            null,
            null,
            null,
            0
        );

        when(movementRepositoryPort.nextId()).thenReturn(movementId);
        doNothing().when(movementRepositoryPort).save(any(Movement.class));
        when(followerQueryPort.getFollowerIds(userId)).thenThrow(new RuntimeException("DB error"));

        // When
        Long result = service.publishMovement(command);

        // Then
        assertEquals(movementId, result);
        verify(movementRepositoryPort).save(any(Movement.class));
        verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("删除动态成功")
    void deleteMovementSuccessfully() {
        // Given
        Long movementId = 1L;
        Long userId = 100L;
        Movement movement = Movement.publish(movementId, userId, "test", null, 0);

        DeleteMovementCommand command = new DeleteMovementCommand(movementId, userId);

        when(movementRepositoryPort.findById(movementId)).thenReturn(movement);

        // When
        service.deleteMovement(command);

        // Then
        verify(movementRepositoryPort).update(movement);
        assertEquals(1, movement.getDeleted());
    }

    @Test
    @DisplayName("删除动态 - 动态不存在应抛出异常")
    void deleteMovementNotFound() {
        // Given
        Long movementId = 1L;
        Long userId = 100L;
        DeleteMovementCommand command = new DeleteMovementCommand(movementId, userId);

        when(movementRepositoryPort.findById(movementId)).thenReturn(null);

        // Then
        assertThrows(DomainException.class, () -> service.deleteMovement(command));
    }

    @Test
    @DisplayName("删除动态 - 无权删除应抛出异常")
    void deleteMovementUnauthorized() {
        // Given
        Long movementId = 1L;
        Long ownerId = 100L;
        Long otherUserId = 200L;
        Movement movement = Movement.publish(movementId, ownerId, "test", null, 0);

        DeleteMovementCommand command = new DeleteMovementCommand(movementId, otherUserId);

        when(movementRepositoryPort.findById(movementId)).thenReturn(movement);

        // Then
        assertThrows(DomainException.class, () -> service.deleteMovement(command));
    }
}

package com.quadra.recommend.application.service;

import com.quadra.recommend.application.port.in.RecordActionUseCase;
import com.quadra.recommend.application.port.in.command.RecordActionCommand;
import com.quadra.recommend.application.port.out.ActionRepositoryPort;
import com.quadra.recommend.domain.model.UserActionLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RecommendApplicationService 单元测试
 */
@DisplayName("RecommendApplicationService 测试")
class RecommendApplicationServiceTest {

    @Mock
    private ActionRepositoryPort actionRepositoryPort;

    private RecommendApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RecommendApplicationService(actionRepositoryPort);
    }

    @Test
    @DisplayName("记录用户行为成功")
    void recordActionSuccessfully() {
        // Given
        Long userId = 100L;
        String targetType = "USER";
        Long targetId = 200L;
        String actionType = "VIEW";
        BigDecimal weight = new BigDecimal("1.0");
        Long actionId = 1L;

        RecordActionCommand command = new RecordActionCommand(
            userId, targetType, targetId, actionType, weight
        );

        when(actionRepositoryPort.nextId()).thenReturn(actionId);
        doNothing().when(actionRepositoryPort).save(any(UserActionLog.class));

        // When
        Long result = service.recordAction(command);

        // Then
        assertEquals(actionId, result);
        verify(actionRepositoryPort).save(any(UserActionLog.class));
    }

    @Test
    @DisplayName("批量记录用户行为成功")
    void recordActionsSuccessfully() {
        // Given
        Long userId = 100L;
        List<RecordActionCommand> commands = List.of(
            new RecordActionCommand(userId, "USER", 200L, "VIEW", new BigDecimal("1.0")),
            new RecordActionCommand(userId, "MOVEMENT", 300L, "LIKE", new BigDecimal("2.0"))
        );

        when(actionRepositoryPort.nextId()).thenReturn(1L, 2L);
        doNothing().when(actionRepositoryPort).saveAll(any(List.class));

        // When
        service.recordActions(commands);

        // Then
        verify(actionRepositoryPort, times(2)).nextId();
        verify(actionRepositoryPort).saveAll(any(List.class));
    }
}

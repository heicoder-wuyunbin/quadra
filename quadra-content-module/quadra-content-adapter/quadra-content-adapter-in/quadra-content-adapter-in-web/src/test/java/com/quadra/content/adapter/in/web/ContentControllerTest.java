package com.quadra.content.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.content.adapter.in.web.dto.PublishMovementRequest;
import com.quadra.content.application.port.in.DeleteMovementUseCase;
import com.quadra.content.application.port.in.PublishMovementUseCase;
import com.quadra.content.application.port.in.PublishVideoUseCase;
import com.quadra.content.application.port.in.command.DeleteMovementCommand;
import com.quadra.content.application.port.in.command.PublishMovementCommand;
import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;
import com.quadra.content.application.port.in.query.PullMyTimelineQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ContentController 单元测试
 */
@WebMvcTest(ContentController.class)
@DisplayName("ContentController 测试")
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PublishMovementUseCase publishMovementUseCase;

    @MockBean
    private DeleteMovementUseCase deleteMovementUseCase;

    @MockBean
    private PublishVideoUseCase publishVideoUseCase;

    @MockBean
    private PullMyTimelineQuery pullMyTimelineQuery;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    @DisplayName("发布图文动态成功")
    void publishMovementSuccessfully() throws Exception {
        // Given
        PublishMovementRequest request = new PublishMovementRequest(
            "测试动态内容",
            null,
            null,
            null,
            null,
            0
        );

        when(publishMovementUseCase.publishMovement(any(PublishMovementCommand.class)))
            .thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/content/movements")
                .header("X-User-Id", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("删除图文动态成功")
    void deleteMovementSuccessfully() throws Exception {
        // Given
        Long movementId = 1L;
        doNothing().when(deleteMovementUseCase).deleteMovement(any(DeleteMovementCommand.class));

        // When & Then
        mockMvc.perform(delete("/api/v1/content/movements/{id}", movementId)
                .header("X-User-Id", 100L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("拉取时间线成功")
    void pullTimelineSuccessfully() throws Exception {
        // Given
        TimelineItemDTO item = new TimelineItemDTO(
            1L, 1L, 100L, "用户昵称", "avatar.jpg",
            "内容", "[]", 10, 5, java.time.LocalDateTime.now()
        );
        PageResult<TimelineItemDTO> pageResult = PageResult.of(
            List.of(item),
            100L, 1, 20
        );

        when(pullMyTimelineQuery.pullTimeline(any(Long.class), any(Integer.class), any(Integer.class)))
            .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/v1/content/timeline")
                .header("X-User-Id", 100L)
                .param("pageNo", "1")
                .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.total").value(100));
    }

    @Test
    @DisplayName("发布图文动态 - 使用默认用户 ID")
    void publishMovementWithDefaultUserId() throws Exception {
        // Given
        PublishMovementRequest request = new PublishMovementRequest(
            "测试动态",
            null,
            null,
            null,
            null,
            0
        );

        when(publishMovementUseCase.publishMovement(any(PublishMovementCommand.class)))
            .thenReturn(2L);

        // When & Then
        mockMvc.perform(post("/api/v1/content/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(2));
    }
}

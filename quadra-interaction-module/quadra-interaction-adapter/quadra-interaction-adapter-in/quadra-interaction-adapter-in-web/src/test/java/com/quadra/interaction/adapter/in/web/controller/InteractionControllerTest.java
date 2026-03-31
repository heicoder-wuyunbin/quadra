package com.quadra.interaction.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.interaction.adapter.in.web.dto.CommentRequest;
import com.quadra.interaction.adapter.in.web.dto.LikeRequest;
import com.quadra.interaction.application.port.in.CancelLikeUseCase;
import com.quadra.interaction.application.port.in.CommentTargetUseCase;
import com.quadra.interaction.application.port.in.DeleteCommentUseCase;
import com.quadra.interaction.application.port.in.LikeTargetUseCase;
import com.quadra.interaction.application.port.in.dto.CommentDTO;
import com.quadra.interaction.application.port.in.dto.PageResult;
import com.quadra.interaction.application.port.in.query.ListCommentsQuery;
import com.quadra.interaction.domain.model.TargetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * InteractionController 单元测试
 */
@WebMvcTest(controllers = InteractionController.class)
@Import({})
class InteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LikeTargetUseCase likeTargetUseCase;

    @MockBean
    private CancelLikeUseCase cancelLikeUseCase;

    @MockBean
    private CommentTargetUseCase commentTargetUseCase;

    @MockBean
    private DeleteCommentUseCase deleteCommentUseCase;

    @MockBean
    private ListCommentsQuery listCommentsQuery;

    @Test
    void likeSuccessfully() throws Exception {
        // Given
        LikeRequest request = new LikeRequest("MOVEMENT", 200L);
        when(likeTargetUseCase.like(any(Long.class), eq(TargetType.MOVEMENT), eq(200L)))
            .thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/interactions/likes")
                .header("X-User-Id", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.interactionId").value(1));
    }

    @Test
    void cancelLikeSuccessfully() throws Exception {
        // Given
        doNothing().when(cancelLikeUseCase).cancel(any(Long.class), eq(TargetType.MOVEMENT), eq(200L));

        // When & Then
        mockMvc.perform(delete("/api/v1/interactions/likes")
                .header("X-User-Id", 100L)
                .param("targetType", "MOVEMENT")
                .param("targetId", "200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void commentSuccessfully() throws Exception {
        // Given
        CommentRequest request = new CommentRequest("MOVEMENT", 200L, "测试评论", null);
        when(commentTargetUseCase.comment(any(Long.class), eq(TargetType.MOVEMENT), eq(200L), eq("测试评论"), eq(null)))
            .thenReturn(2L);

        // When & Then
        mockMvc.perform(post("/api/v1/interactions/comments")
                .header("X-User-Id", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.commentId").value(2));
    }

    @Test
    void deleteCommentSuccessfully() throws Exception {
        // Given
        doNothing().when(deleteCommentUseCase).deleteComment(any(Long.class), eq(1L));

        // When & Then
        mockMvc.perform(delete("/api/v1/interactions/comments/{commentId}", 1L)
                .header("X-User-Id", 100L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void listCommentsSuccessfully() throws Exception {
        // Given
        CommentDTO comment = new CommentDTO();
        comment.setId(1L);
        comment.setUserId(100L);
        comment.setTargetId(200L);
        comment.setTargetType("MOVEMENT");
        comment.setContent("评论内容");
        comment.setCreatedAt(LocalDateTime.now());
        
        PageResult<CommentDTO> pageResult = new PageResult<>();
        pageResult.setList(List.of(comment));
        pageResult.setNextCursor(2L);
        pageResult.setHasMore(true);
        
        when(listCommentsQuery.listComments(eq(TargetType.MOVEMENT), eq(200L), any(), eq(10)))
            .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/v1/interactions/comments")
                .header("X-User-Id", 100L)
                .param("targetType", "MOVEMENT")
                .param("targetId", "200")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.hasMore").value(true));
    }
}

package com.quadra.recommend.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.recommend.adapter.in.web.dto.RecordActionRequest;
import com.quadra.recommend.application.port.in.RecordActionUseCase;
import com.quadra.recommend.application.port.in.command.RecordActionCommand;
import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;
import com.quadra.recommend.application.port.in.query.GetRecommendUsersQuery;
import com.quadra.recommend.application.port.in.query.GetRecommendContentsQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RecommendController 单元测试
 */
@WebMvcTest(RecommendController.class)
class RecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecordActionUseCase recordActionUseCase;

    @MockBean
    private GetRecommendUsersQuery getRecommendUsersQuery;

    @MockBean
    private GetRecommendContentsQuery getRecommendContentsQuery;

    @Test
    void recordActionSuccessfully() throws Exception {
        // Given
        RecordActionRequest request = new RecordActionRequest("USER", 200L, "VIEW", BigDecimal.ONE);
        when(recordActionUseCase.recordAction(any(RecordActionCommand.class)))
            .thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/recommends/actions")
                .header("X-User-Id", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void getTodayRecommendUsersSuccessfully() throws Exception {
        // Given
        RecommendUserDTO user = new RecommendUserDTO(
            200L, 100L, BigDecimal.valueOf(95.5), java.time.LocalDate.now()
        );
        
        PageResult<RecommendUserDTO> pageResult = new PageResult<>(
            List.of(user), 100L, 1, 10
        );
        
        when(getRecommendUsersQuery.getTodayRecommendUsers(eq(100L), eq(1), eq(10)))
            .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/v1/recommends/users")
                .header("X-User-Id", 100L)
                .param("pageNum", "1")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.total").value(100));
    }

    @Test
    void getRecommendContentsSuccessfully() throws Exception {
        // Given
        RecommendContentDTO content = new RecommendContentDTO(
            300L, 400L, "MOVEMENT", BigDecimal.valueOf(88.8)
        );
        
        PageResult<RecommendContentDTO> pageResult = new PageResult<>(
            List.of(content), 100L, 1, 10
        );
        
        when(getRecommendContentsQuery.getRecommendContents(eq(100L), eq(1), eq(10)))
            .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/v1/recommends/contents")
                .header("X-User-Id", 100L)
                .param("pageNum", "1")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.total").value(100));
    }
}

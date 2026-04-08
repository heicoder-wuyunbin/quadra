package com.quadra.recommend.adapter.in.web;

import com.quadra.recommend.adapter.in.web.common.Result;
import com.quadra.recommend.adapter.in.web.context.UserContext;
import com.quadra.recommend.adapter.in.web.dto.RecordActionRequest;
import com.quadra.recommend.application.port.in.RecordActionUseCase;
import com.quadra.recommend.application.port.in.command.RecordActionCommand;
import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;
import com.quadra.recommend.application.port.in.query.GetRecommendUsersQuery;
import com.quadra.recommend.application.port.in.query.GetRecommendContentsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 推荐控制器
 */
@Tag(name = "Recommend", description = "推荐相关接口")
@RestController
@RequestMapping("/recommends")
public class RecommendController {

    private final RecordActionUseCase recordActionUseCase;
    private final GetRecommendUsersQuery getRecommendUsersQuery;
    private final GetRecommendContentsQuery getRecommendContentsQuery;

    public RecommendController(
            RecordActionUseCase recordActionUseCase,
            GetRecommendUsersQuery getRecommendUsersQuery,
            GetRecommendContentsQuery getRecommendContentsQuery) {
        this.recordActionUseCase = recordActionUseCase;
        this.getRecommendUsersQuery = getRecommendUsersQuery;
        this.getRecommendContentsQuery = getRecommendContentsQuery;
    }

    @Operation(summary = "记录用户行为", description = "记录用户对目标的行为（VIEW/LIKE/SKIP/DISLIKE）")
    @PostMapping("/actions")
    public Result<Long> recordAction(@RequestBody RecordActionRequest request) {
        Long userId = UserContext.getUserId();
        
        RecordActionCommand command = new RecordActionCommand(
            userId,
            request.targetType(),
            request.targetId(),
            request.actionType(),
            request.weight()
        );
        
        Long actionId = recordActionUseCase.recordAction(command);
        return Result.success(actionId);
    }

    @Operation(summary = "获取今日推荐用户", description = "获取今日推荐用户列表，按分数倒序排列")
    @GetMapping("/users")
    public Result<PageResult<RecommendUserDTO>> getTodayRecommendUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = UserContext.getUserId();
        PageResult<RecommendUserDTO> result = getRecommendUsersQuery.getTodayRecommendUsers(userId, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取推荐内容", description = "获取推荐内容列表，按分数倒序排列")
    @GetMapping("/contents")
    public Result<PageResult<RecommendContentDTO>> getRecommendContents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = UserContext.getUserId();
        PageResult<RecommendContentDTO> result = getRecommendContentsQuery.getRecommendContents(userId, pageNum, pageSize);
        return Result.success(result);
    }
}

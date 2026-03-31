package com.quadra.interaction.adapter.in.web.controller;

import com.quadra.interaction.adapter.in.web.common.Result;
import com.quadra.interaction.adapter.in.web.context.UserContext;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 互动控制器
 */
@RestController
@RequestMapping("/v1/interactions")
public class InteractionController {

    private final LikeTargetUseCase likeTargetUseCase;
    private final CancelLikeUseCase cancelLikeUseCase;
    private final CommentTargetUseCase commentTargetUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final ListCommentsQuery listCommentsQuery;

    public InteractionController(
            LikeTargetUseCase likeTargetUseCase,
            CancelLikeUseCase cancelLikeUseCase,
            CommentTargetUseCase commentTargetUseCase,
            DeleteCommentUseCase deleteCommentUseCase,
            ListCommentsQuery listCommentsQuery) {
        this.likeTargetUseCase = likeTargetUseCase;
        this.cancelLikeUseCase = cancelLikeUseCase;
        this.commentTargetUseCase = commentTargetUseCase;
        this.deleteCommentUseCase = deleteCommentUseCase;
        this.listCommentsQuery = listCommentsQuery;
    }

    /**
     * 点赞
     * POST /api/v1/interactions/likes
     */
    @PostMapping("/likes")
    public Result<Map<String, Long>> like(@Valid @RequestBody LikeRequest request) {
        Long userId = UserContext.getUserId();
        TargetType targetType = TargetType.valueOf(request.targetType());
        Long interactionId = likeTargetUseCase.like(userId, targetType, request.targetId());
        
        Map<String, Long> data = new HashMap<>();
        data.put("interactionId", interactionId);
        return Result.success(data);
    }

    /**
     * 取消点赞
     * DELETE /api/v1/interactions/likes
     */
    @DeleteMapping("/likes")
    public Result<Void> cancelLike(@RequestParam String targetType, @RequestParam Long targetId) {
        Long userId = UserContext.getUserId();
        TargetType type = TargetType.valueOf(targetType);
        cancelLikeUseCase.cancel(userId, type, targetId);
        return Result.success();
    }

    /**
     * 评论
     * POST /api/v1/interactions/comments
     */
    @PostMapping("/comments")
    public Result<Map<String, Long>> comment(@Valid @RequestBody CommentRequest request) {
        Long userId = UserContext.getUserId();
        TargetType targetType = TargetType.valueOf(request.targetType());
        Long interactionId = commentTargetUseCase.comment(
            userId, targetType, request.targetId(), request.content(), request.replyToId()
        );
        
        Map<String, Long> data = new HashMap<>();
        data.put("commentId", interactionId);
        return Result.success(data);
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        deleteCommentUseCase.deleteComment(userId, commentId);
        return Result.success();
    }

    /**
     * 查询评论列表
     * GET /api/v1/interactions/comments
     */
    @GetMapping("/comments")
    public Result<PageResult<CommentDTO>> listComments(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int pageSize) {
        TargetType type = TargetType.valueOf(targetType);
        PageResult<CommentDTO> result = listCommentsQuery.listComments(type, targetId, cursor, pageSize);
        return Result.success(result);
    }
}

package com.quadra.social.adapter.in.web;

import com.quadra.social.adapter.in.web.common.Result;
import com.quadra.social.adapter.in.web.context.UserContext;
import com.quadra.social.adapter.in.web.dto.FollowRequest;
import com.quadra.social.adapter.in.web.dto.SwipeRequest;
import com.quadra.social.application.port.in.FollowUserUseCase;
import com.quadra.social.application.port.in.UnfollowUserUseCase;
import com.quadra.social.application.port.in.SwipeLikeUseCase;
import com.quadra.social.application.port.in.command.FollowUserCommand;
import com.quadra.social.application.port.in.command.UnfollowUserCommand;
import com.quadra.social.application.port.in.command.SwipeLikeCommand;
import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.MatchResultDTO;
import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.query.ListFollowersQuery;
import com.quadra.social.application.port.in.query.ListFollowingQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Social", description = "社交关系相关接口")
@RestController
@RequestMapping("/social")
public class SocialController {

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final SwipeLikeUseCase swipeLikeUseCase;
    private final ListFollowersQuery listFollowersQuery;
    private final ListFollowingQuery listFollowingQuery;

    public SocialController(
            FollowUserUseCase followUserUseCase,
            UnfollowUserUseCase unfollowUserUseCase,
            SwipeLikeUseCase swipeLikeUseCase,
            ListFollowersQuery listFollowersQuery,
            ListFollowingQuery listFollowingQuery) {
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
        this.swipeLikeUseCase = swipeLikeUseCase;
        this.listFollowersQuery = listFollowersQuery;
        this.listFollowingQuery = listFollowingQuery;
    }

    @Operation(summary = "关注用户", description = "关注指定用户")
    @PostMapping("/follows")
    public Result<Void> follow(@RequestBody FollowRequest request) {
        Long userId = UserContext.getUserId();
        FollowUserCommand command = new FollowUserCommand(userId, request.targetUserId());
        followUserUseCase.follow(command);
        return Result.success();
    }

    @Operation(summary = "取消关注", description = "取消关注指定用户")
    @DeleteMapping("/follows/{targetUserId}")
    public Result<Void> unfollow(
            @Parameter(description = "目标用户ID") @PathVariable Long targetUserId) {
        Long userId = UserContext.getUserId();
        UnfollowUserCommand command = new UnfollowUserCommand(userId, targetUserId);
        unfollowUserUseCase.unfollow(command);
        return Result.success();
    }

    @Operation(summary = "滑动操作", description = "对用户进行 LIKE/DISLIKE 操作")
    @PostMapping("/swipes")
    public Result<MatchResultDTO> swipe(@RequestBody SwipeRequest request) {
        Long userId = UserContext.getUserId();
        SwipeLikeCommand command = new SwipeLikeCommand(userId, request.targetUserId(), request.likeType());
        MatchResultDTO result = swipeLikeUseCase.swipe(command);
        return Result.success(result);
    }

    @Operation(summary = "获取粉丝列表", description = "分页查询当前用户的粉丝列表")
    @GetMapping("/followers")
    public Result<PageResult<FollowerDTO>> getFollowers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<FollowerDTO> result = listFollowersQuery.listFollowers(userId, pageNo, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取关注列表", description = "分页查询当前用户关注的人列表")
    @GetMapping("/following")
    public Result<PageResult<FollowerDTO>> getFollowing(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<FollowerDTO> result = listFollowingQuery.listFollowing(userId, pageNo, pageSize);
        return Result.success(result);
    }
}

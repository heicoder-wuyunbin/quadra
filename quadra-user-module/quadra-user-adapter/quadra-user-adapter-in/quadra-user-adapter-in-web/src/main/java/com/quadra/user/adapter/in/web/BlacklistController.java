package com.quadra.user.adapter.in.web;

import com.quadra.user.adapter.in.web.common.Result;
import com.quadra.user.adapter.in.web.context.UserContext;
import com.quadra.user.adapter.in.web.dto.BlacklistRequest;
import com.quadra.user.application.port.in.BlacklistUseCase;
import com.quadra.user.application.port.in.command.AddBlacklistCommand;
import com.quadra.user.application.port.in.command.RemoveBlacklistCommand;
import com.quadra.user.application.port.in.dto.BlacklistItemDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.query.ListMyBlacklistQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blacklist", description = "用户黑名单接口")
@RestController
@RequestMapping("/users/blacklists")
public class BlacklistController {

    private final BlacklistUseCase blacklistUseCase;
    private final ListMyBlacklistQuery listMyBlacklistQuery;

    public BlacklistController(BlacklistUseCase blacklistUseCase, ListMyBlacklistQuery listMyBlacklistQuery) {
        this.blacklistUseCase = blacklistUseCase;
        this.listMyBlacklistQuery = listMyBlacklistQuery;
    }

    @Operation(summary = "添加黑名单", description = "拉黑目标用户")
    @PostMapping
    public Result<Void> addBlacklist(@RequestBody BlacklistRequest request) {
        Long currentUserId = UserContext.getUserId();
        blacklistUseCase.addBlacklist(new AddBlacklistCommand(currentUserId, request.targetUserId()));
        return Result.success();
    }

    @Operation(summary = "分页查询我的黑名单")
    @GetMapping
    public Result<PageResult<BlacklistItemDTO>> listMyBlacklist(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = UserContext.getUserId();
        PageResult<BlacklistItemDTO> page = listMyBlacklistQuery.listBlacklist(currentUserId, pageNo, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "移除黑名单", description = "取消拉黑目标用户")
    @DeleteMapping
    public Result<Void> removeBlacklist(@RequestBody BlacklistRequest request) {
        Long currentUserId = UserContext.getUserId();
        blacklistUseCase.removeBlacklist(new RemoveBlacklistCommand(currentUserId, request.targetUserId()));
        return Result.success();
    }
}

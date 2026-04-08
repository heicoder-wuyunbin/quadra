package com.quadra.content.adapter.in.web;

import com.quadra.content.adapter.in.web.common.Result;
import com.quadra.content.adapter.in.web.dto.PublishMovementRequest;
import com.quadra.content.adapter.in.web.dto.PublishVideoRequest;
import com.quadra.content.application.port.in.DeleteMovementUseCase;
import com.quadra.content.application.port.in.PublishMovementUseCase;
import com.quadra.content.application.port.in.PublishVideoUseCase;
import com.quadra.content.application.port.in.command.DeleteMovementCommand;
import com.quadra.content.application.port.in.command.PublishMovementCommand;
import com.quadra.content.application.port.in.command.PublishVideoCommand;
import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;
import com.quadra.content.application.port.in.query.PullMyTimelineQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Content", description = "内容相关接口")
@RestController
@RequestMapping("/content")
public class ContentController {

    private final PublishMovementUseCase publishMovementUseCase;
    private final DeleteMovementUseCase deleteMovementUseCase;
    private final PublishVideoUseCase publishVideoUseCase;
    private final PullMyTimelineQuery pullMyTimelineQuery;

    public ContentController(
            PublishMovementUseCase publishMovementUseCase,
            DeleteMovementUseCase deleteMovementUseCase,
            PublishVideoUseCase publishVideoUseCase,
            PullMyTimelineQuery pullMyTimelineQuery) {
        this.publishMovementUseCase = publishMovementUseCase;
        this.deleteMovementUseCase = deleteMovementUseCase;
        this.publishVideoUseCase = publishVideoUseCase;
        this.pullMyTimelineQuery = pullMyTimelineQuery;
    }

    @Operation(summary = "发布图文动态", description = "发布图文动态，文本或媒体至少一项非空")
    @PostMapping("/movements")
    public Result<Long> publishMovement(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody PublishMovementRequest request) {
        // 如果没有传递用户ID，使用默认值（实际项目中应该从JWT中解析）
        Long effectiveUserId = userId != null ? userId : 1L;
        
        // 转换媒体信息
        List<PublishMovementCommand.MediaInfo> medias = new ArrayList<>();
        if (request.medias() != null) {
            for (PublishMovementRequest.MediaInfo info : request.medias()) {
                medias.add(new PublishMovementCommand.MediaInfo(
                    info.type(), info.url(), info.thumbnail(), info.width(), info.height()
                ));
            }
        }
        
        PublishMovementCommand command = new PublishMovementCommand(
            effectiveUserId,
            request.textContent(),
            medias,
            request.longitude(),
            request.latitude(),
            request.locationName(),
            request.state()
        );
        
        Long movementId = publishMovementUseCase.publishMovement(command);
        return Result.success(movementId);
    }

    @Operation(summary = "删除图文动态", description = "逻辑删除图文动态")
    @DeleteMapping("/movements/{id}")
    public Result<Void> deleteMovement(
            @Parameter(description = "动态ID") @PathVariable("id") Long movementId,
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long effectiveUserId = userId != null ? userId : 1L;
        
        DeleteMovementCommand command = new DeleteMovementCommand(movementId, effectiveUserId);
        deleteMovementUseCase.deleteMovement(command);
        return Result.success();
    }

    @Operation(summary = "发布短视频", description = "发布短视频")
    @PostMapping("/videos")
    public Result<Long> publishVideo(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody PublishVideoRequest request) {
        Long effectiveUserId = userId != null ? userId : 1L;
        
        PublishVideoCommand command = new PublishVideoCommand(
            effectiveUserId,
            request.videoUrl(),
            request.coverUrl(),
            request.duration(),
            request.description()
        );
        
        Long videoId = publishVideoUseCase.publishVideo(command);
        return Result.success(videoId);
    }

    @Operation(summary = "拉取时间线", description = "拉取用户的时间线Feed")
    @GetMapping("/timeline")
    public Result<PageResult<TimelineItemDTO>> pullTimeline(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int pageSize) {
        Long effectiveUserId = userId != null ? userId : 1L;
        
        PageResult<TimelineItemDTO> result = pullMyTimelineQuery.pullTimeline(effectiveUserId, pageNo, pageSize);
        return Result.success(result);
    }
}

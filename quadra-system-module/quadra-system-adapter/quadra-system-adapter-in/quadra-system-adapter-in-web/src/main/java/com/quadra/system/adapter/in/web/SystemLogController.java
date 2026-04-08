package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.application.port.in.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 管理后台日志接口（系统服务内置）
 *
 * 说明：
 * - 目前仅「访问日志」与「操作日志」为真实落库数据
 * - 其它类型日志先提供空列表接口，避免前端 404，后续再逐步补齐
 */
@Tag(name = "SystemLog", description = "管理后台日志接口")
@RestController
@RequestMapping("/system/logs")
public class SystemLogController {

    @Operation(summary = "操作日志", description = "分页查询管理员操作审计日志（sys_operate_log）")
    @GetMapping("/operation")
    public Result<PageResult<OperationLogVO>> listOperationLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 重要：adapter-in-web 层不应直接依赖 MyBatis-Plus 与 adapter-out 的 Mapper/Entity。
        // 这里先返回空列表，避免编译失败与前端 404；后续如需真实数据，建议通过 application port + adapter-out 实现查询。
        return Result.success(PageResult.of(Collections.emptyList(), 0, page, size));
    }

    @Operation(summary = "登录日志", description = "暂未落库，先返回空列表（避免前端 404）")
    @GetMapping("/login")
    public Result<PageResult<LoginLogVO>> listLoginLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.success(PageResult.of(Collections.emptyList(), 0, page, size));
    }

    @Operation(summary = "错误日志", description = "暂未落库，先返回空列表（避免前端 404）")
    @GetMapping("/error")
    public Result<PageResult<ErrorLogVO>> listErrorLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Boolean handled,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.success(PageResult.of(Collections.emptyList(), 0, page, size));
    }

    @Operation(summary = "标记错误已处理", description = "暂未实现持久化，先返回成功（避免前端 404）")
    @PutMapping("/error/{id}/handled")
    public Result<Void> markErrorHandled(@PathVariable("id") String id, @RequestBody(required = false) Object body) {
        return Result.success();
    }

    @Operation(summary = "接口日志统计", description = "暂未落库，先返回空列表（避免前端 404）")
    @GetMapping("/api")
    public Result<PageResult<ApiStatVO>> listApiStats(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String method,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.success(PageResult.of(Collections.emptyList(), 0, page, size));
    }

    @Operation(summary = "慢查询日志", description = "暂未落库，先返回空列表（避免前端 404）")
    @GetMapping("/slow-sql")
    public Result<PageResult<SlowSqlVO>> listSlowSql(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String db,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.success(PageResult.of(Collections.emptyList(), 0, page, size));
    }

    // --------------------
    // VO 定义（与前端字段对齐）
    // --------------------
    public record OperationLogVO(
            String id,
            Long adminId,
            String adminName,
            String module,
            String action,
            Long targetId,
            Integer responseStatus,
            Integer executeTime,
            String ip,
            String userAgent,
            String createdAt
    ) {}

    public record LoginLogVO(
            String id,
            Long adminId,
            String adminName,
            String ip,
            String location,
            String userAgent,
            String status,
            String reason,
            String createdAt
    ) {}

    public record ErrorLogVO(
            String id,
            String level,
            String service,
            String message,
            String stackTrace,
            Long userId,
            String requestId,
            String url,
            Object params,
            Boolean handled,
            String handledBy,
            String handledAt,
            String createdAt
    ) {}

    public record ApiStatVO(
            String id,
            String method,
            String path,
            Long count,
            Long avgTime,
            Long p95Time,
            Double errorRate,
            String lastCalledAt
    ) {}

    public record SlowSqlVO(
            String id,
            String db,
            String sql,
            Long executeTime,
            Long rowsExamined,
            String explain,
            String suggestion,
            String createdAt
    ) {}
}

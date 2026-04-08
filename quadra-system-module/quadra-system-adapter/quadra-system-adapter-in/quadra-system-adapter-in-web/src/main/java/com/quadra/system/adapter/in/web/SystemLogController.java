package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.application.port.in.command.MarkErrorHandledUseCase;
import com.quadra.system.application.port.in.dto.*;
import com.quadra.system.application.port.in.query.ListApiStatsQuery;
import com.quadra.system.application.port.in.query.ListErrorLogsQuery;
import com.quadra.system.application.port.in.query.ListLoginLogsQuery;
import com.quadra.system.application.port.in.query.ListOperationLogsQuery;
import com.quadra.system.adapter.in.web.context.AdminContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台日志接口（系统服务内置）
 */
@Tag(name = "SystemLog", description = "管理后台日志接口")
@RestController
@RequestMapping("/system/logs")
public class SystemLogController {

    private final ListApiStatsQuery listApiStatsQuery;
    private final ListOperationLogsQuery listOperationLogsQuery;
    private final ListLoginLogsQuery listLoginLogsQuery;
    private final ListErrorLogsQuery listErrorLogsQuery;
    private final MarkErrorHandledUseCase markErrorHandledUseCase;

    public SystemLogController(ListApiStatsQuery listApiStatsQuery,
                               ListOperationLogsQuery listOperationLogsQuery,
                               ListLoginLogsQuery listLoginLogsQuery,
                               ListErrorLogsQuery listErrorLogsQuery,
                               MarkErrorHandledUseCase markErrorHandledUseCase) {
        this.listApiStatsQuery = listApiStatsQuery;
        this.listOperationLogsQuery = listOperationLogsQuery;
        this.listLoginLogsQuery = listLoginLogsQuery;
        this.listErrorLogsQuery = listErrorLogsQuery;
        this.markErrorHandledUseCase = markErrorHandledUseCase;
    }

    @Operation(summary = "操作日志", description = "分页查询管理员操作审计日志（sys_operate_log）")
    @GetMapping("/operation")
    public Result<PageResult<OperationLogVO>> listOperationLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<OperationLogDTO> dtoPage = listOperationLogsQuery.listOperationLogs(keyword, startTime, endTime, page, size);
        List<OperationLogVO> vos = dtoPage.records().stream().map(dto -> new OperationLogVO(
                dto.id().toString(),
                dto.adminId(),
                dto.adminName(),
                dto.module(),
                dto.action(),
                dto.targetId(),
                dto.responseStatus(),
                dto.executeTime(),
                dto.ipAddress(),
                dto.userAgent(),
                dto.createdAt() != null ? dto.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null
        )).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, dtoPage.total(), dtoPage.current(), dtoPage.size()));
    }

    @Operation(summary = "登录日志", description = "分页查询管理员登录日志（sys_login_log）")
    @GetMapping("/login")
    public Result<PageResult<LoginLogVO>> listLoginLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<LoginLogDTO> dtoPage = listLoginLogsQuery.listLoginLogs(keyword, startTime, endTime, page, size);
        List<LoginLogVO> vos = dtoPage.records().stream().map(dto -> new LoginLogVO(
                dto.id().toString(),
                dto.adminId(),
                dto.adminName(),
                dto.ip(),
                dto.location(),
                dto.userAgent(),
                dto.status(),
                dto.reason(),
                dto.createdAt() != null ? dto.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null
        )).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, dtoPage.total(), dtoPage.current(), dtoPage.size()));
    }

    @Operation(summary = "错误日志", description = "分页查询系统错误日志（sys_error_log）")
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
        PageResult<ErrorLogDTO> dtoPage = listErrorLogsQuery.listErrorLogs(level, service, handled, keyword, startTime, endTime, page, size);
        List<ErrorLogVO> vos = dtoPage.records().stream().map(dto -> new ErrorLogVO(
                dto.id().toString(),
                dto.level(),
                dto.service(),
                dto.message(),
                dto.stackTrace(),
                dto.userId(),
                dto.requestId(),
                dto.url(),
                dto.params(),
                dto.handled(),
                dto.handledBy(),
                dto.handledAt() != null ? dto.handledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                dto.createdAt() != null ? dto.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null
        )).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, dtoPage.total(), dtoPage.current(), dtoPage.size()));
    }

    @Operation(summary = "标记错误已处理", description = "将错误日志标记为已处理")
    @PutMapping("/error/{id}/handled")
    public Result<Void> markErrorHandled(@PathVariable("id") String id, @RequestBody(required = false) Object body) {
        Long adminId = AdminContext.getAdminId();
        String adminName = "Admin_" + (adminId != null ? adminId : "Unknown");
        // 如果想拿真实姓名，可以通过 AdminQueryPort 或注入 AdminApplicationService 查询。
        // 这里先用固定格式，后续完善认证上下文。
        markErrorHandledUseCase.markHandled(id, adminId, adminName);
        return Result.success();
    }

    @Operation(summary = "接口日志统计", description = "聚合 sys_request_log，提供接口调用次数/平均耗时/错误率等统计")
    @GetMapping("/api")
    public Result<PageResult<ApiStatDTO>> listApiStats(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String method,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<ApiStatDTO> result = listApiStatsQuery.list(keyword, method, page, size);
        return Result.success(result);
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

    // ApiStatDTO 已与前端字段对齐，这里不再单独定义 VO

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

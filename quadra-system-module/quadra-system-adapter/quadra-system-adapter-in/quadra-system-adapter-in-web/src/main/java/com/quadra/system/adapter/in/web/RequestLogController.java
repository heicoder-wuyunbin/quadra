package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.RequestLogDTO;
import com.quadra.system.application.port.in.query.ListRequestLogsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "RequestLog", description = "管理后台接口访问日志")
@RestController
@RequestMapping("/system/logs/requests")
public class RequestLogController {

    private final ListRequestLogsQuery listRequestLogsQuery;

    public RequestLogController(ListRequestLogsQuery listRequestLogsQuery) {
        this.listRequestLogsQuery = listRequestLogsQuery;
    }

    @Operation(summary = "分页查询访问日志", description = "用于排查 401/404/耗时等问题")
    @GetMapping
    public Result<PageResult<RequestLogDTO>> list(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Integer statusCode,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String pathKeyword,
            @RequestParam(required = false) String traceId,
            @Parameter(description = "开始时间（yyyy-MM-dd HH:mm:ss）")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间（yyyy-MM-dd HH:mm:ss）")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<RequestLogDTO> result = listRequestLogsQuery.list(service, adminId, statusCode, method, pathKeyword, traceId, startTime, endTime, page, size);
        return Result.success(result);
    }
}

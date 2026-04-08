package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.application.port.in.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 运维监控接口（兜底实现）
 *
 * 当前仓库未提供独立 monitor 服务时，为避免管理台页面 404，先返回空数据/成功。
 */
@Tag(name = "Monitor", description = "运维监控（兜底）")
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Operation(summary = "服务列表")
    @GetMapping("/services")
    public Result<Object> listServices() {
        return Result.success(Collections.emptyList());
    }

    @Operation(summary = "性能列表")
    @GetMapping("/performance")
    public Result<Object> listPerformance(@RequestParam(required = false) String keyword) {
        return Result.success(Collections.emptyList());
    }

    @Operation(summary = "告警规则列表")
    @GetMapping("/alerts/rules")
    public Result<PageResult<Map<String, Object>>> listAlertRules(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }

    @Operation(summary = "创建告警规则")
    @PostMapping("/alerts/rules")
    public Result<Long> createAlertRule(@RequestBody Map<String, Object> body) {
        return Result.success(0L);
    }

    @Operation(summary = "更新告警规则")
    @PutMapping("/alerts/rules/{id}")
    public Result<Void> updateAlertRule(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @Operation(summary = "删除告警规则")
    @DeleteMapping("/alerts/rules/{id}")
    public Result<Void> deleteAlertRule(@PathVariable("id") Long id) {
        return Result.success();
    }

    @Operation(summary = "告警事件列表")
    @GetMapping("/alerts/events")
    public Result<PageResult<Map<String, Object>>> listAlertEvents(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }

    @Operation(summary = "确认告警")
    @PutMapping("/alerts/events/{id}/ack")
    public Result<Void> ack(@PathVariable("id") String id) {
        return Result.success();
    }

    @Operation(summary = "处理告警")
    @PutMapping("/alerts/events/{id}/resolve")
    public Result<Void> resolve(@PathVariable("id") String id) {
        return Result.success();
    }
}


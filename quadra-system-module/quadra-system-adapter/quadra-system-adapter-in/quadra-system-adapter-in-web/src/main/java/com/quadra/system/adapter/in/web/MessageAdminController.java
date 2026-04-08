package com.quadra.system.adapter.in.web;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.application.port.in.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 消息推送管理接口（兜底实现）。
 *
 * 当前仓库未提供独立 message 服务时，为避免管理台页面 404，先在 system 服务提供空实现/简单返回。
 * 后续接入真实 message 服务后，可移除该 Controller 并在网关路由到真实服务。
 */
@Tag(name = "MessageAdmin", description = "消息推送管理（兜底）")
@RestController
@RequestMapping("/message/admin")
public class MessageAdminController {

    @Operation(summary = "通知列表")
    @GetMapping("/notices")
    public Result<PageResult<Map<String, Object>>> listNotices(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }

    @Operation(summary = "发送通知")
    @PostMapping("/notices")
    public Result<Long> sendNotice(@RequestBody Map<String, Object> body) {
        return Result.success(0L);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/notices/{id}")
    public Result<Void> deleteNotice(@PathVariable("id") Long id) {
        return Result.success();
    }

    @Operation(summary = "模板列表")
    @GetMapping("/templates")
    public Result<PageResult<Map<String, Object>>> listTemplates(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }

    @Operation(summary = "创建模板")
    @PostMapping("/templates")
    public Result<Long> createTemplate(@RequestBody Map<String, Object> body) {
        return Result.success(0L);
    }

    @Operation(summary = "更新模板")
    @PutMapping("/templates/{id}")
    public Result<Void> updateTemplate(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @Operation(summary = "公告列表")
    @GetMapping("/announcements")
    public Result<PageResult<Map<String, Object>>> listAnnouncements(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }

    @Operation(summary = "创建公告")
    @PostMapping("/announcements")
    public Result<Long> createAnnouncement(@RequestBody Map<String, Object> body) {
        return Result.success(0L);
    }

    @Operation(summary = "更新公告")
    @PutMapping("/announcements/{id}")
    public Result<Void> updateAnnouncement(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @Operation(summary = "发布公告")
    @PutMapping("/announcements/{id}/publish")
    public Result<Void> publish(@PathVariable("id") Long id) {
        return Result.success();
    }

    @Operation(summary = "下线公告")
    @PutMapping("/announcements/{id}/offline")
    public Result<Void> offline(@PathVariable("id") Long id) {
        return Result.success();
    }

    @Operation(summary = "置顶/取消置顶公告")
    @PutMapping("/announcements/{id}/top")
    public Result<Void> top(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @Operation(summary = "推送记录")
    @GetMapping("/records")
    public Result<PageResult<Map<String, Object>>> listRecords(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int p = page == null ? 1 : page;
        int s = size == null ? 10 : size;
        return Result.success(PageResult.of(Collections.emptyList(), 0, p, s));
    }
}


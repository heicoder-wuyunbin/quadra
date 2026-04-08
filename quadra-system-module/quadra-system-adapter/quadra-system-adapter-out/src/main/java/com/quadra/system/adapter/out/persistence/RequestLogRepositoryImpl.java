package com.quadra.system.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.system.adapter.out.persistence.entity.SysRequestLogDO;
import com.quadra.system.adapter.out.persistence.mapper.SysRequestLogMapper;
import com.quadra.system.adapter.out.persistence.mapper.dto.ApiStatRow;
import com.quadra.system.application.port.in.dto.ApiStatDTO;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.RequestLogDTO;
import com.quadra.system.application.port.out.RequestLogRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RequestLogRepositoryImpl implements RequestLogRepositoryPort {

    private final SysRequestLogMapper sysRequestLogMapper;

    public RequestLogRepositoryImpl(SysRequestLogMapper sysRequestLogMapper) {
        this.sysRequestLogMapper = sysRequestLogMapper;
    }

    @Override
    public void save(RequestLogDTO log) {
        SysRequestLogDO logDO = new SysRequestLogDO();
        logDO.setService(log.service());
        logDO.setTraceId(log.traceId());
        logDO.setAdminId(log.adminId());
        logDO.setMethod(log.method());
        logDO.setPath(log.path());
        logDO.setQueryString(log.queryString());
        logDO.setStatusCode(log.statusCode());
        logDO.setDurationMs(log.durationMs());
        logDO.setIpAddress(log.ipAddress());
        logDO.setUserAgent(log.userAgent());
        logDO.setRequestHeaders(log.requestHeaders());
        logDO.setRequestBody(log.requestBody());
        logDO.setResponseBody(log.responseBody());
        sysRequestLogMapper.insert(logDO);
    }

    @Override
    public PageResult<ApiStatDTO> statsPage(String keyword, String method, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        long offset = (long) (safePage - 1) * safeSize;

        Long total = sysRequestLogMapper.countApiStats(keyword, method);
        List<ApiStatRow> rows = sysRequestLogMapper.selectApiStats(keyword, method, safeSize, offset);

        List<ApiStatDTO> records = rows.stream()
                .map(r -> new ApiStatDTO(
                        r.getId(),
                        r.getMethod(),
                        r.getPath(),
                        r.getCount(),
                        r.getAvgTime(),
                        r.getP95Time(),
                        r.getErrorRate(),
                        r.getLastCalledAt()
                ))
                .toList();

        return PageResult.of(records, total == null ? 0 : total, safePage, safeSize);
    }

    @Override
    public PageResult<RequestLogDTO> page(
            String service,
            Long adminId,
            Integer statusCode,
            String method,
            String pathKeyword,
            String traceId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size
    ) {
        LambdaQueryWrapper<SysRequestLogDO> wrapper = new LambdaQueryWrapper<>();
        if (service != null && !service.isBlank()) {
            wrapper.eq(SysRequestLogDO::getService, service);
        }
        if (adminId != null) {
            wrapper.eq(SysRequestLogDO::getAdminId, adminId);
        }
        if (statusCode != null) {
            wrapper.eq(SysRequestLogDO::getStatusCode, statusCode);
        }
        if (method != null && !method.isBlank()) {
            wrapper.eq(SysRequestLogDO::getMethod, method);
        }
        if (pathKeyword != null && !pathKeyword.isBlank()) {
            wrapper.like(SysRequestLogDO::getPath, pathKeyword);
        }
        if (traceId != null && !traceId.isBlank()) {
            wrapper.eq(SysRequestLogDO::getTraceId, traceId);
        }
        if (startTime != null) {
            wrapper.ge(SysRequestLogDO::getCreatedAt, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysRequestLogDO::getCreatedAt, endTime);
        }
        wrapper.orderByDesc(SysRequestLogDO::getCreatedAt);

        Page<SysRequestLogDO> mpPage = new Page<>(page, size);
        Page<SysRequestLogDO> result = sysRequestLogMapper.selectPage(mpPage, wrapper);

        List<RequestLogDTO> records = result.getRecords().stream().map(this::toDto).toList();
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    private RequestLogDTO toDto(SysRequestLogDO d) {
        return new RequestLogDTO(
                d.getId(),
                d.getService(),
                d.getTraceId(),
                d.getAdminId(),
                d.getMethod(),
                d.getPath(),
                d.getQueryString(),
                d.getStatusCode(),
                d.getDurationMs(),
                d.getIpAddress(),
                d.getUserAgent(),
                d.getRequestHeaders(),
                d.getRequestBody(),
                d.getResponseBody(),
                d.getCreatedAt()
        );
    }
}

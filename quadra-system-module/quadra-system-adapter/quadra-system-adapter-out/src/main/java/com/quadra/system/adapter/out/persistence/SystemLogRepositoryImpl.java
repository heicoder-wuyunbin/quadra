package com.quadra.system.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.system.adapter.out.persistence.entity.SysErrorLogDO;
import com.quadra.system.adapter.out.persistence.entity.SysLoginLogDO;
import com.quadra.system.adapter.out.persistence.entity.SysOperateLogDO;
import com.quadra.system.adapter.out.persistence.mapper.SysErrorLogMapper;
import com.quadra.system.adapter.out.persistence.mapper.SysLoginLogMapper;
import com.quadra.system.adapter.out.persistence.mapper.SysOperateLogMapper;
import com.quadra.system.application.port.in.dto.ErrorLogDTO;
import com.quadra.system.application.port.in.dto.LoginLogDTO;
import com.quadra.system.application.port.in.dto.OperationLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.out.SystemLogRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SystemLogRepositoryImpl implements SystemLogRepositoryPort {

    private final SysOperateLogMapper operateLogMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final SysErrorLogMapper errorLogMapper;

    public SystemLogRepositoryImpl(SysOperateLogMapper operateLogMapper,
                                   SysLoginLogMapper loginLogMapper,
                                   SysErrorLogMapper errorLogMapper) {
        this.operateLogMapper = operateLogMapper;
        this.loginLogMapper = loginLogMapper;
        this.errorLogMapper = errorLogMapper;
    }

    @Override
    public PageResult<OperationLogDTO> listOperationLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        LambdaQueryWrapper<SysOperateLogDO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysOperateLogDO::getAdminName, keyword)
                              .or()
                              .like(SysOperateLogDO::getModule, keyword)
                              .or()
                              .like(SysOperateLogDO::getAction, keyword));
        }
        if (startTime != null) {
            wrapper.ge(SysOperateLogDO::getCreatedAt, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysOperateLogDO::getCreatedAt, endTime);
        }
        wrapper.orderByDesc(SysOperateLogDO::getCreatedAt);

        Page<SysOperateLogDO> pageParam = new Page<>(page, size);
        Page<SysOperateLogDO> doPage = operateLogMapper.selectPage(pageParam, wrapper);

        List<OperationLogDTO> dtos = doPage.getRecords().stream()
                .map(d -> new OperationLogDTO(
                        d.getId(),
                        d.getAdminId(),
                        d.getAdminName(),
                        d.getModule(),
                        d.getAction(),
                        d.getTargetId(),
                        d.getResponseStatus(),
                        d.getExecuteTime(),
                        d.getIpAddress(),
                        d.getUserAgent(),
                        d.getRequestParams(),
                        d.getCreatedAt()
                )).collect(Collectors.toList());

        return PageResult.of(dtos, doPage.getTotal(), doPage.getCurrent(), doPage.getSize());
    }

    @Override
    public PageResult<LoginLogDTO> listLoginLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        LambdaQueryWrapper<SysLoginLogDO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysLoginLogDO::getAdminName, keyword)
                              .or()
                              .like(SysLoginLogDO::getIp, keyword));
        }
        if (startTime != null) {
            wrapper.ge(SysLoginLogDO::getCreatedAt, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysLoginLogDO::getCreatedAt, endTime);
        }
        wrapper.orderByDesc(SysLoginLogDO::getCreatedAt);

        Page<SysLoginLogDO> pageParam = new Page<>(page, size);
        Page<SysLoginLogDO> doPage = loginLogMapper.selectPage(pageParam, wrapper);

        List<LoginLogDTO> dtos = doPage.getRecords().stream()
                .map(d -> new LoginLogDTO(
                        d.getId(),
                        d.getAdminId(),
                        d.getAdminName(),
                        d.getIp(),
                        d.getLocation(),
                        d.getUserAgent(),
                        d.getStatus(),
                        d.getReason(),
                        d.getCreatedAt()
                )).collect(Collectors.toList());

        return PageResult.of(dtos, doPage.getTotal(), doPage.getCurrent(), doPage.getSize());
    }

    @Override
    public PageResult<ErrorLogDTO> listErrorLogs(String level, String service, Boolean handled, String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        LambdaQueryWrapper<SysErrorLogDO> wrapper = new LambdaQueryWrapper<>();
        if (level != null && !level.isBlank()) {
            wrapper.eq(SysErrorLogDO::getLevel, level);
        }
        if (service != null && !service.isBlank()) {
            wrapper.eq(SysErrorLogDO::getService, service);
        }
        if (handled != null) {
            wrapper.eq(SysErrorLogDO::getHandled, handled);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysErrorLogDO::getMessage, keyword)
                              .or()
                              .like(SysErrorLogDO::getStackTrace, keyword)
                              .or()
                              .like(SysErrorLogDO::getUrl, keyword));
        }
        if (startTime != null) {
            wrapper.ge(SysErrorLogDO::getCreatedAt, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysErrorLogDO::getCreatedAt, endTime);
        }
        wrapper.orderByDesc(SysErrorLogDO::getCreatedAt);

        Page<SysErrorLogDO> pageParam = new Page<>(page, size);
        Page<SysErrorLogDO> doPage = errorLogMapper.selectPage(pageParam, wrapper);

        List<ErrorLogDTO> dtos = doPage.getRecords().stream()
                .map(d -> new ErrorLogDTO(
                        d.getId(),
                        d.getLevel(),
                        d.getService(),
                        d.getMessage(),
                        d.getStackTrace(),
                        d.getUserId(),
                        d.getRequestId(),
                        d.getUrl(),
                        d.getParams(),
                        d.getHandled(),
                        d.getHandledBy(),
                        d.getHandledAt(),
                        d.getCreatedAt()
                )).collect(Collectors.toList());

        return PageResult.of(dtos, doPage.getTotal(), doPage.getCurrent(), doPage.getSize());
    }

    @Override
    public void markErrorHandled(String id, Long adminId, String adminName) {
        SysErrorLogDO errorLog = errorLogMapper.selectById(id);
        if (errorLog != null) {
            errorLog.setHandled(true);
            errorLog.setHandledBy(adminName);
            errorLog.setHandledAt(LocalDateTime.now());
            errorLogMapper.updateById(errorLog);
        }
    }
}

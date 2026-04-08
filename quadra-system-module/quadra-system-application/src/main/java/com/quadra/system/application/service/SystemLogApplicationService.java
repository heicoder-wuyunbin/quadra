package com.quadra.system.application.service;

import com.quadra.system.application.port.in.SaveErrorLogUseCase;
import com.quadra.system.application.port.in.SaveLoginLogUseCase;
import com.quadra.system.application.port.in.SaveOperationLogUseCase;
import com.quadra.system.application.port.in.command.MarkErrorHandledUseCase;
import com.quadra.system.application.port.in.command.SaveErrorLogCommand;
import com.quadra.system.application.port.in.command.SaveLoginLogCommand;
import com.quadra.system.application.port.in.command.SaveOperationLogCommand;
import com.quadra.system.application.port.in.dto.ErrorLogDTO;
import com.quadra.system.application.port.in.dto.LoginLogDTO;
import com.quadra.system.application.port.in.dto.OperationLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.query.ListErrorLogsQuery;
import com.quadra.system.application.port.in.query.ListLoginLogsQuery;
import com.quadra.system.application.port.in.query.ListOperationLogsQuery;
import com.quadra.system.application.port.out.SystemLogRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemLogApplicationService implements ListOperationLogsQuery, ListLoginLogsQuery, ListErrorLogsQuery, MarkErrorHandledUseCase, SaveOperationLogUseCase, SaveLoginLogUseCase, SaveErrorLogUseCase {

    private final SystemLogRepositoryPort systemLogRepositoryPort;

    public SystemLogApplicationService(SystemLogRepositoryPort systemLogRepositoryPort) {
        this.systemLogRepositoryPort = systemLogRepositoryPort;
    }

    @Override
    public PageResult<OperationLogDTO> listOperationLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return systemLogRepositoryPort.listOperationLogs(keyword, startTime, endTime, page, size);
    }

    @Override
    public PageResult<LoginLogDTO> listLoginLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return systemLogRepositoryPort.listLoginLogs(keyword, startTime, endTime, page, size);
    }

    @Override
    public PageResult<ErrorLogDTO> listErrorLogs(String level, String service, Boolean handled, String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return systemLogRepositoryPort.listErrorLogs(level, service, handled, keyword, startTime, endTime, page, size);
    }

    @Override
    public void markHandled(String id, Long adminId, String adminName) {
        systemLogRepositoryPort.markErrorHandled(id, adminId, adminName);
    }

    @Override
    public void save(SaveOperationLogCommand command) {
        systemLogRepositoryPort.saveOperationLog(command);
    }

    @Override
    public void save(SaveLoginLogCommand command) {
        systemLogRepositoryPort.saveLoginLog(command);
    }

    @Override
    public void save(SaveErrorLogCommand command) {
        systemLogRepositoryPort.saveErrorLog(command);
    }
}

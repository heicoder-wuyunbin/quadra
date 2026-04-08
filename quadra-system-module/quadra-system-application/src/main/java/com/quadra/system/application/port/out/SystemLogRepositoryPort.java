package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.command.SaveErrorLogCommand;
import com.quadra.system.application.port.in.command.SaveLoginLogCommand;
import com.quadra.system.application.port.in.command.SaveOperationLogCommand;
import com.quadra.system.application.port.in.dto.ErrorLogDTO;
import com.quadra.system.application.port.in.dto.LoginLogDTO;
import com.quadra.system.application.port.in.dto.OperationLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;

import java.time.LocalDateTime;

public interface SystemLogRepositoryPort {
    PageResult<OperationLogDTO> listOperationLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResult<LoginLogDTO> listLoginLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResult<ErrorLogDTO> listErrorLogs(String level, String service, Boolean handled, String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    
    void markErrorHandled(String id, Long adminId, String adminName);

    void saveOperationLog(SaveOperationLogCommand command);
    void saveLoginLog(SaveLoginLogCommand command);
    void saveErrorLog(SaveErrorLogCommand command);
}

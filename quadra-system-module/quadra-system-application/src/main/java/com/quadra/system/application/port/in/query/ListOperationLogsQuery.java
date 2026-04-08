package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.OperationLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;

import java.time.LocalDateTime;

public interface ListOperationLogsQuery {
    PageResult<OperationLogDTO> listOperationLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
}

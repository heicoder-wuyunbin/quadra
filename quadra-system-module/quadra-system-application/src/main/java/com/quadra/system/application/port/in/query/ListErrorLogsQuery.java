package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.ErrorLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;

import java.time.LocalDateTime;

public interface ListErrorLogsQuery {
    PageResult<ErrorLogDTO> listErrorLogs(String level, String service, Boolean handled, String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
}

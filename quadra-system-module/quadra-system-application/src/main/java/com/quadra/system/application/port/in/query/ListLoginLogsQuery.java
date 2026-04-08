package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.LoginLogDTO;
import com.quadra.system.application.port.in.dto.PageResult;

import java.time.LocalDateTime;

public interface ListLoginLogsQuery {
    PageResult<LoginLogDTO> listLoginLogs(String keyword, LocalDateTime startTime, LocalDateTime endTime, int page, int size);
}

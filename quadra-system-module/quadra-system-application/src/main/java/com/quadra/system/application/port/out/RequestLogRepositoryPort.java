package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.RequestLogDTO;

import java.time.LocalDateTime;

public interface RequestLogRepositoryPort {

    void save(RequestLogDTO log);

    PageResult<RequestLogDTO> page(
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
    );
}


package com.quadra.system.application.service;

import com.quadra.system.application.port.in.SaveRequestLogUseCase;
import com.quadra.system.application.port.in.command.SaveRequestLogCommand;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.RequestLogDTO;
import com.quadra.system.application.port.in.query.ListRequestLogsQuery;
import com.quadra.system.application.port.out.RequestLogRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RequestLogApplicationService implements SaveRequestLogUseCase, ListRequestLogsQuery {

    private final RequestLogRepositoryPort requestLogRepositoryPort;

    public RequestLogApplicationService(RequestLogRepositoryPort requestLogRepositoryPort) {
        this.requestLogRepositoryPort = requestLogRepositoryPort;
    }

    @Override
    public void save(SaveRequestLogCommand command) {
        RequestLogDTO dto = new RequestLogDTO(
                null,
                command.service(),
                command.traceId(),
                command.adminId(),
                command.method(),
                command.path(),
                command.queryString(),
                command.statusCode(),
                command.durationMs(),
                command.ipAddress(),
                command.userAgent(),
                command.requestHeaders(),
                command.requestBody(),
                command.responseBody(),
                null
        );
        requestLogRepositoryPort.save(dto);
    }

    @Override
    public PageResult<RequestLogDTO> list(
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
        return requestLogRepositoryPort.page(service, adminId, statusCode, method, pathKeyword, traceId, startTime, endTime, page, size);
    }
}


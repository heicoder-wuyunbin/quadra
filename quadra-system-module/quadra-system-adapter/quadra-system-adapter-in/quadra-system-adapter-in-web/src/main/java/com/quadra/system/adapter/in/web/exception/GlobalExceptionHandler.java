package com.quadra.system.adapter.in.web.exception;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.adapter.in.web.common.ResultCode;
import com.quadra.system.adapter.in.web.context.AdminContext;
import com.quadra.system.application.port.in.SaveErrorLogUseCase;
import com.quadra.system.application.port.in.command.SaveErrorLogCommand;
import com.quadra.system.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final SaveErrorLogUseCase saveErrorLogUseCase;

    public GlobalExceptionHandler(SaveErrorLogUseCase saveErrorLogUseCase) {
        this.saveErrorLogUseCase = saveErrorLogUseCase;
    }

    /**
     * 处理领域异常 (业务规则拦截)
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleDomainException(DomainException e, HttpServletRequest request) {
        log.warn("业务异常: {}", e.getMessage());
        saveErrorLogAsync("WARN", e, request);
        
        if (e.getMessage() != null && e.getMessage().contains("已存在")) {
            return Result.failure(ResultCode.ADMIN_ALREADY_EXISTS, e.getMessage());
        }
        return Result.failure(ResultCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * 处理系统未知异常兜底
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统未知异常: ", e);
        saveErrorLogAsync("ERROR", e, request);
        return Result.failure(ResultCode.INTERNAL_SERVER_ERROR);
    }

    private void saveErrorLogAsync(String level, Exception e, HttpServletRequest request) {
        Long adminId = AdminContext.getAdminId();
        String url = request != null ? request.getRequestURI() : "";
        
        String tempTraceId = request != null ? request.getHeader("X-B3-TraceId") : "";
        if (tempTraceId == null || tempTraceId.isEmpty()) {
            tempTraceId = request != null ? request.getHeader("traceparent") : "";
        }
        final String traceId = tempTraceId;
        
        String params = request != null ? request.getQueryString() : "";

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();

        CompletableFuture.runAsync(() -> {
            try {
                SaveErrorLogCommand command = new SaveErrorLogCommand(
                        level,
                        "system-service",
                        message,
                        stackTrace,
                        adminId,
                        traceId,
                        url,
                        params
                );
                saveErrorLogUseCase.save(command);
            } catch (Exception ex) {
                log.error("Failed to save error log", ex);
            }
        });
    }
}

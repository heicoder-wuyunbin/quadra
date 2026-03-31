package com.quadra.system.adapter.in.web.exception;

import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.adapter.in.web.common.ResultCode;
import com.quadra.system.domain.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理领域异常 (业务规则拦截)
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleDomainException(DomainException e) {
        log.warn("业务异常: {}", e.getMessage());
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
    public Result<Void> handleException(Exception e) {
        log.error("系统未知异常: ", e);
        return Result.failure(ResultCode.INTERNAL_SERVER_ERROR);
    }
}

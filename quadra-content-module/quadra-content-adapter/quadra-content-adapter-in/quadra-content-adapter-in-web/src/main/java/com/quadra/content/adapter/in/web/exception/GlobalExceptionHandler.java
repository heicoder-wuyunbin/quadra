package com.quadra.content.adapter.in.web.exception;

import com.quadra.content.adapter.in.web.common.Result;
import com.quadra.content.adapter.in.web.common.ResultCode;
import com.quadra.content.domain.exception.DomainException;
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
     * HTTP 状态码返回 200 或 400 视团队规范而定。这里采用 200，并在 Result 内部体现业务错误码
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleDomainException(DomainException e) {
        log.warn("业务异常: {}", e.getMessage());
        // 如果异常信息里包含特定的关键字，可以映射到具体的 ResultCode，这里做简单处理
        if (e.getMessage() != null && e.getMessage().contains("不能为空")) {
            return Result.failure(ResultCode.CONTENT_EMPTY, e.getMessage());
        }
        if (e.getMessage() != null && e.getMessage().contains("不存在")) {
            return Result.failure(ResultCode.CONTENT_NOT_FOUND, e.getMessage());
        }
        if (e.getMessage() != null && e.getMessage().contains("无权")) {
            return Result.failure(ResultCode.NO_PERMISSION, e.getMessage());
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

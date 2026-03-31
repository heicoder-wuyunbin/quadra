package com.quadra.recommend.domain.exception;

/**
 * 领域异常
 * 用于封装领域层业务规则校验失败的情况
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

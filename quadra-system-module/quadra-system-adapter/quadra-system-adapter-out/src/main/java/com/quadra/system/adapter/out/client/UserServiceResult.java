package com.quadra.system.adapter.out.client;

public record UserServiceResult<T>(
        boolean success,
        int code,
        String message,
        T data,
        long timestamp,
        String requestId
) {}

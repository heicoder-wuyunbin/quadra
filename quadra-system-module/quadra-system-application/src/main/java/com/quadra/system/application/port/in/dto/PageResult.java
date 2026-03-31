package com.quadra.system.application.port.in.dto;

import java.util.List;

public record PageResult<T>(
    List<T> records,
    long total,
    long current,
    long size,
    long pages
) {
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        long pages = size > 0 ? (total + size - 1) / size : 0;
        return new PageResult<>(records, total, current, size, pages);
    }
}

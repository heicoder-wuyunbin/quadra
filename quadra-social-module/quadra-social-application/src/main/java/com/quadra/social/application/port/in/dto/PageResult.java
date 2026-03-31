package com.quadra.social.application.port.in.dto;

import java.util.List;

public record PageResult<T>(
        List<T> records,
        long total,
        int pageNo,
        int pageSize,
        long totalPages
) {
    public static <T> PageResult<T> of(List<T> records, long total, int pageNo, int pageSize) {
        long pages = pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(records, total, pageNo, pageSize, pages);
    }
}

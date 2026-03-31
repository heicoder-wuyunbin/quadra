package com.quadra.recommend.application.port.in.dto;

import java.util.List;

/**
 * 分页结果
 */
public record PageResult<T>(
    List<T> list,
    long total,
    int pageNum,
    int pageSize
) {
    public boolean hasNext() {
        return (long) pageNum * pageSize < total;
    }
}

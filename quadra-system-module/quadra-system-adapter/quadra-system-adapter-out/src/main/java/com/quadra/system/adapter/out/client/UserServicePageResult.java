package com.quadra.system.adapter.out.client;

import java.util.List;

public record UserServicePageResult<T>(
        List<T> records,
        long total,
        int pageNo,
        int pageSize,
        long totalPages
) {}

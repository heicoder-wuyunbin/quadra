package com.quadra.system.adapter.out.persistence.mapper.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiStatRow {
    private String id;
    private String method;
    private String path;
    private Long count;
    private Long avgTime;
    private Long p95Time;
    private Double errorRate;
    private LocalDateTime lastCalledAt;
}


package com.quadra.system.adapter.in.web.dto;

import java.util.List;

public record BatchAdminIdsRequest(
    List<Long> adminIds,
    Integer status
) {}

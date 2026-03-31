package com.quadra.content.application.port.out;

import java.util.List;

/**
 * 粉丝查询端口（调用 social 服务）
 */
public interface FollowerQueryPort {
    /**
     * 查询用户的所有粉丝ID
     * @param userId 用户ID
     * @return 粉丝ID列表
     */
    List<Long> getFollowerIds(Long userId);
}

package com.quadra.content.adapter.out.event;

import com.quadra.content.application.port.out.FollowerQueryPort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 粉丝查询适配器
 * TODO: 实际应该通过 RPC 调用 social 服务获取粉丝列表
 */
@Component
public class FollowerQueryAdapter implements FollowerQueryPort {

    @Override
    public List<Long> getFollowerIds(Long userId) {
        // TODO: 调用 social 服务获取粉丝列表
        // 暂时返回空列表，等待 social 服务实现后对接
        return Collections.emptyList();
    }
}

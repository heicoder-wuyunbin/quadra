package com.quadra.recommend.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.recommend.adapter.out.persistence.entity.OutboxEventDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Outbox 事件 Mapper
 */
@Mapper
public interface OutboxEventMapper extends BaseMapper<OutboxEventDO> {
}

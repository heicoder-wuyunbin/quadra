package com.quadra.recommend.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.recommend.adapter.out.persistence.entity.UserActionLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为日志 Mapper
 */
@Mapper
public interface UserActionLogMapper extends BaseMapper<UserActionLogDO> {
}

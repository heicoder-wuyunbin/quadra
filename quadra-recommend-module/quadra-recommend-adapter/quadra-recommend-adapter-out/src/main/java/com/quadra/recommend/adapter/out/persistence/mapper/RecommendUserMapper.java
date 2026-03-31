package com.quadra.recommend.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendUserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐用户结果 Mapper
 */
@Mapper
public interface RecommendUserMapper extends BaseMapper<RecommendUserDO> {
}

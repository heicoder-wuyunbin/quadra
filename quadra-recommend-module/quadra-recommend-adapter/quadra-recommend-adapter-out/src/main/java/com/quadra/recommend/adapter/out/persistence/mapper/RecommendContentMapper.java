package com.quadra.recommend.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendContentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐内容结果 Mapper
 */
@Mapper
public interface RecommendContentMapper extends BaseMapper<RecommendContentDO> {
}

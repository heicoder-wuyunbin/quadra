package com.quadra.recommend.adapter.out.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quadra.recommend.adapter.out.persistence.entity.UserFeatureDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户特征 Mapper
 */
@Mapper
public interface UserFeatureMapper extends BaseMapper<UserFeatureDO> {
}

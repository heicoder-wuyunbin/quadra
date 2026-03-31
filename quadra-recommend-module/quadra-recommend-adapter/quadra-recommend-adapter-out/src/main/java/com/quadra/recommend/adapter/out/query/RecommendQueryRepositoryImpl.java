package com.quadra.recommend.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendUserDO;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendContentDO;
import com.quadra.recommend.adapter.out.persistence.mapper.RecommendUserMapper;
import com.quadra.recommend.adapter.out.persistence.mapper.RecommendContentMapper;
import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;
import com.quadra.recommend.application.port.out.RecommendQueryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐查询仓库实现
 */
@Repository
public class RecommendQueryRepositoryImpl implements RecommendQueryPort {

    private final RecommendUserMapper recommendUserMapper;
    private final RecommendContentMapper recommendContentMapper;

    public RecommendQueryRepositoryImpl(
            RecommendUserMapper recommendUserMapper,
            RecommendContentMapper recommendContentMapper) {
        this.recommendUserMapper = recommendUserMapper;
        this.recommendContentMapper = recommendContentMapper;
    }

    @Override
    public PageResult<RecommendUserDTO> findTodayRecommendUsers(Long userId, int pageNum, int pageSize) {
        LocalDate today = LocalDate.now();
        
        LambdaQueryWrapper<RecommendUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendUserDO::getUserId, userId)
               .eq(RecommendUserDO::getRecommendDate, today)
               .orderByDesc(RecommendUserDO::getScore);
        
        Page<RecommendUserDO> page = new Page<>(pageNum, pageSize);
        Page<RecommendUserDO> result = recommendUserMapper.selectPage(page, wrapper);
        
        List<RecommendUserDTO> dtoList = result.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageResult<>(dtoList, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public PageResult<RecommendContentDTO> findRecommendContents(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<RecommendContentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendContentDO::getUserId, userId)
               .orderByDesc(RecommendContentDO::getScore);
        
        Page<RecommendContentDO> page = new Page<>(pageNum, pageSize);
        Page<RecommendContentDO> result = recommendContentMapper.selectPage(page, wrapper);
        
        List<RecommendContentDTO> dtoList = result.getRecords().stream()
            .map(this::convertToContentDTO)
            .collect(Collectors.toList());
        
        return new PageResult<>(dtoList, result.getTotal(), pageNum, pageSize);
    }

    private RecommendUserDTO convertToDTO(RecommendUserDO recommendUserDO) {
        return new RecommendUserDTO(
            recommendUserDO.getId(),
            recommendUserDO.getRecommendTargetId(),
            recommendUserDO.getScore(),
            recommendUserDO.getRecommendDate()
        );
    }

    private RecommendContentDTO convertToContentDTO(RecommendContentDO recommendContentDO) {
        return new RecommendContentDTO(
            recommendContentDO.getId(),
            recommendContentDO.getTargetId(),
            recommendContentDO.getTargetType(),
            recommendContentDO.getScore()
        );
    }
}

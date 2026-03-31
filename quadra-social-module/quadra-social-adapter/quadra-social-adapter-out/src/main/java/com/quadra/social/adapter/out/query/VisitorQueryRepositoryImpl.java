package com.quadra.social.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.social.adapter.out.persistence.entity.UserVisitorDO;
import com.quadra.social.adapter.out.persistence.mapper.UserVisitorMapper;
import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.dto.VisitorDTO;
import com.quadra.social.application.port.out.VisitorQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VisitorQueryRepositoryImpl implements VisitorQueryPort {

    private final UserVisitorMapper userVisitorMapper;

    public VisitorQueryRepositoryImpl(UserVisitorMapper userVisitorMapper) {
        this.userVisitorMapper = userVisitorMapper;
    }

    @Override
    public PageResult<VisitorDTO> findVisitors(Long userId, int pageNo, int pageSize) {
        Page<UserVisitorDO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<UserVisitorDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserVisitorDO::getUserId, userId)
               .eq(UserVisitorDO::getDeleted, 0)
               .orderByDesc(UserVisitorDO::getVisitTime);
        
        Page<UserVisitorDO> result = userVisitorMapper.selectPage(page, wrapper);
        
        List<VisitorDTO> visitors = result.getRecords().stream()
            .map(this::convertToDTO)
            .toList();
        
        return PageResult.of(visitors, result.getTotal(), pageNo, pageSize);
    }

    private VisitorDTO convertToDTO(UserVisitorDO visitorDO) {
        return new VisitorDTO(
            visitorDO.getVisitorId(),
            null, // nickname 需要关联查询
            null, // avatar 需要关联查询
            visitorDO.getSource(),
            visitorDO.getScore(),
            visitorDO.getVisitTime()
        );
    }
}

package com.quadra.user.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.user.adapter.out.persistence.entity.StrangerQuestionDO;
import com.quadra.user.adapter.out.persistence.mapper.StrangerQuestionMapper;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.dto.QuestionItemDTO;
import com.quadra.user.application.port.out.QuestionQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuestionQueryRepositoryImpl implements QuestionQueryPort {

    private final StrangerQuestionMapper strangerQuestionMapper;

    public QuestionQueryRepositoryImpl(StrangerQuestionMapper strangerQuestionMapper) {
        this.strangerQuestionMapper = strangerQuestionMapper;
    }

    @Override
    public PageResult<QuestionItemDTO> listByUserId(Long userId, int pageNo, int pageSize) {
        Page<StrangerQuestionDO> page = new Page<>(Math.max(pageNo, 1), Math.max(pageSize, 1));
        LambdaQueryWrapper<StrangerQuestionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrangerQuestionDO::getUserId, userId)
                .orderByAsc(StrangerQuestionDO::getSortOrder)
                .orderByDesc(StrangerQuestionDO::getCreateTime);
        Page<StrangerQuestionDO> result = strangerQuestionMapper.selectPage(page, wrapper);
        List<QuestionItemDTO> records = result.getRecords().stream()
                .map(item -> new QuestionItemDTO(item.getId(), item.getQuestion(), item.getSortOrder(), item.getStatus(), item.getCreateTime()))
                .toList();
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
}

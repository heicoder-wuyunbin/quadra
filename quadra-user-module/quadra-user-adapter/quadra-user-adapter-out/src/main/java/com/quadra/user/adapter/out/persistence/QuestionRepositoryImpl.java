package com.quadra.user.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.user.adapter.out.persistence.entity.StrangerQuestionDO;
import com.quadra.user.adapter.out.persistence.mapper.StrangerQuestionMapper;
import com.quadra.user.application.port.out.QuestionRepositoryPort;
import com.quadra.user.domain.model.question.StrangerQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class QuestionRepositoryImpl implements QuestionRepositoryPort {

    private final StrangerQuestionMapper questionMapper;

    public QuestionRepositoryImpl(StrangerQuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public void save(StrangerQuestion question) {
        StrangerQuestionDO DO = new StrangerQuestionDO();
        DO.setId(question.getId());
        DO.setUserId(question.getUserId());
        DO.setQuestion(question.getQuestion());
        DO.setSortOrder(question.getSortOrder());
        DO.setStatus(question.getStatus());
        DO.setCreateTime(question.getCreateTime());
        questionMapper.insert(DO);
    }

    @Override
    public void update(StrangerQuestion question) {
        StrangerQuestionDO DO = new StrangerQuestionDO();
        DO.setId(question.getId());
        DO.setQuestion(question.getQuestion());
        DO.setSortOrder(question.getSortOrder());
        DO.setStatus(question.getStatus());
        questionMapper.updateById(DO);
    }

    @Override
    public StrangerQuestion findById(Long id) {
        StrangerQuestionDO DO = questionMapper.selectById(id);
        if (DO == null) return null;
        return assemble(DO);
    }

    @Override
    public List<StrangerQuestion> findByUserId(Long userId) {
        LambdaQueryWrapper<StrangerQuestionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrangerQuestionDO::getUserId, userId)
               .eq(StrangerQuestionDO::getStatus, 1)
               .orderByAsc(StrangerQuestionDO::getSortOrder);
               
        return questionMapper.selectList(wrapper).stream()
                .map(this::assemble)
                .collect(Collectors.toList());
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private StrangerQuestion assemble(StrangerQuestionDO DO) {
        try {
            java.lang.reflect.Constructor<StrangerQuestion> constructor = StrangerQuestion.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            StrangerQuestion q = constructor.newInstance();
            
            setField(q, "id", DO.getId());
            setField(q, "userId", DO.getUserId());
            setField(q, "question", DO.getQuestion());
            setField(q, "sortOrder", DO.getSortOrder());
            setField(q, "status", DO.getStatus());
            setField(q, "createTime", DO.getCreateTime());
            return q;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore StrangerQuestion", e);
        }
    }
    
    private void setField(Object obj, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}

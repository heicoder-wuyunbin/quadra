package com.quadra.interaction.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.interaction.adapter.out.persistence.entity.InteractionDO;
import com.quadra.interaction.adapter.out.persistence.mapper.InteractionMapper;
import com.quadra.interaction.application.port.out.InteractionRepositoryPort;
import com.quadra.interaction.domain.exception.DomainException;
import com.quadra.interaction.domain.model.ActionType;
import com.quadra.interaction.domain.model.Interaction;
import com.quadra.interaction.domain.model.TargetType;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class InteractionRepositoryImpl implements InteractionRepositoryPort {

    private final InteractionMapper interactionMapper;

    public InteractionRepositoryImpl(InteractionMapper interactionMapper) {
        this.interactionMapper = interactionMapper;
    }

    @Override
    @Transactional
    public void save(Interaction interaction) {
        InteractionDO interactionDO = convertToDO(interaction);
        
        try {
            interactionMapper.insert(interactionDO);
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突，表示重复点赞
            throw new DomainException("已经点赞过了", e);
        }
    }

    @Override
    @Transactional
    public void update(Interaction interaction) {
        InteractionDO interactionDO = convertToDO(interaction);
        int updated = interactionMapper.updateById(interactionDO);
        if (updated == 0) {
            throw new DomainException("未找到互动记录");
        }
    }

    @Override
    @Transactional
    public void cancelLike(Long userId, TargetType targetType, Long targetId) {
        // 逻辑删除：设置 deleted=1
        LambdaUpdateWrapper<InteractionDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(InteractionDO::getUserId, userId)
                     .eq(InteractionDO::getTargetType, targetType.name())
                     .eq(InteractionDO::getTargetId, targetId)
                     .eq(InteractionDO::getActionType, ActionType.LIKE.name())
                     .eq(InteractionDO::getDeleted, 0)
                     .set(InteractionDO::getDeleted, 1)
                     .set(InteractionDO::getUpdatedAt, LocalDateTime.now());
        
        int updated = interactionMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new DomainException("未找到点赞记录");
        }
    }

    @Override
    public Interaction findById(Long id) {
        InteractionDO interactionDO = interactionMapper.selectById(id);
        if (interactionDO == null || interactionDO.getDeleted() == 1) {
            return null;
        }
        return convertToDomain(interactionDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    @Override
    public boolean existsLike(Long userId, TargetType targetType, Long targetId) {
        LambdaQueryWrapper<InteractionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionDO::getUserId, userId)
               .eq(InteractionDO::getTargetType, targetType.name())
               .eq(InteractionDO::getTargetId, targetId)
               .eq(InteractionDO::getActionType, ActionType.LIKE.name())
               .eq(InteractionDO::getDeleted, 0);
        
        return interactionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Interaction findLike(Long userId, TargetType targetType, Long targetId) {
        LambdaQueryWrapper<InteractionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionDO::getUserId, userId)
               .eq(InteractionDO::getTargetType, targetType.name())
               .eq(InteractionDO::getTargetId, targetId)
               .eq(InteractionDO::getActionType, ActionType.LIKE.name())
               .eq(InteractionDO::getDeleted, 0);
        
        InteractionDO interactionDO = interactionMapper.selectOne(wrapper);
        if (interactionDO == null) {
            return null;
        }
        return convertToDomain(interactionDO);
    }

    private InteractionDO convertToDO(Interaction interaction) {
        InteractionDO interactionDO = new InteractionDO();
        interactionDO.setId(interaction.getId());
        interactionDO.setUserId(interaction.getUserId());
        interactionDO.setTargetId(interaction.getTargetId());
        interactionDO.setTargetType(interaction.getTargetType().name());
        interactionDO.setActionType(interaction.getActionType().name());
        interactionDO.setContent(interaction.getContent());
        interactionDO.setReplyToId(interaction.getReplyToId());
        interactionDO.setVersion(interaction.getVersion());
        interactionDO.setDeleted(interaction.getDeleted());
        interactionDO.setCreatedAt(interaction.getCreatedAt());
        interactionDO.setUpdatedAt(interaction.getUpdatedAt());
        return interactionDO;
    }

    private Interaction convertToDomain(InteractionDO interactionDO) {
        try {
            // 通过反射创建 Interaction 实例并设置属性
            java.lang.reflect.Constructor<Interaction> constructor = Interaction.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Interaction interaction = constructor.newInstance();
            
            setField(interaction, "id", interactionDO.getId());
            setField(interaction, "userId", interactionDO.getUserId());
            setField(interaction, "targetId", interactionDO.getTargetId());
            setField(interaction, "targetType", TargetType.valueOf(interactionDO.getTargetType()));
            setField(interaction, "actionType", ActionType.valueOf(interactionDO.getActionType()));
            setField(interaction, "content", interactionDO.getContent());
            setField(interaction, "replyToId", interactionDO.getReplyToId());
            setField(interaction, "version", interactionDO.getVersion());
            setField(interaction, "deleted", interactionDO.getDeleted());
            setField(interaction, "createdAt", interactionDO.getCreatedAt());
            setField(interaction, "updatedAt", interactionDO.getUpdatedAt());
            
            return interaction;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore Interaction from DB", e);
        }
    }
    
    private void setField(Interaction interaction, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = Interaction.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(interaction, value);
    }
}

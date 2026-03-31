package com.quadra.interaction.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.interaction.adapter.out.persistence.entity.InteractionDO;
import com.quadra.interaction.adapter.out.persistence.mapper.InteractionMapper;
import com.quadra.interaction.application.port.in.dto.CommentDTO;
import com.quadra.interaction.application.port.in.dto.PageResult;
import com.quadra.interaction.application.port.out.InteractionQueryPort;
import com.quadra.interaction.domain.model.ActionType;
import com.quadra.interaction.domain.model.TargetType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 互动查询仓储实现
 * 游标分页：按 target+时间/ID 排序
 */
@Repository
public class InteractionQueryRepositoryImpl implements InteractionQueryPort {

    private final InteractionMapper interactionMapper;

    public InteractionQueryRepositoryImpl(InteractionMapper interactionMapper) {
        this.interactionMapper = interactionMapper;
    }

    @Override
    public PageResult<CommentDTO> findCommentsByTarget(TargetType targetType, Long targetId, Long cursor, int pageSize) {
        LambdaQueryWrapper<InteractionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionDO::getTargetType, targetType.name())
               .eq(InteractionDO::getTargetId, targetId)
               .eq(InteractionDO::getActionType, ActionType.COMMENT.name())
               .eq(InteractionDO::getDeleted, 0);
        
        // 游标分页：查询 ID 小于 cursor 的记录
        if (cursor != null && cursor > 0) {
            wrapper.lt(InteractionDO::getId, cursor);
        }
        
        // 按 ID 倒序（即按时间倒序，因为雪花算法 ID 包含时间信息）
        wrapper.orderByDesc(InteractionDO::getId)
               .last("LIMIT " + (pageSize + 1));  // 多查一条用于判断是否有下一页
        
        List<InteractionDO> interactionDOs = interactionMapper.selectList(wrapper);
        
        List<CommentDTO> comments = new ArrayList<>();
        Long nextCursor = null;
        boolean hasMore = false;
        
        for (int i = 0; i < interactionDOs.size(); i++) {
            InteractionDO interactionDO = interactionDOs.get(i);
            if (i < pageSize) {
                CommentDTO dto = convertToDTO(interactionDO);
                comments.add(dto);
            } else {
                // 多查出来的那条，用于判断是否有下一页
                hasMore = true;
                nextCursor = interactionDOs.get(pageSize - 1).getId();
            }
        }
        
        // 如果正好取满 pageSize，且总数等于 pageSize+1，则 hasMore=true
        if (interactionDOs.size() > pageSize) {
            hasMore = true;
            nextCursor = interactionDOs.get(pageSize - 1).getId();
        }
        
        return new PageResult<>(comments, nextCursor, hasMore);
    }
    
    private CommentDTO convertToDTO(InteractionDO interactionDO) {
        CommentDTO dto = new CommentDTO();
        dto.setId(interactionDO.getId());
        dto.setUserId(interactionDO.getUserId());
        dto.setTargetId(interactionDO.getTargetId());
        dto.setTargetType(interactionDO.getTargetType());
        dto.setContent(interactionDO.getContent());
        dto.setReplyToId(interactionDO.getReplyToId());
        dto.setCreatedAt(interactionDO.getCreatedAt());
        return dto;
    }
}

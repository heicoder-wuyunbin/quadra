package com.quadra.interaction.application.port.out;

import com.quadra.interaction.application.port.in.dto.CommentDTO;
import com.quadra.interaction.application.port.in.dto.PageResult;
import com.quadra.interaction.domain.model.TargetType;

/**
 * 互动查询端口
 */
public interface InteractionQueryPort {
    /**
     * 游标分页查询评论列表
     */
    PageResult<CommentDTO> findCommentsByTarget(TargetType targetType, Long targetId, Long cursor, int pageSize);
}

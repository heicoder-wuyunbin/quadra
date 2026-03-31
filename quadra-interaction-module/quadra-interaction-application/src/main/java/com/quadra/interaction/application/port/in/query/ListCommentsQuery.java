package com.quadra.interaction.application.port.in.query;

import com.quadra.interaction.application.port.in.dto.CommentDTO;
import com.quadra.interaction.application.port.in.dto.PageResult;
import com.quadra.interaction.domain.model.TargetType;

/**
 * 评论列表查询
 */
public interface ListCommentsQuery {
    /**
     * 按目标类型和目标ID游标分页查询评论列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param cursor 游标（上一页最后一条记录的ID）
     * @param pageSize 每页大小
     * @return 评论分页结果
     */
    PageResult<CommentDTO> listComments(TargetType targetType, Long targetId, Long cursor, int pageSize);
}

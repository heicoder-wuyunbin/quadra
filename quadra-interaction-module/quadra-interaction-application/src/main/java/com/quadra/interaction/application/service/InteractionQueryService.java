package com.quadra.interaction.application.service;

import com.quadra.interaction.application.port.in.dto.CommentDTO;
import com.quadra.interaction.application.port.in.dto.PageResult;
import com.quadra.interaction.application.port.in.query.ListCommentsQuery;
import com.quadra.interaction.application.port.out.InteractionQueryPort;
import com.quadra.interaction.domain.model.TargetType;
import org.springframework.stereotype.Service;

@Service
public class InteractionQueryService implements ListCommentsQuery {

    private final InteractionQueryPort interactionQueryPort;

    public InteractionQueryService(InteractionQueryPort interactionQueryPort) {
        this.interactionQueryPort = interactionQueryPort;
    }

    @Override
    public PageResult<CommentDTO> listComments(TargetType targetType, Long targetId, Long cursor, int pageSize) {
        // 直接通过读模型端口查询，不需要组装 Domain 对象
        return interactionQueryPort.findCommentsByTarget(targetType, targetId, cursor, pageSize);
    }
}

package com.quadra.content.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.content.adapter.out.persistence.entity.MovementDO;
import com.quadra.content.adapter.out.persistence.entity.MovementInboxDO;
import com.quadra.content.adapter.out.persistence.mapper.MovementInboxMapper;
import com.quadra.content.adapter.out.persistence.mapper.MovementMapper;
import com.quadra.content.application.port.in.dto.PageResult;
import com.quadra.content.application.port.in.dto.TimelineItemDTO;
import com.quadra.content.application.port.out.TimelineQueryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TimelineQueryRepositoryImpl implements TimelineQueryPort {

    private final MovementInboxMapper movementInboxMapper;
    private final MovementMapper movementMapper;

    public TimelineQueryRepositoryImpl(MovementInboxMapper movementInboxMapper, MovementMapper movementMapper) {
        this.movementInboxMapper = movementInboxMapper;
        this.movementMapper = movementMapper;
    }

    @Override
    public PageResult<TimelineItemDTO> queryTimeline(Long userId, int pageNo, int pageSize) {
        // 1. 查询用户的 inbox
        LambdaQueryWrapper<MovementInboxDO> inboxWrapper = new LambdaQueryWrapper<>();
        inboxWrapper.eq(MovementInboxDO::getOwnerId, userId)
                    .eq(MovementInboxDO::getDeleted, 0)
                    .orderByDesc(MovementInboxDO::getCreatedAt);
        
        Page<MovementInboxDO> inboxPage = new Page<>(pageNo, pageSize);
        movementInboxMapper.selectPage(inboxPage, inboxWrapper);
        
        // 2. 获取所有 movementId
        List<Long> movementIds = inboxPage.getRecords().stream()
                .map(MovementInboxDO::getMovementId)
                .collect(Collectors.toList());
        
        if (movementIds.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0, pageNo, pageSize);
        }
        
        // 3. 批量查询 movement 详情
        List<MovementDO> movements = movementMapper.selectBatchIds(movementIds);
        Map<Long, MovementDO> movementMap = movements.stream()
                .collect(Collectors.toMap(MovementDO::getId, m -> m));
        
        // 4. 组装结果
        List<TimelineItemDTO> items = new ArrayList<>();
        for (MovementInboxDO inbox : inboxPage.getRecords()) {
            MovementDO movement = movementMap.get(inbox.getMovementId());
            if (movement != null && movement.getDeleted() == 0) {
                items.add(new TimelineItemDTO(
                    inbox.getId(),
                    movement.getId(),
                    movement.getUserId(),
                    null, // authorNickname 需要从 user 服务获取
                    null, // authorAvatar 需要从 user 服务获取
                    movement.getTextContent(),
                    movement.getMedias(),
                    movement.getLikeCount(),
                    movement.getCommentCount(),
                    inbox.getCreatedAt()
                ));
            }
        }
        
        return PageResult.of(items, inboxPage.getTotal(), pageNo, pageSize);
    }
}

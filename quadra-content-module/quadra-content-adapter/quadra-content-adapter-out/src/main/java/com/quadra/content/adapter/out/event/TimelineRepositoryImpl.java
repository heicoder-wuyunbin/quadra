package com.quadra.content.adapter.out.event;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.content.adapter.out.persistence.entity.MovementInboxDO;
import com.quadra.content.adapter.out.persistence.mapper.MovementInboxMapper;
import com.quadra.content.application.port.out.TimelineRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TimelineRepositoryImpl implements TimelineRepositoryPort {

    private final MovementInboxMapper movementInboxMapper;

    public TimelineRepositoryImpl(MovementInboxMapper movementInboxMapper) {
        this.movementInboxMapper = movementInboxMapper;
    }

    @Override
    public void batchInsertInbox(Long movementId, Long authorId, List<Long> followerIds) {
        List<MovementInboxDO> inboxList = new ArrayList<>();
        
        for (Long followerId : followerIds) {
            MovementInboxDO inbox = new MovementInboxDO();
            inbox.setId(IdWorker.getId());
            inbox.setOwnerId(followerId);
            inbox.setAuthorId(authorId);
            inbox.setMovementId(movementId);
            inbox.setVersion(0);
            inbox.setDeleted(0);
            inboxList.add(inbox);
        }
        
        // 批量插入
        for (MovementInboxDO inbox : inboxList) {
            movementInboxMapper.insert(inbox);
        }
    }
}

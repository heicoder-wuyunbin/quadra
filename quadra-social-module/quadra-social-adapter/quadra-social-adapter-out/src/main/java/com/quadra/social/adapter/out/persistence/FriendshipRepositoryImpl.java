package com.quadra.social.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.social.adapter.out.persistence.entity.FriendshipDO;
import com.quadra.social.adapter.out.persistence.mapper.FriendshipMapper;
import com.quadra.social.application.port.out.FriendshipRepositoryPort;
import com.quadra.social.domain.model.Friendship;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepositoryPort {

    private final FriendshipMapper friendshipMapper;

    public FriendshipRepositoryImpl(FriendshipMapper friendshipMapper) {
        this.friendshipMapper = friendshipMapper;
    }

    @Override
    @Transactional
    public void saveBidirectional(Friendship friendshipAB, Friendship friendshipBA) {
        // 同事务内写入 A→B 和 B→A 两条记录
        FriendshipDO friendshipDOAB = convertToDO(friendshipAB);
        FriendshipDO friendshipDOBA = convertToDO(friendshipBA);
        
        friendshipMapper.insert(friendshipDOAB);
        friendshipMapper.insert(friendshipDOBA);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private FriendshipDO convertToDO(Friendship friendship) {
        FriendshipDO friendshipDO = new FriendshipDO();
        friendshipDO.setId(friendship.getId());
        friendshipDO.setUserId(friendship.getUserId());
        friendshipDO.setFriendId(friendship.getFriendId());
        friendshipDO.setVersion(friendship.getVersion());
        friendshipDO.setDeleted(friendship.getDeleted());
        friendshipDO.setCreatedAt(friendship.getCreatedAt());
        friendshipDO.setUpdatedAt(friendship.getUpdatedAt());
        return friendshipDO;
    }
}

package com.quadra.social.application.port.out;

import com.quadra.social.domain.model.Friendship;

public interface FriendshipRepositoryPort {
    /**
     * 保存好友关系（同事务内写入A→B和B→A两条记录）
     */
    void saveBidirectional(Friendship friendshipAB, Friendship friendshipBA);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}

package com.quadra.social.application.service;

import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.in.query.ListFollowersQuery;
import com.quadra.social.application.port.in.query.ListFollowingQuery;
import com.quadra.social.application.port.out.FollowQueryPort;
import org.springframework.stereotype.Service;

@Service
public class SocialQueryService implements ListFollowersQuery, ListFollowingQuery {

    private final FollowQueryPort followQueryPort;

    public SocialQueryService(FollowQueryPort followQueryPort) {
        this.followQueryPort = followQueryPort;
    }

    @Override
    public PageResult<FollowerDTO> listFollowers(Long userId, int pageNo, int pageSize) {
        return followQueryPort.findFollowers(userId, pageNo, pageSize);
    }

    @Override
    public PageResult<FollowerDTO> listFollowing(Long userId, int pageNo, int pageSize) {
        return followQueryPort.findFollowing(userId, pageNo, pageSize);
    }
}

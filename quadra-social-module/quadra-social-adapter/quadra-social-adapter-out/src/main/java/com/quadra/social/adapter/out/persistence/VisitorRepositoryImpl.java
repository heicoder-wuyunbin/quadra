package com.quadra.social.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.social.adapter.out.persistence.entity.UserVisitorDO;
import com.quadra.social.adapter.out.persistence.mapper.UserVisitorMapper;
import com.quadra.social.application.port.out.VisitorRepositoryPort;
import com.quadra.social.domain.model.UserVisitor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VisitorRepositoryImpl implements VisitorRepositoryPort {

    private final UserVisitorMapper userVisitorMapper;

    public VisitorRepositoryImpl(UserVisitorMapper userVisitorMapper) {
        this.userVisitorMapper = userVisitorMapper;
    }

    @Override
    @Transactional
    public void save(UserVisitor visitor) {
        UserVisitorDO visitorDO = convertToDO(visitor);
        userVisitorMapper.insert(visitorDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private UserVisitorDO convertToDO(UserVisitor visitor) {
        UserVisitorDO visitorDO = new UserVisitorDO();
        visitorDO.setId(visitor.getId());
        visitorDO.setUserId(visitor.getUserId());
        visitorDO.setVisitorId(visitor.getVisitorId());
        visitorDO.setVisitDate(visitor.getVisitDate());
        visitorDO.setVisitTime(visitor.getVisitTime());
        visitorDO.setSource(visitor.getSource());
        visitorDO.setScore(visitor.getScore());
        visitorDO.setVersion(visitor.getVersion());
        visitorDO.setDeleted(visitor.getDeleted());
        visitorDO.setCreatedAt(visitor.getCreatedAt());
        visitorDO.setUpdatedAt(visitor.getUpdatedAt());
        return visitorDO;
    }
}

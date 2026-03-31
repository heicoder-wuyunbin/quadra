package com.quadra.recommend.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.recommend.adapter.out.persistence.entity.UserActionLogDO;
import com.quadra.recommend.adapter.out.persistence.mapper.UserActionLogMapper;
import com.quadra.recommend.application.port.out.ActionRepositoryPort;
import com.quadra.recommend.domain.model.UserActionLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 行为仓库实现
 */
@Repository
public class ActionRepositoryImpl implements ActionRepositoryPort {

    private final UserActionLogMapper userActionLogMapper;

    public ActionRepositoryImpl(UserActionLogMapper userActionLogMapper) {
        this.userActionLogMapper = userActionLogMapper;
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    @Override
    public void save(UserActionLog actionLog) {
        UserActionLogDO actionLogDO = convertToDO(actionLog);
        userActionLogMapper.insert(actionLogDO);
    }

    @Override
    @Transactional
    public void saveAll(List<UserActionLog> actionLogs) {
        for (UserActionLog actionLog : actionLogs) {
            UserActionLogDO actionLogDO = convertToDO(actionLog);
            userActionLogMapper.insert(actionLogDO);
        }
    }

    private UserActionLogDO convertToDO(UserActionLog actionLog) {
        UserActionLogDO actionLogDO = new UserActionLogDO();
        actionLogDO.setId(actionLog.getId());
        actionLogDO.setUserId(actionLog.getUserId());
        actionLogDO.setTargetId(actionLog.getTargetId());
        actionLogDO.setTargetType(actionLog.getTargetType().name());
        actionLogDO.setActionType(actionLog.getActionType().name());
        actionLogDO.setWeight(actionLog.getWeight());
        actionLogDO.setVersion(actionLog.getVersion());
        actionLogDO.setCreatedAt(actionLog.getCreatedAt());
        return actionLogDO;
    }
}

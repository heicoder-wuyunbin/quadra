package com.quadra.recommend.application.port.out;

import com.quadra.recommend.domain.model.UserActionLog;
import java.util.List;

/**
 * 行为仓库端口
 */
public interface ActionRepositoryPort {
    
    /**
     * 获取下一个ID
     * @return 雪花算法生成的ID
     */
    Long nextId();
    
    /**
     * 保存单条行为日志
     * @param actionLog 行为日志聚合根
     */
    void save(UserActionLog actionLog);
    
    /**
     * 批量保存行为日志
     * @param actionLogs 行为日志聚合根列表
     */
    void saveAll(List<UserActionLog> actionLogs);
}

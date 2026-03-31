package com.quadra.recommend.application.port.in;

import com.quadra.recommend.application.port.in.command.RecordActionCommand;
import java.util.List;

/**
 * 记录用户行为用例接口
 */
public interface RecordActionUseCase {
    
    /**
     * 记录单条用户行为
     * @param command 行为命令
     * @return 行为日志ID
     */
    Long recordAction(RecordActionCommand command);
    
    /**
     * 批量记录用户行为
     * @param commands 行为命令列表
     */
    void recordActions(List<RecordActionCommand> commands);
}

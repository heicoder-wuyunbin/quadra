package com.quadra.content.application.port.in;

import com.quadra.content.application.port.in.command.PublishMovementCommand;

/**
 * 发布图文动态用例
 */
public interface PublishMovementUseCase {
    /**
     * 发布图文动态
     * @param command 发布指令
     * @return 动态ID
     */
    Long publishMovement(PublishMovementCommand command);
}

package com.quadra.content.application.port.in;

import com.quadra.content.application.port.in.command.DeleteMovementCommand;

/**
 * 删除图文动态用例
 */
public interface DeleteMovementUseCase {
    /**
     * 删除图文动态
     * @param command 删除指令
     */
    void deleteMovement(DeleteMovementCommand command);
}

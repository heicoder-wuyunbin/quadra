package com.quadra.social.application.port.in;

import com.quadra.social.application.port.in.command.UnfollowUserCommand;

public interface UnfollowUserUseCase {
    /**
     * 取消关注
     * @param command 取消关注指令
     */
    void unfollow(UnfollowUserCommand command);
}

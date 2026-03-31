package com.quadra.social.application.port.in;

import com.quadra.social.application.port.in.command.FollowUserCommand;

public interface FollowUserUseCase {
    /**
     * 关注用户
     * @param command 关注指令
     */
    void follow(FollowUserCommand command);
}

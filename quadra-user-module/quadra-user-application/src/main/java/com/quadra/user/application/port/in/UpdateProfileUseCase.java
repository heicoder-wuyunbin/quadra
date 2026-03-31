package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.UpdateProfileCommand;

public interface UpdateProfileUseCase {
    /**
     * 更新用户资料
     */
    void updateProfile(UpdateProfileCommand command);
}

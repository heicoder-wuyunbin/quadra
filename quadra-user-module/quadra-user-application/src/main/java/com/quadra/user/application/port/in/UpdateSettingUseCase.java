package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.UpdateSettingCommand;

public interface UpdateSettingUseCase {
    /**
     * 更新用户偏好设置
     */
    void updateSetting(UpdateSettingCommand command);
}

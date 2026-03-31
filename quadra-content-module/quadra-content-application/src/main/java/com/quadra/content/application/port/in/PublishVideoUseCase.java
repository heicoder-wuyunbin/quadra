package com.quadra.content.application.port.in;

import com.quadra.content.application.port.in.command.PublishVideoCommand;

/**
 * 发布短视频用例
 */
public interface PublishVideoUseCase {
    /**
     * 发布短视频
     * @param command 发布指令
     * @return 视频ID
     */
    Long publishVideo(PublishVideoCommand command);
}

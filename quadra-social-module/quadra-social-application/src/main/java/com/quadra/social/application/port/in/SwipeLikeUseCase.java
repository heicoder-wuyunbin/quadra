package com.quadra.social.application.port.in;

import com.quadra.social.application.port.in.command.SwipeLikeCommand;
import com.quadra.social.application.port.in.dto.MatchResultDTO;

public interface SwipeLikeUseCase {
    /**
     * 滑动操作（LIKE/DISLIKE）
     * @param command 滑动指令
     * @return 匹配结果（如果双方互相喜欢则返回匹配信息）
     */
    MatchResultDTO swipe(SwipeLikeCommand command);
}

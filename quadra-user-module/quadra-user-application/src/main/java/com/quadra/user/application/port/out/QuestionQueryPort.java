package com.quadra.user.application.port.out;

import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.dto.QuestionItemDTO;

public interface QuestionQueryPort {
    PageResult<QuestionItemDTO> listByUserId(Long userId, int pageNo, int pageSize);
}

package com.quadra.user.application.port.in.query;

import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.dto.QuestionItemDTO;

public interface ListMyQuestionsQuery {
    PageResult<QuestionItemDTO> listQuestions(Long userId, int pageNo, int pageSize);
}

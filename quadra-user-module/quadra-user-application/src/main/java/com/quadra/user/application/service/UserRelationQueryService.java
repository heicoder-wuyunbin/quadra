package com.quadra.user.application.service;

import com.quadra.user.application.port.in.dto.BlacklistItemDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.dto.QuestionItemDTO;
import com.quadra.user.application.port.in.query.ListMyBlacklistQuery;
import com.quadra.user.application.port.in.query.ListMyQuestionsQuery;
import com.quadra.user.application.port.out.BlacklistQueryPort;
import com.quadra.user.application.port.out.QuestionQueryPort;
import org.springframework.stereotype.Service;

@Service
public class UserRelationQueryService implements ListMyBlacklistQuery, ListMyQuestionsQuery {

    private final BlacklistQueryPort blacklistQueryPort;
    private final QuestionQueryPort questionQueryPort;

    public UserRelationQueryService(BlacklistQueryPort blacklistQueryPort, QuestionQueryPort questionQueryPort) {
        this.blacklistQueryPort = blacklistQueryPort;
        this.questionQueryPort = questionQueryPort;
    }

    @Override
    public PageResult<BlacklistItemDTO> listBlacklist(Long userId, int pageNo, int pageSize) {
        return blacklistQueryPort.listByUserId(userId, pageNo, pageSize);
    }

    @Override
    public PageResult<QuestionItemDTO> listQuestions(Long userId, int pageNo, int pageSize) {
        return questionQueryPort.listByUserId(userId, pageNo, pageSize);
    }
}

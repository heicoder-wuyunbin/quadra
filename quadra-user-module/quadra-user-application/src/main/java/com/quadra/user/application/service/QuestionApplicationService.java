package com.quadra.user.application.service;

import com.quadra.user.application.port.in.QuestionUseCase;
import com.quadra.user.application.port.in.command.AddQuestionCommand;
import com.quadra.user.application.port.in.command.DisableQuestionCommand;
import com.quadra.user.application.port.in.command.UpdateQuestionCommand;
import com.quadra.user.application.port.out.QuestionRepositoryPort;
import com.quadra.user.domain.exception.DomainException;
import com.quadra.user.domain.model.question.StrangerQuestion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionApplicationService implements QuestionUseCase {

    private final QuestionRepositoryPort questionRepositoryPort;

    public QuestionApplicationService(QuestionRepositoryPort questionRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
    }

    @Override
    public Long addQuestion(AddQuestionCommand command) {
        // 限制每个用户最多添加 3 个问题
        List<StrangerQuestion> questions = questionRepositoryPort.findByUserId(command.userId());
        if (questions.size() >= 3) {
            throw new DomainException("最多只能设置3个破冰问题");
        }

        Long id = questionRepositoryPort.nextId();
        StrangerQuestion question = StrangerQuestion.create(id, command.userId(), command.question(), command.sortOrder());
        
        questionRepositoryPort.save(question);
        return id;
    }

    @Override
    public void updateQuestion(UpdateQuestionCommand command) {
        StrangerQuestion question = questionRepositoryPort.findById(command.questionId());
        if (question == null || !question.getUserId().equals(command.userId())) {
            throw new DomainException("破冰问题不存在或无权操作");
        }

        question.updateContent(command.question(), command.sortOrder());
        questionRepositoryPort.update(question);
    }

    @Override
    public void disableQuestion(DisableQuestionCommand command) {
        StrangerQuestion question = questionRepositoryPort.findById(command.questionId());
        if (question == null || !question.getUserId().equals(command.userId())) {
            throw new DomainException("破冰问题不存在或无权操作");
        }

        question.disable();
        questionRepositoryPort.update(question);
    }
}

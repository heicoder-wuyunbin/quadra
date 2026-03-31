package com.quadra.user.application.port.in;

import com.quadra.user.application.port.in.command.AddQuestionCommand;
import com.quadra.user.application.port.in.command.DisableQuestionCommand;
import com.quadra.user.application.port.in.command.UpdateQuestionCommand;

public interface QuestionUseCase {
    Long addQuestion(AddQuestionCommand command);
    void updateQuestion(UpdateQuestionCommand command);
    void disableQuestion(DisableQuestionCommand command);
}

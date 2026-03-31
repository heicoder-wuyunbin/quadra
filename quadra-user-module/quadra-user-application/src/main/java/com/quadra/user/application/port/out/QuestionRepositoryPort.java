package com.quadra.user.application.port.out;

import com.quadra.user.domain.model.question.StrangerQuestion;
import java.util.List;

public interface QuestionRepositoryPort {
    void save(StrangerQuestion question);
    void update(StrangerQuestion question);
    StrangerQuestion findById(Long id);
    List<StrangerQuestion> findByUserId(Long userId);
    Long nextId();
}

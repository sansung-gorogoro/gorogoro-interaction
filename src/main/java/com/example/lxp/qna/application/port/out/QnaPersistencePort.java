package com.example.lxp.qna.application.port.out;

import com.example.lxp.qna.domain.model.Question;

import java.util.Optional;

public interface QnaPersistencePort {

    Question save(Question question);

    Optional<Question> findById(Long questionId);

    void delete(Question question);

    void deleteByThreadId(String threadId);
    
}

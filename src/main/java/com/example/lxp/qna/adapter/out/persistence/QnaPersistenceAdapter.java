package com.example.lxp.qna.adapter.out.persistence;

import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QnaPersistenceAdapter implements QnaPersistencePort {

    private final QnaRepository qnaRepository;

    public QnaPersistenceAdapter(QnaRepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    @Override
    public Question save(Question question) {
        return qnaRepository.save(question);
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return qnaRepository.findById(questionId);
    }

    @Override
    public void delete(Question question) {
        qnaRepository.delete(question);
    }

    @Override
    public void deleteByThreadId(String threadId) {
        qnaRepository.deleteByThreadId(threadId);
    }
}

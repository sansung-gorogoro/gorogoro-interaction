package com.example.lxp.qna.application.service;

import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class QnaQueryService implements QuestionQueryUseCase {

    private final QnaPersistencePort qnaPersistencePort;

    public QnaQueryService(QnaPersistencePort qnaPersistencePort) {
        this.qnaPersistencePort = qnaPersistencePort;
    }

    @Override
    public List<Question> findUnansweredQuestionsForInstructor(Long instructorId, int limit) {
        return qnaPersistencePort.findOpenedRootQuestionsByInstructorId(instructorId, limit);
    }

}

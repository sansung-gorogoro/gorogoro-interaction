package com.example.lxp.qna.application.port.in;

import com.example.lxp.qna.domain.model.Question;

import java.util.List;

public interface QuestionQueryUseCase {

    List<Question> findUnansweredQuestionsForInstructor(Long instructorId, int limit);

}

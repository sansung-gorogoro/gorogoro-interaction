package com.example.lxp.qna.application.port.in;

import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionItem;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsResponse;
import com.example.lxp.qna.application.port.in.dto.GetLessonQuestionsQuery;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;
import com.example.lxp.qna.domain.model.Question;

import java.util.List;

public interface QuestionQueryUseCase {

    List<Question> findUnansweredQuestionsForInstructor(GetUnansweredQuestionsQuery query);

    LessonQuestionsResponse findLessonRootQuestions(GetLessonQuestionsQuery query);

    List<LessonQuestionItem> findLessonRootQuestionsAll(Long courseId, Long lessonId);

}

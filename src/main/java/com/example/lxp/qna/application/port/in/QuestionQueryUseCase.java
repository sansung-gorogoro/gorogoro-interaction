package com.example.lxp.qna.application.port.in;

import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsAllResponse;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsResponse;
import com.example.lxp.qna.adapter.in.web.dto.QuestionThreadResponse;
import com.example.lxp.qna.adapter.in.web.dto.UnansweredQuestionsResponse;
import com.example.lxp.qna.application.port.in.dto.GetLessonQuestionsQuery;
import com.example.lxp.qna.application.port.in.dto.GetQnaThreadQuery;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;

public interface QuestionQueryUseCase {

    UnansweredQuestionsResponse findUnansweredQuestionsForInstructor(GetUnansweredQuestionsQuery query);

    LessonQuestionsResponse findLessonRootQuestions(GetLessonQuestionsQuery query);

    LessonQuestionsAllResponse findLessonRootQuestionsAll(Long courseId, Long lessonId);

    QuestionThreadResponse getThread(GetQnaThreadQuery query);

}

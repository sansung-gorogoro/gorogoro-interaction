package com.example.lxp.qna.adapter.in.web.dto;

import java.util.List;

public record LessonQuestionsAllResponse(
        List<LessonQuestionItem> questions
) {
}

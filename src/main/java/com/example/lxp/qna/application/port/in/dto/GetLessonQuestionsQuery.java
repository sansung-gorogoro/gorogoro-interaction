package com.example.lxp.qna.application.port.in.dto;

public record GetLessonQuestionsQuery(
        Long courseId,
        Long lessonId,
        int page,
        int size
) {
}

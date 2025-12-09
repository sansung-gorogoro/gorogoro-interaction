package com.example.lxp.qna.application.port.in.dto;

public record CreateQuestionCommand(
        Long rootId,
        String threadId,
        Long courseId,
        Long lessonId,
        String title,
        String comment,
        Long authorId
) {
}

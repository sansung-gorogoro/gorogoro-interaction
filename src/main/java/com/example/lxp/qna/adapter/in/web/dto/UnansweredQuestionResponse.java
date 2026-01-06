package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;

import java.time.Instant;

public record UnansweredQuestionResponse(
        Long questionId,
        Long courseId,
        Long lessonId,
        String title,
        Long authorId,
        Instant lastActivityAt
) {

    public static UnansweredQuestionResponse from(Question question) {
        return new UnansweredQuestionResponse(
                question.getId(),
                question.getCourseId(),
                question.getLessonId(),
                question.getTitle(),
                question.getAuthorId(),
                question.getLastActivityAt()
        );
    }

}

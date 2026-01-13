package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;

import java.time.Instant;

public record LessonQuestionItem(
        Long questionId,
        Long courseId,
        Long lessonId,
        String title,
        Long authorId,
        QuestionStatus status,
        Long replyCount,
        Instant lastActivityAt
) {

    public static LessonQuestionItem from(Question question, Long replyCount) {
        return new LessonQuestionItem(
                question.getId(),
                question.getCourseId(),
                question.getLessonId(),
                question.getTitle(),
                question.getAuthorId(),
                question.getStatus(),
                replyCount,
                question.getLastActivityAt()
        );
    }

}

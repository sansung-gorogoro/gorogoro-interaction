package com.example.lxp.qna.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.qna.domain.model.Question;

public record QuestionCreatedEvent(

        Long questionId,
        String threadId,
        Long courseId,
        Long lessonId,
        Long authorId

) implements DomainEvent {

    public static QuestionCreatedEvent from(Question question) {
        return new QuestionCreatedEvent(
                question.getId(),
                question.getThreadId(),
                question.getCourseId(),
                question.getLessonId(),
                question.getAuthorId()
        );
    }

    @Override
    public String type() {
        return "";
    }

    @Override
    public String version() {
        return DomainEvent.super.version();
    }

}

package com.example.lxp.qna.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.qna.domain.model.Question;

public class QuestionCreatedEvent implements DomainEvent {

    private static final String SERVICE_PREFIX = "qna";
    private static final String EVENT_NAME = "question-created";

    private final Long questionId;
    private final String threadId;
    private final Long courseId;
    private final Long lessonId;
    private final Long authorId;

    private QuestionCreatedEvent(
            Long questionId,
            String threadId,
            Long courseId,
            Long lessonId,
            Long authorId
    ) {
        this.questionId = questionId;
        this.threadId = threadId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.authorId = authorId;
    }

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
    public String getServicePrefix() {
        return SERVICE_PREFIX;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getThreadId() {
        return threadId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public Long getAuthorId() {
        return authorId;
    }
    
}

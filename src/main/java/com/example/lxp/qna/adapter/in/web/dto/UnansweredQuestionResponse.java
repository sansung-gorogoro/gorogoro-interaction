package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnansweredQuestionResponse {

    private final Long questionId;
    private final Long courseId;
    private final Long lessonId;
    private final String title;
    private final Long authorId;
    private final String authorNickname;
    private final Instant lastActivityAt;

    public static UnansweredQuestionResponse from(Question question, String authorNickname) {
        return new UnansweredQuestionResponse(
                question.getId(),
                question.getCourseId(),
                question.getLessonId(),
                question.getTitle(),
                question.getAuthorId(),
                authorNickname,
                question.getLastActivityAt()
        );
    }

}

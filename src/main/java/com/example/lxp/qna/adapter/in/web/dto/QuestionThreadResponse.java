package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionThreadResponse {

    private final String threadId;
    private final Long courseId;
    private final Long lessonId;
    private final Long instructorId;
    private final QuestionStatus status;
    private final Instant lastActivityAt;
    private final List<QuestionThreadItemResponse> questions;

    public static QuestionThreadResponse of(Question root, List<QuestionThreadItemResponse> items) {
        return new QuestionThreadResponse(
                root.getThreadId(),
                root.getCourseId(),
                root.getLessonId(),
                root.getInstructorId(),
                root.getStatus(),
                root.getLastActivityAt(),
                items
        );
    }

}

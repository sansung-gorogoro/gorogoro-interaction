package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LessonQuestionItem {

    private final Long questionId;
    private final Long courseId;
    private final Long lessonId;
    private final String title;
    private final Long authorId;
    private final String authorNickname;
    private final QuestionStatus status;
    private final Long replyCount;
    private final Instant lastActivityAt;

    public static LessonQuestionItem from(Question question, Long replyCount, String authorNickname) {
        return new LessonQuestionItem(
                question.getId(),
                question.getCourseId(),
                question.getLessonId(),
                question.getTitle(),
                question.getAuthorId(),
                authorNickname,
                question.getStatus(),
                replyCount,
                question.getLastActivityAt()
        );
    }

}

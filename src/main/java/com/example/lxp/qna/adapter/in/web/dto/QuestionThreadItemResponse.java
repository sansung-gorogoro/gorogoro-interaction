package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionThreadItemResponse {

    private final Long questionId;
    private final boolean isRoot;
    private final String title;
    private final String content;
    private final Long authorId;
    private final Instant createdAt;

    public static QuestionThreadItemResponse from(Question question) {
        return new QuestionThreadItemResponse(
                question.getId(),
                question.isRoot(),
                question.getTitle(),
                question.getContent(),
                question.getAuthorId(),
                question.getCreatedAt()
        );
    }

}

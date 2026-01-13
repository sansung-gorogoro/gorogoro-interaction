package com.example.lxp.qna.adapter.in.web.dto;

import java.time.Instant;

public record QuestionThreadItemResponse(
        Long questionId,
        boolean isRoot,
        String title,
        String content,
        Long authorId,
        Instant createdAt
) {
}

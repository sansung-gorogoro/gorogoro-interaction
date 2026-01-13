package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.QuestionStatus;

import java.time.Instant;
import java.util.List;

public record QuestionThreadResponse(
        String threadId,
        Long courseId,
        Long lessonId,
        Long instructorId,
        QuestionStatus status,
        Instant lastActivityAt,
        List<QuestionThreadItemResponse> questions
) {
}

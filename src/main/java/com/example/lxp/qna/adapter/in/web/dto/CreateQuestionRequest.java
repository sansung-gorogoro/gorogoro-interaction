package com.example.lxp.qna.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQuestionRequest(
        Long rootId,
        String threadId,
        String title,
        @NotBlank(message = "Content is required")
        String content
) {
}

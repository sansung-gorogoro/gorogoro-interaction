package com.example.lxp.qna.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQuestionRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}

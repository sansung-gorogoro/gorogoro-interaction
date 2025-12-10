package com.example.lxp.qna.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AddAnswerRequest(
    @NotBlank String content
) {}

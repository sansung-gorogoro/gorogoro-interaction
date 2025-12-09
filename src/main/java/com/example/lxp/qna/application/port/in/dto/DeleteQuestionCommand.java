package com.example.lxp.qna.application.port.in.dto;

public record DeleteQuestionCommand(
        Long questionId,
        Long authorId
) {
}

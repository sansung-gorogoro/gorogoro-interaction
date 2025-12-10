package com.example.lxp.qna.application.port.in.dto;

public record UpdateQuestionCommand(
        Long questionId,
        String title,
        String comment,
        Long authorId
) {
}

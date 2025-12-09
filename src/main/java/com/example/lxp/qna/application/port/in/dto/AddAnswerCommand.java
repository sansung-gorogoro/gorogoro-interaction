package com.example.lxp.qna.application.port.in.dto;

public record AddAnswerCommand(
    Long rootQuestionId,
    Long authorId,
    String content,
    Long courseId,
    Long lessonId
) {}

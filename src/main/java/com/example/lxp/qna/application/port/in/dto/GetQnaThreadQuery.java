package com.example.lxp.qna.application.port.in.dto;

public record GetQnaThreadQuery(
        Long courseId,
        Long lessonId,
        Long questionId
) {
}

package com.example.lxp.qna.application.port.in.dto;

import com.example.lxp.common.auth.model.User;

public record CreateQuestionCommand(
        Long courseId,
        Long lessonId,
        String title,
        String comment,
        User user
) {
}

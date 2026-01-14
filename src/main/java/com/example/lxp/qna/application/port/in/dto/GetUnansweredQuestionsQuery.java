package com.example.lxp.qna.application.port.in.dto;

import com.example.lxp.common.auth.model.User;

public record GetUnansweredQuestionsQuery(
        User user,
        Integer limit
) {
}

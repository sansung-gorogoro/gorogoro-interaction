package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;

import java.util.List;

public record UnansweredQuestionsResponse(
        List<UnansweredQuestionResponse> questions
) {

    public static UnansweredQuestionsResponse from(List<Question> questions) {
        List<UnansweredQuestionResponse> items = questions.stream()
                .map(UnansweredQuestionResponse::from)
                .toList();
        return new UnansweredQuestionsResponse(items);
    }

}

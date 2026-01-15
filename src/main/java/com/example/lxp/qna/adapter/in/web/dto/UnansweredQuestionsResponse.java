package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnansweredQuestionsResponse {

    private final List<UnansweredQuestionResponse> questions;

    public static UnansweredQuestionsResponse from(List<Question> questions) {
        List<UnansweredQuestionResponse> items = questions.stream()
                .map(UnansweredQuestionResponse::from)
                .toList();
        return new UnansweredQuestionsResponse(items);
    }

}

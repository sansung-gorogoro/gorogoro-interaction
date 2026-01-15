package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnansweredQuestionsResponse {

    private final List<UnansweredQuestionResponse> questions;

    public static UnansweredQuestionsResponse from(List<Question> questions, Map<Long, String> nicknameMap, String unknownNickname) {
        List<UnansweredQuestionResponse> items = questions.stream()
                .map(question -> UnansweredQuestionResponse.from(
                        question,
                        nicknameMap.getOrDefault(question.getAuthorId(), unknownNickname)
                ))
                .toList();
        return new UnansweredQuestionsResponse(items);
    }

}

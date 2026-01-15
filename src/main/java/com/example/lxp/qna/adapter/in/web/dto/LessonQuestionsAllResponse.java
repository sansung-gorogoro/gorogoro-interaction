package com.example.lxp.qna.adapter.in.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LessonQuestionsAllResponse {

    private final List<LessonQuestionItem> questions;

    public static LessonQuestionsAllResponse of(List<LessonQuestionItem> questions) {
        return new LessonQuestionsAllResponse(questions);
    }

}

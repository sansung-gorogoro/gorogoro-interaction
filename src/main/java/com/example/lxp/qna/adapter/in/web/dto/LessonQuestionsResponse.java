package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LessonQuestionsResponse {

    private final List<LessonQuestionItem> questions;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public static LessonQuestionsResponse from(Page<Question> questionPage, Map<Long, Long> replyCounts) {
        List<LessonQuestionItem> items = questionPage.getContent().stream()
                .map(question -> LessonQuestionItem.from(
                        question,
                        replyCounts.getOrDefault(question.getId(), 0L)
                ))
                .toList();

        return new LessonQuestionsResponse(
                items,
                questionPage.getNumber(),
                questionPage.getSize(),
                questionPage.getTotalPages(),
                questionPage.getTotalElements(),
                questionPage.hasNext(),
                questionPage.hasPrevious()
        );
    }

}

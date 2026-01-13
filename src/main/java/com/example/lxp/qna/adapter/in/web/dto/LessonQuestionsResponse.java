package com.example.lxp.qna.adapter.in.web.dto;

import com.example.lxp.qna.domain.model.Question;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public record LessonQuestionsResponse(
        List<LessonQuestionItem> questions,
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {

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

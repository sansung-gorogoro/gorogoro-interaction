package com.example.lxp.qna.adapter.in.web;

import com.example.lxp.qna.adapter.in.web.dto.UnansweredQuestionsResponse;
import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.domain.model.Question;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/internal/qna")
public class QnaInternalController {

    private static final int DEFAULT_LIMIT = 3;

    private final QuestionQueryUseCase questionQueryUseCase;

    public QnaInternalController(QuestionQueryUseCase questionQueryUseCase) {
        this.questionQueryUseCase = questionQueryUseCase;
    }

    @GetMapping("/instructors/{instructorId}/unanswered")
    public ResponseEntity<UnansweredQuestionsResponse> getUnansweredQuestions(
            @PathVariable @Positive Long instructorId,
            @RequestParam(defaultValue = "3") @Positive int limit
    ) {
        List<Question> questions = questionQueryUseCase.findUnansweredQuestionsForInstructor(
                instructorId,
                Math.min(limit, 10)
        );
        return ResponseEntity.ok(UnansweredQuestionsResponse.from(questions));
    }

}

package com.example.lxp.qna.adapter.in.web;

import com.example.lxp.common.auth.model.User;
import com.example.lxp.qna.adapter.in.web.dto.UnansweredQuestionsResponse;
import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;
import com.example.lxp.qna.domain.model.Question;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/qna/instructors")
public class QnaInstructorController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    private final QuestionQueryUseCase questionQueryUseCase;

    public QnaInstructorController(QuestionQueryUseCase questionQueryUseCase) {
        this.questionQueryUseCase = questionQueryUseCase;
    }

    @GetMapping("/unanswered")
    public ResponseEntity<UnansweredQuestionsResponse> getUnansweredQuestions(
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole,
            @RequestParam(required = false) @Positive Integer limit
    ) {
        User user = User.from(userId, userRole);
        GetUnansweredQuestionsQuery query = new GetUnansweredQuestionsQuery(
                user,
                limit
        );
        List<Question> questions = questionQueryUseCase.findUnansweredQuestionsForInstructor(query);
        return ResponseEntity.ok(UnansweredQuestionsResponse.from(questions));
    }

}

package com.example.lxp.qna.adapter.in.web;

import com.example.lxp.common.auth.model.User;
import com.example.lxp.qna.adapter.in.web.dto.AddAnswerRequest;
import com.example.lxp.qna.adapter.in.web.dto.CreateQuestionRequest;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsAllResponse;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsResponse;
import com.example.lxp.qna.adapter.in.web.dto.QuestionThreadResponse;
import com.example.lxp.qna.adapter.in.web.dto.UpdateQuestionRequest;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.application.port.in.dto.AddAnswerCommand;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.GetLessonQuestionsQuery;
import com.example.lxp.qna.application.port.in.dto.GetQnaThreadQuery;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/courses/{courseId}/lessons/{lessonId}/qna")
public class QnaController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 10;
    private final QuestionCommandUseCase questionCommandUseCase;
    private final QuestionQueryUseCase questionQueryUseCase;

    public QnaController(
            QuestionCommandUseCase questionCommandUseCase,
            QuestionQueryUseCase questionQueryUseCase
    ) {
        this.questionCommandUseCase = questionCommandUseCase;
        this.questionQueryUseCase = questionQueryUseCase;
    }

    /**
     * [TEMP] 전체 root 질문 목록 반환 (pagination 미적용)
     * - 프론트 요청으로 pagination 없이 전체 리스트 반환
     * - 정렬: lastActivityAt DESC, id DESC
     * - Date: 2026-01-13
     */
    @GetMapping
    public ResponseEntity<LessonQuestionsAllResponse> getQuestionsAll(
            @PathVariable @Positive Long courseId,
            @PathVariable @Positive Long lessonId
    ) {
        return ResponseEntity.ok(questionQueryUseCase.findLessonRootQuestionsAll(courseId, lessonId));
    }

    /**
     * [PRESERVED] Pagination 적용 root 질문 목록 조회
     * - 프론트 요청으로 pagination 연결 해제 (데모 우선)
     * - 메서드 삭제하지 말 것: pagination.md 기반 구현을 다음 MVP에서 복구하기 위함
     * - TODO(next MVP): 아래 @GetMapping 주석 해제하고 /qna GET에 재연결, 위 임시 메서드 제거
     * - 기준: docs/pagination.md (page=0-based, size<=10 cap, sort=lastActivityAt desc + id desc, metadata 포함)
     * - Date: 2026-01-13
     */
    // @GetMapping
    public ResponseEntity<LessonQuestionsResponse> getQuestions(
            @PathVariable @Positive Long courseId,
            @PathVariable @Positive Long lessonId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(required = false) @Positive Integer size
    ) {
        int effectiveSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        GetLessonQuestionsQuery query = new GetLessonQuestionsQuery(courseId, lessonId, page, effectiveSize);
        LessonQuestionsResponse response = questionQueryUseCase.findLessonRootQuestions(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}/thread")
    public ResponseEntity<QuestionThreadResponse> getThread(
            @PathVariable @Positive Long courseId,
            @PathVariable @Positive Long lessonId,
            @PathVariable @Positive Long questionId
    ) {
        GetQnaThreadQuery query = new GetQnaThreadQuery(courseId, lessonId, questionId);
        return ResponseEntity.ok(questionQueryUseCase.getThread(query));
    }

    @PostMapping
    public ResponseEntity<Void> createQuestion(
            @PathVariable @Positive Long courseId,
            @PathVariable @Positive Long lessonId,
            @RequestBody @Valid CreateQuestionRequest body,
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole
    ) {
        User user = User.from(userId, userRole);
        CreateQuestionCommand command = new CreateQuestionCommand(
                courseId,
                lessonId,
                body.title(),
                body.content(),
                user
        );
        questionCommandUseCase.createQuestion(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{questionId}/replies")
    public ResponseEntity<Void> addAnswer(
            @PathVariable @Positive Long courseId,
            @PathVariable @Positive Long lessonId,
            @PathVariable @Positive Long questionId,
            @RequestBody @Valid AddAnswerRequest body,
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole
    ) {
        User user = User.from(userId, userRole);
        AddAnswerCommand command = new AddAnswerCommand(
                questionId,
                user,
                body.content(),
                courseId,
                lessonId
        );
        questionCommandUseCase.addAnswer(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable @Positive Long questionId,
            @RequestBody UpdateQuestionRequest body,
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole
    ) {
        User user = User.from(userId, userRole);
        UpdateQuestionCommand command = new UpdateQuestionCommand(
                questionId,
                body.title(),
                body.content(),
                user
        );
        questionCommandUseCase.updateQuestion(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable @Positive Long questionId,
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole
    ) {
        User user = User.from(userId, userRole);
        DeleteQuestionCommand command = new DeleteQuestionCommand(
                questionId,
                user
        );
        questionCommandUseCase.deleteQuestion(command);
        return ResponseEntity.noContent().build();
    }

}

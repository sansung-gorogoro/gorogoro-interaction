package com.example.lxp.qna.adapter.in.web;

import com.example.lxp.qna.adapter.in.web.dto.AddAnswerRequest;
import com.example.lxp.qna.adapter.in.web.dto.CreateQuestionRequest;
import com.example.lxp.qna.adapter.in.web.dto.UpdateQuestionRequest;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
import com.example.lxp.qna.application.port.in.dto.AddAnswerCommand;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons/{lessonId}/qna")
public class QnaController {

    private static final String HEADER_USER_ID = "X-User-Id";

    private final QuestionCommandUseCase questionCommandUseCase;

    public QnaController(QuestionCommandUseCase questionCommandUseCase) {
        this.questionCommandUseCase = questionCommandUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createQuestion(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody @Valid CreateQuestionRequest body,
            @RequestHeader(HEADER_USER_ID) Long userId
    ) {
        CreateQuestionCommand command = new CreateQuestionCommand(
                courseId,
                lessonId,
                body.title(),
                body.content(),
                userId
        );
        questionCommandUseCase.createQuestion(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{questionId}/replies")
    public ResponseEntity<Void> addAnswer(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable Long questionId,
            @RequestBody @Valid AddAnswerRequest body,
            @RequestHeader(HEADER_USER_ID) Long userId
    ) {
        AddAnswerCommand command = new AddAnswerCommand(
                questionId,
                userId,
                body.content(),
                courseId,
                lessonId
        );
        questionCommandUseCase.addAnswer(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest body,
            @RequestHeader(HEADER_USER_ID) Long userId
    ) {
        UpdateQuestionCommand command = new UpdateQuestionCommand(
                questionId,
                body.title(),
                body.content(),
                userId
        );
        questionCommandUseCase.updateQuestion(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId,
            @RequestHeader(HEADER_USER_ID) Long userId
    ) {
        DeleteQuestionCommand command = new DeleteQuestionCommand(
                questionId,
                userId
        );
        questionCommandUseCase.deleteQuestion(command);
        return ResponseEntity.noContent().build();
    }

}

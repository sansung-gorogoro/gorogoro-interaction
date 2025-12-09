package com.example.lxp.qna.adapter.in.web;

import com.example.lxp.qna.adapter.in.web.dto.CreateQuestionRequest;
import com.example.lxp.qna.adapter.in.web.dto.UpdateQuestionRequest;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
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

    // TODO: replace with authenticated user id once credential is integrated
    private static final Long USER_ID_STUB = 1L;

    private final QuestionCommandUseCase questionCommandUseCase;

    public QnaController(QuestionCommandUseCase questionCommandUseCase) {
        this.questionCommandUseCase = questionCommandUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createQuestion(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody @Valid CreateQuestionRequest body
    ) {
        CreateQuestionCommand command = new CreateQuestionCommand(
                body.rootId(),
                body.threadId(),
                courseId,
                lessonId,
                body.title(),
                body.content(),
                USER_ID_STUB
        );
        questionCommandUseCase.createQuestion(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody @Valid UpdateQuestionRequest body
    ) {
        UpdateQuestionCommand command = new UpdateQuestionCommand(
                questionId,
                body.title(),
                body.content(),
                USER_ID_STUB
        );
        questionCommandUseCase.updateQuestion(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId
    ) {
        DeleteQuestionCommand command = new DeleteQuestionCommand(
                questionId,
                USER_ID_STUB
        );
        questionCommandUseCase.deleteQuestion(command);
        return ResponseEntity.noContent().build();
    }
    
}

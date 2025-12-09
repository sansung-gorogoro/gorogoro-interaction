package com.example.lxp.qna.application.service;

import com.example.lxp.common.port.out.PublishEventPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.application.port.out.VerifyInstructorPort;
import com.example.lxp.qna.domain.event.QuestionCreatedEvent;
import com.example.lxp.qna.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class QnaCommandService implements QuestionCommandUseCase {

    private final QnaPersistencePort qnaPersistencePort;
    private final PublishEventPort publishEventPort;
    private final VerifyInstructorPort verifyInstructorPort;

    public QnaCommandService(
            QnaPersistencePort qnaPersistencePort,
            PublishEventPort publishEventPort,
            VerifyInstructorPort verifyInstructorPort
    ) {
        this.qnaPersistencePort = qnaPersistencePort;
        this.publishEventPort = publishEventPort;
        this.verifyInstructorPort = verifyInstructorPort;
    }

    @Override
    public Question createQuestion(CreateQuestionCommand command) {
        if (command.rootId() == null) {
            if (command.threadId() != null && !command.threadId().isBlank()) {
                throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
            }

            Question question = Question.createRoot(
                    command.courseId(),
                    command.lessonId(),
                    command.authorId(),
                    command.title(),
                    command.comment()
            );

            Question savedQuestion = qnaPersistencePort.save(question);
            publishEventPort.publish(QuestionCreatedEvent.from(savedQuestion));
            return savedQuestion;
        }

        Question parentQuestion = qnaPersistencePort.findById(command.rootId())
                .orElseThrow(() -> BusinessException.builder(ErrorCode.QUESTION_NOT_FOUND).build());

        if (!Objects.equals(parentQuestion.getCourseId(), command.courseId())
                || !Objects.equals(parentQuestion.getLessonId(), command.lessonId())) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
        }

        if (command.threadId() != null && !Objects.equals(parentQuestion.getThreadId(), command.threadId())) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
        }

        if (parentQuestion.getRootId() != null) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
        }

        if (!canReply(parentQuestion, command.authorId())) {
            throw BusinessException.builder(ErrorCode.FORBIDDEN_QUESTION_REPLY).build();
        }

        Question reply = Question.createReply(
                command.rootId(),
                parentQuestion.getThreadId(),
                command.courseId(),
                command.lessonId(),
                command.authorId(),
                command.comment()
        );

        Question savedReply = qnaPersistencePort.save(reply);
        publishEventPort.publish(QuestionCreatedEvent.from(savedReply));

        return savedReply;
    }

    @Override
    public Question updateQuestion(UpdateQuestionCommand command) {
        Question question = qnaPersistencePort.findById(command.questionId())
                .orElseThrow(() -> BusinessException.builder(ErrorCode.QUESTION_NOT_FOUND).build());

        question.update(
                command.authorId(),
                command.title(),
                command.comment()
        );

        return qnaPersistencePort.save(question);
    }

    @Override
    public void deleteQuestion(DeleteQuestionCommand command) {
        Question question = qnaPersistencePort.findById(command.questionId())
                .orElseThrow(() -> BusinessException.builder(ErrorCode.QUESTION_NOT_FOUND).build());

        question.delete(command.authorId());
        if (question.getRootId() == null) {
            qnaPersistencePort.deleteByThreadId(question.getThreadId());
        } else {
            qnaPersistencePort.delete(question);
        }
    }

    private boolean canReply(Question rootQuestion, Long replierId) {
        boolean isRootAuthor = Objects.equals(rootQuestion.getAuthorId(), replierId);
        boolean isInstructor = verifyInstructorPort.isInstructor(replierId);
        return isRootAuthor || isInstructor;
    }

}

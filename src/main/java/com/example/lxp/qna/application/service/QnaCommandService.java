package com.example.lxp.qna.application.service;

import com.example.lxp.common.auth.model.Role;
import com.example.lxp.common.auth.model.User;
import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
import com.example.lxp.qna.application.port.in.dto.AddAnswerCommand;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import com.example.lxp.qna.application.port.out.CourseClientPort;
import com.example.lxp.qna.application.port.out.EnrollmentClientPort;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.event.QuestionCreatedEvent;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class QnaCommandService implements QuestionCommandUseCase {

    private final QnaPersistencePort qnaPersistencePort;
    private final EventPublisherPort publishEventPort;
    private final CourseClientPort courseClientPort;
    private final EnrollmentClientPort enrollmentClientPort;

    public QnaCommandService(
            QnaPersistencePort qnaPersistencePort,
            EventPublisherPort publishEventPort,
            CourseClientPort courseClientPort,
            EnrollmentClientPort enrollmentClientPort
    ) {
        this.qnaPersistencePort = qnaPersistencePort;
        this.publishEventPort = publishEventPort;
        this.courseClientPort = courseClientPort;
        this.enrollmentClientPort = enrollmentClientPort;
    }

    @Override
    public Question createQuestion(CreateQuestionCommand command) {
        Long instructorId = courseClientPort.getInstructorId(command.courseId());

        Question question = Question.createRoot(
                command.courseId(),
                command.lessonId(),
                command.user().getId(),
                instructorId,
                command.title(),
                command.comment()
        );

        Question savedQuestion = qnaPersistencePort.save(question);
        publishEventPort.publish(QuestionCreatedEvent.from(savedQuestion));
        return savedQuestion;
    }

    @Override
    public Question addAnswer(AddAnswerCommand command) {
        Question rootQuestion = qnaPersistencePort.findById(command.rootQuestionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        if (!Objects.equals(rootQuestion.getCourseId(), command.courseId())
                || !Objects.equals(rootQuestion.getLessonId(), command.lessonId())) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_CONTEXT_MISMATCH).build();
        }

        if (!rootQuestion.isRoot()) {
            throw BusinessException.builder(QnaErrorCode.CANNOT_REPLY_TO_REPLY).build();
        }

        validateAnswerPermission(rootQuestion, command.user());

        Question reply = Question.createReply(
                command.rootQuestionId(),
                rootQuestion.getThreadId(),
                command.courseId(),
                command.lessonId(),
                command.user().getId(),
                command.content()
        );

        boolean isInstructorReply = rootQuestion.isInstructor(command.user().getId());
        if (isInstructorReply) {
            rootQuestion.markAsAnswered();
        } else {
            rootQuestion.reopen();
        }

        qnaPersistencePort.save(rootQuestion);
        Question savedReply = qnaPersistencePort.save(reply);
        publishEventPort.publish(QuestionCreatedEvent.from(savedReply));
        return savedReply;
    }

    @Override
    public Question updateQuestion(UpdateQuestionCommand command) {
        Question question = qnaPersistencePort.findById(command.questionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        validateQuestionModification(question, command.user());

        question.update(command.title(), command.comment());

        return qnaPersistencePort.save(question);
    }

    @Override
    public void deleteQuestion(DeleteQuestionCommand command) {
        Question question = qnaPersistencePort.findById(command.questionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        validateQuestionModification(question, command.user());

        if (question.getRootId() == null) {
            qnaPersistencePort.deleteByThreadId(question.getThreadId());
        } else {
            qnaPersistencePort.delete(question);
        }
    }

    private void validateQuestionModification(Question question, User user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (!question.isAuthor(user.getId())) {
            throw BusinessException.builder(QnaErrorCode.FORBIDDEN_QUESTION_MODIFICATION).build();
        }
    }

    private void validateAnswerPermission(Question rootQuestion, User user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (rootQuestion.isInstructor(user.getId())) {
            return;
        }

        boolean isEnrolled = enrollmentClientPort.isEnrolled(rootQuestion.getCourseId(), user.getId());
        if (!isEnrolled) {
            throw BusinessException.builder(QnaErrorCode.FORBIDDEN_QUESTION_REPLY).build();
        }
    }

}

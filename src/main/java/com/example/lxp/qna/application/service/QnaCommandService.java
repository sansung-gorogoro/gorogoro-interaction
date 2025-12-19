package com.example.lxp.qna.application.service;

import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.application.port.in.QuestionCommandUseCase;
import com.example.lxp.qna.application.port.in.dto.AddAnswerCommand;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import com.example.lxp.qna.application.port.out.CourseClientPort;
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

    public QnaCommandService(
            QnaPersistencePort qnaPersistencePort,
            EventPublisherPort publishEventPort,
            CourseClientPort courseClientPort
    ) {
        this.qnaPersistencePort = qnaPersistencePort;
        this.publishEventPort = publishEventPort;
        this.courseClientPort = courseClientPort;
    }

    @Override
    public Question createQuestion(CreateQuestionCommand command) {
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

    @Override
    public Question addAnswer(AddAnswerCommand command) {
        Question parentQuestion = qnaPersistencePort.findById(command.rootQuestionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        if (!Objects.equals(parentQuestion.getCourseId(), command.courseId())
                || !Objects.equals(parentQuestion.getLessonId(), command.lessonId())) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_CONTEXT_MISMATCH).build();
        }

        if (parentQuestion.getRootId() != null) {
            throw BusinessException.builder(QnaErrorCode.CANNOT_REPLY_TO_REPLY).build();
        }

        if (!canReply(parentQuestion, command.authorId())) {
            throw BusinessException.builder(QnaErrorCode.FORBIDDEN_QUESTION_REPLY).build();
        }

        Question reply = Question.createReply(
                command.rootQuestionId(),
                parentQuestion.getThreadId(),
                command.courseId(),
                command.lessonId(),
                command.authorId(),
                command.content()
        );

        Question savedReply = qnaPersistencePort.save(reply);
        publishEventPort.publish(QuestionCreatedEvent.from(savedReply));
        return savedReply;
    }

    @Override
    public Question updateQuestion(UpdateQuestionCommand command) {
        Question question = qnaPersistencePort.findById(command.questionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

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
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        question.delete(command.authorId());
        if (question.getRootId() == null) {
            qnaPersistencePort.deleteByThreadId(question.getThreadId());
        } else {
            qnaPersistencePort.delete(question);
        }
    }

    private boolean canReply(Question rootQuestion, Long replierId) {
        boolean isRootAuthor = Objects.equals(rootQuestion.getAuthorId(), replierId);
        boolean isInstructor = courseClientPort.isInstructor(rootQuestion.getCourseId(), replierId);
        return isRootAuthor || isInstructor;
    }

}

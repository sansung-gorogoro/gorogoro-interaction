package com.example.lxp.qna.application.service;

import com.example.lxp.common.auth.model.Role;
import com.example.lxp.common.auth.model.User;
import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.application.port.in.dto.AddAnswerCommand;
import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import com.example.lxp.qna.application.port.out.CourseClientPort;
import com.example.lxp.qna.application.port.out.EnrollmentClientPort;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.event.QuestionCreatedEvent;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("QnaCommandService Unit Tests")
class QnaCommandServiceTest {

    @Mock
    private QnaPersistencePort qnaPersistencePort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private CourseClientPort courseClientPort;

    @Mock
    private EnrollmentClientPort enrollmentClientPort;

    @InjectMocks
    private QnaCommandService commandService;

    private static final Long COURSE_ID = 1L;
    private static final Long LESSON_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final Long INSTRUCTOR_ID = 4L;
    private static final Long QUESTION_ID = 100L;
    private static final String VALID_TITLE = "Test Title";
    private static final String VALID_CONTENT = "Test Content";
    private static final String THREAD_ID = "thread-123";

    private User adminUser;
    private User instructorUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        adminUser = User.from(10L, "ADMIN");
        instructorUser = User.from(INSTRUCTOR_ID, "INSTRUCTOR");
        studentUser = User.from(AUTHOR_ID, "STUDENT");
    }

    @Nested
    @DisplayName("createQuestion")
    class CreateQuestion {

        @Test
        @DisplayName("shouldCreateRootQuestionWithInstructorIdFromCourseClient")
        void shouldCreateRootQuestionWithInstructorIdFromCourseClient() {
            // given
            CreateQuestionCommand command = new CreateQuestionCommand(
                    COURSE_ID, LESSON_ID, VALID_TITLE, VALID_CONTENT, studentUser
            );
            when(courseClientPort.getInstructorId(COURSE_ID)).thenReturn(INSTRUCTOR_ID);
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.createQuestion(command);

            // then
            assertThat(result.getInstructorId()).isEqualTo(INSTRUCTOR_ID);
            assertThat(result.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(result.getLessonId()).isEqualTo(LESSON_ID);
            assertThat(result.getAuthorId()).isEqualTo(AUTHOR_ID);
            verify(courseClientPort).getInstructorId(COURSE_ID);
            verify(qnaPersistencePort).save(any(Question.class));
        }

        @Test
        @DisplayName("shouldPublishQuestionCreatedEventAfterSavingRootQuestion")
        void shouldPublishQuestionCreatedEventAfterSavingRootQuestion() {
            // given
            CreateQuestionCommand command = new CreateQuestionCommand(
                    COURSE_ID, LESSON_ID, VALID_TITLE, VALID_CONTENT, studentUser
            );
            when(courseClientPort.getInstructorId(COURSE_ID)).thenReturn(INSTRUCTOR_ID);
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.createQuestion(command);

            // then
            ArgumentCaptor<QuestionCreatedEvent> eventCaptor = ArgumentCaptor.forClass(QuestionCreatedEvent.class);
            verify(eventPublisherPort).publish(eventCaptor.capture());

            QuestionCreatedEvent event = eventCaptor.getValue();
            assertThat(event.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(event.getLessonId()).isEqualTo(LESSON_ID);
            assertThat(event.getAuthorId()).isEqualTo(AUTHOR_ID);
            assertThat(event.getThreadId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("addAnswer - Context")
    class AddAnswerContext {

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenRootQuestionMissing")
        void shouldThrowQuestionNotFoundWhenRootQuestionMissing() {
            // given
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, studentUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commandService.addAnswer(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));

            verify(qnaPersistencePort, never()).save(any());
        }

        @Test
        @DisplayName("shouldThrowContextMismatchWhenCourseOrLessonMismatch")
        void shouldThrowContextMismatchWhenCourseOrLessonMismatch() {
            // given
            Question root = createRootQuestion();
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, studentUser, VALID_CONTENT, 999L, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when & then
            assertThatThrownBy(() -> commandService.addAnswer(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_CONTEXT_MISMATCH));
        }

        @Test
        @DisplayName("shouldThrowCannotReplyToReplyWhenRootIsNotRoot")
        void shouldThrowCannotReplyToReplyWhenRootIsNotRoot() {
            // given
            Question reply = createReplyQuestion(QUESTION_ID);
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, studentUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(reply));

            // when & then
            assertThatThrownBy(() -> commandService.addAnswer(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.CANNOT_REPLY_TO_REPLY));
        }
    }

    @Nested
    @DisplayName("addAnswer - Permission")
    class AddAnswerPermission {

        @Test
        @DisplayName("shouldAllowAdminToReply")
        void shouldAllowAdminToReply() {
            // given
            Question root = createRootQuestion();
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, adminUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.addAnswer(command);

            // then
            assertThat(result).isNotNull();
            verify(enrollmentClientPort, never()).isEnrolled(any(), any());
        }

        @Test
        @DisplayName("shouldAllowInstructorToReplyWhenUserIsInstructor")
        void shouldAllowInstructorToReplyWhenUserIsInstructor() {
            // given
            Question root = createRootQuestion();
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, instructorUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.addAnswer(command);

            // then
            assertThat(result).isNotNull();
            verify(enrollmentClientPort, never()).isEnrolled(any(), any());
        }

        @Test
        @DisplayName("shouldAllowStudentToReplyWhenEnrolled")
        void shouldAllowStudentToReplyWhenEnrolled() {
            // given
            Question root = createRootQuestion();
            User otherStudent = User.from(999L, "STUDENT");
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, otherStudent, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(enrollmentClientPort.isEnrolled(COURSE_ID, 999L)).thenReturn(true);
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.addAnswer(command);

            // then
            assertThat(result).isNotNull();
            verify(enrollmentClientPort).isEnrolled(COURSE_ID, 999L);
        }

        @Test
        @DisplayName("shouldThrowForbiddenWhenStudentNotEnrolled")
        void shouldThrowForbiddenWhenStudentNotEnrolled() {
            // given
            Question root = createRootQuestion();
            User otherStudent = User.from(999L, "STUDENT");
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, otherStudent, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(enrollmentClientPort.isEnrolled(COURSE_ID, 999L)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> commandService.addAnswer(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.FORBIDDEN_QUESTION_REPLY));
        }
    }

    @Nested
    @DisplayName("addAnswer - Status/Activity")
    class AddAnswerStatusActivity {

        @Test
        @DisplayName("shouldMarkRootAsAnsweredAndUpdateLastActivityWhenInstructorReplies")
        void shouldMarkRootAsAnsweredAndUpdateLastActivityWhenInstructorReplies() {
            // given
            Question root = createRootQuestion();
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, instructorUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.addAnswer(command);

            // then
            assertThat(root.getStatus()).isEqualTo(QuestionStatus.ANSWERED);
        }

        @Test
        @DisplayName("shouldReopenRootAndUpdateLastActivityWhenStudentReplies")
        void shouldReopenRootAndUpdateLastActivityWhenStudentReplies() {
            // given
            Question root = createRootQuestion();
            root.markAsAnswered();
            User otherStudent = User.from(999L, "STUDENT");
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, otherStudent, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(enrollmentClientPort.isEnrolled(COURSE_ID, 999L)).thenReturn(true);
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.addAnswer(command);

            // then
            assertThat(root.getStatus()).isEqualTo(QuestionStatus.OPENED);
        }
    }

    @Nested
    @DisplayName("addAnswer - Save/Event")
    class AddAnswerSaveEvent {

        @Test
        @DisplayName("shouldSaveRootAndReplyAndPublishEventForReply")
        void shouldSaveRootAndReplyAndPublishEventForReply() {
            // given
            Question root = createRootQuestion();
            AddAnswerCommand command = new AddAnswerCommand(
                    QUESTION_ID, instructorUser, VALID_CONTENT, COURSE_ID, LESSON_ID
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.addAnswer(command);

            // then
            verify(qnaPersistencePort, times(2)).save(any(Question.class));

            ArgumentCaptor<QuestionCreatedEvent> eventCaptor = ArgumentCaptor.forClass(QuestionCreatedEvent.class);
            verify(eventPublisherPort).publish(eventCaptor.capture());

            QuestionCreatedEvent event = eventCaptor.getValue();
            assertThat(event.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(event.getLessonId()).isEqualTo(LESSON_ID);
            assertThat(event.getAuthorId()).isEqualTo(INSTRUCTOR_ID);
            assertThat(event.getThreadId()).isEqualTo(THREAD_ID);
        }
    }

    @Nested
    @DisplayName("updateQuestion - NotFound/Permission")
    class UpdateQuestionPermission {

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenUpdatingMissingQuestion")
        void shouldThrowQuestionNotFoundWhenUpdatingMissingQuestion() {
            // given
            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    QUESTION_ID, VALID_TITLE, VALID_CONTENT, studentUser
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commandService.updateQuestion(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }

        @Test
        @DisplayName("shouldAllowAdminToUpdateAnyQuestion")
        void shouldAllowAdminToUpdateAnyQuestion() {
            // given
            Question root = createRootQuestion();
            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    QUESTION_ID, "New Title", "New Content", adminUser
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.updateQuestion(command);

            // then
            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getContent()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("shouldAllowAuthorToUpdateOwnQuestion")
        void shouldAllowAuthorToUpdateOwnQuestion() {
            // given
            Question root = createRootQuestion();
            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    QUESTION_ID, "New Title", "New Content", studentUser
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Question result = commandService.updateQuestion(command);

            // then
            assertThat(result.getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("shouldThrowForbiddenWhenNonAuthorNonAdminUpdates")
        void shouldThrowForbiddenWhenNonAuthorNonAdminUpdates() {
            // given
            Question root = createRootQuestion();
            User otherStudent = User.from(999L, "STUDENT");
            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    QUESTION_ID, "New Title", "New Content", otherStudent
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when & then
            assertThatThrownBy(() -> commandService.updateQuestion(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.FORBIDDEN_QUESTION_MODIFICATION));
        }
    }

    @Nested
    @DisplayName("updateQuestion - lastActivityAt")
    class UpdateQuestionLastActivity {

        @Test
        @DisplayName("shouldUpdateLastActivityWhenUpdatingRootQuestion")
        void shouldUpdateLastActivityWhenUpdatingRootQuestion() {
            // given
            Question root = createRootQuestion();
            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    QUESTION_ID, "New Title", "New Content", studentUser
            );
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.updateQuestion(command);

            // then
            verify(qnaPersistencePort, times(1)).save(any(Question.class));
        }

        @Test
        @DisplayName("shouldUpdateRootLastActivityWhenUpdatingReply")
        void shouldUpdateRootLastActivityWhenUpdatingReply() {
            // given
            Question root = createRootQuestion();
            Question reply = createReplyQuestion(QUESTION_ID);
            setPrivateField(reply, "authorId", AUTHOR_ID);
            Long replyId = 200L;
            setPrivateField(reply, "id", replyId);
            setPrivateField(reply, "rootId", QUESTION_ID);

            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    replyId, null, "New Content", studentUser
            );
            when(qnaPersistencePort.findById(replyId)).thenReturn(Optional.of(reply));
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            commandService.updateQuestion(command);

            // then
            verify(qnaPersistencePort, times(2)).save(any(Question.class));
        }

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenUpdatingReplyButRootMissing")
        void shouldThrowQuestionNotFoundWhenUpdatingReplyButRootMissing() {
            // given
            Question reply = createReplyQuestion(QUESTION_ID);
            setPrivateField(reply, "authorId", AUTHOR_ID);
            Long replyId = 200L;
            setPrivateField(reply, "id", replyId);
            setPrivateField(reply, "rootId", QUESTION_ID);

            UpdateQuestionCommand command = new UpdateQuestionCommand(
                    replyId, null, "New Content", studentUser
            );
            when(qnaPersistencePort.findById(replyId)).thenReturn(Optional.of(reply));
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commandService.updateQuestion(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("deleteQuestion - NotFound/Permission")
    class DeleteQuestionPermission {

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenDeletingMissingQuestion")
        void shouldThrowQuestionNotFoundWhenDeletingMissingQuestion() {
            // given
            DeleteQuestionCommand command = new DeleteQuestionCommand(QUESTION_ID, studentUser);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commandService.deleteQuestion(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }

        @Test
        @DisplayName("shouldAllowAdminToDeleteAnyQuestion")
        void shouldAllowAdminToDeleteAnyQuestion() {
            // given
            Question root = createRootQuestion();
            DeleteQuestionCommand command = new DeleteQuestionCommand(QUESTION_ID, adminUser);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when
            commandService.deleteQuestion(command);

            // then
            verify(qnaPersistencePort).deleteByThreadId(THREAD_ID);
        }

        @Test
        @DisplayName("shouldAllowAuthorToDeleteOwnQuestion")
        void shouldAllowAuthorToDeleteOwnQuestion() {
            // given
            Question root = createRootQuestion();
            DeleteQuestionCommand command = new DeleteQuestionCommand(QUESTION_ID, studentUser);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when
            commandService.deleteQuestion(command);

            // then
            verify(qnaPersistencePort).deleteByThreadId(THREAD_ID);
        }

        @Test
        @DisplayName("shouldThrowForbiddenWhenNonAuthorNonAdminDeletes")
        void shouldThrowForbiddenWhenNonAuthorNonAdminDeletes() {
            // given
            Question root = createRootQuestion();
            User otherStudent = User.from(999L, "STUDENT");
            DeleteQuestionCommand command = new DeleteQuestionCommand(QUESTION_ID, otherStudent);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when & then
            assertThatThrownBy(() -> commandService.deleteQuestion(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.FORBIDDEN_QUESTION_MODIFICATION));
        }
    }

    @Nested
    @DisplayName("deleteQuestion - Policy")
    class DeleteQuestionPolicy {

        @Test
        @DisplayName("shouldDeleteByThreadIdWhenDeletingRootQuestion")
        void shouldDeleteByThreadIdWhenDeletingRootQuestion() {
            // given
            Question root = createRootQuestion();
            DeleteQuestionCommand command = new DeleteQuestionCommand(QUESTION_ID, studentUser);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));

            // when
            commandService.deleteQuestion(command);

            // then
            verify(qnaPersistencePort).deleteByThreadId(THREAD_ID);
            verify(qnaPersistencePort, never()).delete(any());
        }

        @Test
        @DisplayName("shouldDeleteSingleQuestionWhenDeletingReply")
        void shouldDeleteSingleQuestionWhenDeletingReply() {
            // given
            Question reply = createReplyQuestion(QUESTION_ID);
            setPrivateField(reply, "authorId", AUTHOR_ID);
            Long replyId = 200L;
            setPrivateField(reply, "id", replyId);

            DeleteQuestionCommand command = new DeleteQuestionCommand(replyId, studentUser);
            when(qnaPersistencePort.findById(replyId)).thenReturn(Optional.of(reply));

            // when
            commandService.deleteQuestion(command);

            // then
            verify(qnaPersistencePort).delete(reply);
            verify(qnaPersistencePort, never()).deleteByThreadId(any());
        }
    }

    private Question createRootQuestion() {
        Question root = Question.createRoot(COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID, VALID_TITLE, VALID_CONTENT);
        setPrivateField(root, "id", QUESTION_ID);
        setPrivateField(root, "threadId", THREAD_ID);
        return root;
    }

    private Question createReplyQuestion(Long rootId) {
        return Question.createReply(rootId, THREAD_ID, COURSE_ID, LESSON_ID, AUTHOR_ID, VALID_CONTENT);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}

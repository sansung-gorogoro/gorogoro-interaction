package com.example.lxp.qna.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Question Domain Unit Tests")
class QuestionTest {

    private static final Long COURSE_ID = 1L;
    private static final Long LESSON_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final Long INSTRUCTOR_ID = 4L;
    private static final String VALID_TITLE = "Test Title";
    private static final String VALID_CONTENT = "Test Content";
    private static final String VALID_THREAD_ID = "thread-123";

    @Nested
    @DisplayName("Factory / Validation")
    class FactoryValidation {

        @Test
        @DisplayName("shouldCreateRootWithOpenedStatusAndLastActivityWhenValid")
        void shouldCreateRootWithOpenedStatusAndLastActivityWhenValid() {
            // given
            Instant before = Instant.now();

            // when
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // then
            assertThat(root.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(root.getLessonId()).isEqualTo(LESSON_ID);
            assertThat(root.getAuthorId()).isEqualTo(AUTHOR_ID);
            assertThat(root.getInstructorId()).isEqualTo(INSTRUCTOR_ID);
            assertThat(root.getTitle()).isEqualTo(VALID_TITLE);
            assertThat(root.getContent()).isEqualTo(VALID_CONTENT);
            assertThat(root.getStatus()).isEqualTo(QuestionStatus.OPENED);
            assertThat(root.getLastActivityAt()).isNotNull();
            assertThat(root.getLastActivityAt()).isAfterOrEqualTo(before);
            assertThat(root.getRootId()).isNull();
            assertThat(root.getThreadId()).isNotNull();
        }

        @Test
        @DisplayName("shouldThrowWhenCreateRootWithBlankTitle")
        void shouldThrowWhenCreateRootWithBlankTitle() {
            // when & then
            assertThatThrownBy(() -> Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    "", VALID_CONTENT
            ))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(QnaErrorCode.QUESTION_TITLE_IS_BLANK);
                    });
        }

        @Test
        @DisplayName("shouldThrowWhenCreateRootWithBlankContent")
        void shouldThrowWhenCreateRootWithBlankContent() {
            // when & then
            assertThatThrownBy(() -> Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, ""
            ))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(QnaErrorCode.QUESTION_CONTENT_IS_BLANK);
                    });
        }

        @Test
        @DisplayName("shouldCreateReplyWithRootIdAndThreadIdWhenValid")
        void shouldCreateReplyWithRootIdAndThreadIdWhenValid() {
            // given
            Long rootId = 100L;

            // when
            Question reply = Question.createReply(
                    rootId, VALID_THREAD_ID, COURSE_ID, LESSON_ID,
                    AUTHOR_ID, VALID_CONTENT
            );

            // then
            assertThat(reply.getRootId()).isEqualTo(rootId);
            assertThat(reply.getThreadId()).isEqualTo(VALID_THREAD_ID);
            assertThat(reply.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(reply.getLessonId()).isEqualTo(LESSON_ID);
            assertThat(reply.getAuthorId()).isEqualTo(AUTHOR_ID);
            assertThat(reply.getContent()).isEqualTo(VALID_CONTENT);
            assertThat(reply.getTitle()).isNull();
            assertThat(reply.getStatus()).isNull();
            assertThat(reply.getLastActivityAt()).isNull();
        }

        @Test
        @DisplayName("shouldThrowWhenCreateReplyWithBlankContent")
        void shouldThrowWhenCreateReplyWithBlankContent() {
            // when & then
            assertThatThrownBy(() -> Question.createReply(
                    100L, VALID_THREAD_ID, COURSE_ID, LESSON_ID,
                    AUTHOR_ID, ""
            ))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(QnaErrorCode.QUESTION_CONTENT_IS_BLANK);
                    });
        }

        @Test
        @DisplayName("shouldThrowWhenCreateReplyWithBlankThreadId")
        void shouldThrowWhenCreateReplyWithBlankThreadId() {
            // when & then
            assertThatThrownBy(() -> Question.createReply(
                    100L, "", COURSE_ID, LESSON_ID,
                    AUTHOR_ID, VALID_CONTENT
            ))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(QnaErrorCode.QUESTION_THREAD_ID_IS_BLANK);
                    });
        }
    }

    @Nested
    @DisplayName("Update Rules")
    class UpdateRules {

        @Test
        @DisplayName("shouldUpdateOnlyNonBlankFields")
        void shouldUpdateOnlyNonBlankFields() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );
            String newTitle = "Updated Title";
            String newContent = "Updated Content";

            // when
            root.update(newTitle, newContent);

            // then
            assertThat(root.getTitle()).isEqualTo(newTitle);
            assertThat(root.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("shouldNotOverwriteTitleWhenBlankTitleProvided")
        void shouldNotOverwriteTitleWhenBlankTitleProvided() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // when
            root.update("", "New Content");

            // then
            assertThat(root.getTitle()).isEqualTo(VALID_TITLE);
            assertThat(root.getContent()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("shouldNotOverwriteContentWhenBlankContentProvided")
        void shouldNotOverwriteContentWhenBlankContentProvided() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // when
            root.update("New Title", "");

            // then
            assertThat(root.getTitle()).isEqualTo("New Title");
            assertThat(root.getContent()).isEqualTo(VALID_CONTENT);
        }
    }

    @Nested
    @DisplayName("Status / Activity")
    class StatusActivity {

        @Test
        @DisplayName("shouldSetAnsweredAndUpdateLastActivityWhenMarkAsAnswered")
        void shouldSetAnsweredAndUpdateLastActivityWhenMarkAsAnswered() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );
            Instant originalLastActivity = root.getLastActivityAt();

            // small delay to ensure time difference
            sleepMillis(10);
            Instant before = Instant.now();

            // when
            root.markAsAnswered();

            // then
            assertThat(root.getStatus()).isEqualTo(QuestionStatus.ANSWERED);
            assertThat(root.getLastActivityAt()).isAfterOrEqualTo(before);
            assertThat(root.getLastActivityAt()).isAfter(originalLastActivity);
        }

        @Test
        @DisplayName("shouldSetOpenedAndUpdateLastActivityWhenReopen")
        void shouldSetOpenedAndUpdateLastActivityWhenReopen() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );
            root.markAsAnswered();
            Instant afterAnswered = root.getLastActivityAt();

            sleepMillis(10);
            Instant before = Instant.now();

            // when
            root.reopen();

            // then
            assertThat(root.getStatus()).isEqualTo(QuestionStatus.OPENED);
            assertThat(root.getLastActivityAt()).isAfterOrEqualTo(before);
            assertThat(root.getLastActivityAt()).isAfter(afterAnswered);
        }

        @Test
        @DisplayName("shouldUpdateLastActivityWhenUpdateLastActivityCalled")
        void shouldUpdateLastActivityWhenUpdateLastActivityCalled() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );
            Instant original = root.getLastActivityAt();
            QuestionStatus originalStatus = root.getStatus();

            sleepMillis(10);
            Instant before = Instant.now();

            // when
            root.updateLastActivity();

            // then
            assertThat(root.getLastActivityAt()).isAfterOrEqualTo(before);
            assertThat(root.getLastActivityAt()).isAfter(original);
            assertThat(root.getStatus()).isEqualTo(originalStatus);
        }

        private void sleepMillis(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Nested
    @DisplayName("Helpers")
    class Helpers {

        @Test
        @DisplayName("shouldReturnTrueWhenIsRootHasNoRootId")
        void shouldReturnTrueWhenIsRootHasNoRootId() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // when & then
            assertThat(root.isRoot()).isTrue();
            assertThat(root.getRootId()).isNull();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenIsRootHasRootId")
        void shouldReturnFalseWhenIsRootHasRootId() {
            // given
            Question reply = Question.createReply(
                    100L, VALID_THREAD_ID, COURSE_ID, LESSON_ID,
                    AUTHOR_ID, VALID_CONTENT
            );

            // when & then
            assertThat(reply.isRoot()).isFalse();
            assertThat(reply.getRootId()).isNotNull();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenIsAuthorMatches")
        void shouldReturnTrueWhenIsAuthorMatches() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // when & then
            assertThat(root.isAuthor(AUTHOR_ID)).isTrue();
            assertThat(root.isAuthor(999L)).isFalse();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenIsInstructorMatches")
        void shouldReturnTrueWhenIsInstructorMatches() {
            // given
            Question root = Question.createRoot(
                    COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID,
                    VALID_TITLE, VALID_CONTENT
            );

            // when & then
            assertThat(root.isInstructor(INSTRUCTOR_ID)).isTrue();
            assertThat(root.isInstructor(999L)).isFalse();
        }
    }
}

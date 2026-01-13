package com.example.lxp.qna.application.service;

import com.example.lxp.common.auth.model.User;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsAllResponse;
import com.example.lxp.qna.adapter.in.web.dto.QuestionThreadResponse;
import com.example.lxp.qna.adapter.in.web.dto.UnansweredQuestionsResponse;
import com.example.lxp.qna.application.port.in.dto.GetQnaThreadQuery;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("QnaQueryService Unit Tests")
class QnaQueryServiceTest {

    @Mock
    private QnaPersistencePort qnaPersistencePort;

    @InjectMocks
    private QnaQueryService queryService;

    private static final Long COURSE_ID = 1L;
    private static final Long LESSON_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final Long INSTRUCTOR_ID = 4L;
    private static final Long QUESTION_ID = 100L;
    private static final String VALID_TITLE = "Test Title";
    private static final String VALID_CONTENT = "Test Content";
    private static final String THREAD_ID = "thread-123";

    private User instructorUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        instructorUser = User.from(INSTRUCTOR_ID, "INSTRUCTOR");
        studentUser = User.from(AUTHOR_ID, "STUDENT");
    }

    @Nested
    @DisplayName("findLessonRootQuestionsAll")
    class FindLessonRootQuestionsAll {

        @Test
        @DisplayName("shouldReturnLessonQuestionsAllResponseWithReplyCounts")
        void shouldReturnLessonQuestionsAllResponseWithReplyCounts() {
            // given
            Question root1 = createRootQuestion(1L);
            Question root2 = createRootQuestion(2L);
            List<Question> roots = List.of(root1, root2);
            Map<Long, Long> replyCounts = Map.of(1L, 5L, 2L, 3L);

            when(qnaPersistencePort.findRootQuestionsAllByLesson(eq(COURSE_ID), eq(LESSON_ID), any(Sort.class)))
                    .thenReturn(roots);
            when(qnaPersistencePort.countRepliesByRootIds(List.of(1L, 2L))).thenReturn(replyCounts);

            // when
            LessonQuestionsAllResponse response = queryService.findLessonRootQuestionsAll(COURSE_ID, LESSON_ID);

            // then
            assertThat(response.getQuestions()).hasSize(2);
            assertThat(response.getQuestions().get(0).getReplyCount()).isEqualTo(5L);
            assertThat(response.getQuestions().get(1).getReplyCount()).isEqualTo(3L);
        }

        @Test
        @DisplayName("shouldDefaultMissingReplyCountToZero")
        void shouldDefaultMissingReplyCountToZero() {
            // given
            Question root1 = createRootQuestion(1L);
            Question root2 = createRootQuestion(2L);
            List<Question> roots = List.of(root1, root2);
            Map<Long, Long> replyCounts = Map.of(1L, 5L);

            when(qnaPersistencePort.findRootQuestionsAllByLesson(eq(COURSE_ID), eq(LESSON_ID), any(Sort.class)))
                    .thenReturn(roots);
            when(qnaPersistencePort.countRepliesByRootIds(List.of(1L, 2L))).thenReturn(replyCounts);

            // when
            LessonQuestionsAllResponse response = queryService.findLessonRootQuestionsAll(COURSE_ID, LESSON_ID);

            // then
            assertThat(response.getQuestions()).hasSize(2);
            assertThat(response.getQuestions().get(0).getReplyCount()).isEqualTo(5L);
            assertThat(response.getQuestions().get(1).getReplyCount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("getThread - Success")
    class GetThreadSuccess {

        @Test
        @DisplayName("shouldReturnThreadResponseWhenQuestionIdIsRoot")
        void shouldReturnThreadResponseWhenQuestionIdIsRoot() {
            // given
            Question root = createRootQuestion(QUESTION_ID);
            Question reply = createReplyQuestion(200L, QUESTION_ID);
            List<Question> thread = List.of(root, reply);

            GetQnaThreadQuery query = new GetQnaThreadQuery(COURSE_ID, LESSON_ID, QUESTION_ID);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.findByThreadIdOrderByCreatedAtAscIdAsc(THREAD_ID)).thenReturn(thread);

            // when
            QuestionThreadResponse response = queryService.getThread(query);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getThreadId()).isEqualTo(THREAD_ID);
            assertThat(response.getQuestions()).hasSize(2);
        }

        @Test
        @DisplayName("shouldReturnThreadResponseWhenQuestionIdIsReply")
        void shouldReturnThreadResponseWhenQuestionIdIsReply() {
            // given
            Question root = createRootQuestion(QUESTION_ID);
            Question reply = createReplyQuestion(200L, QUESTION_ID);
            List<Question> thread = List.of(root, reply);

            GetQnaThreadQuery query = new GetQnaThreadQuery(COURSE_ID, LESSON_ID, 200L);
            when(qnaPersistencePort.findById(200L)).thenReturn(Optional.of(reply));
            when(qnaPersistencePort.findByThreadIdOrderByCreatedAtAscIdAsc(THREAD_ID)).thenReturn(thread);

            // when
            QuestionThreadResponse response = queryService.getThread(query);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getThreadId()).isEqualTo(THREAD_ID);
        }
    }

    @Nested
    @DisplayName("getThread - Exception")
    class GetThreadException {

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenQuestionIdDoesNotExist")
        void shouldThrowQuestionNotFoundWhenQuestionIdDoesNotExist() {
            // given
            GetQnaThreadQuery query = new GetQnaThreadQuery(COURSE_ID, LESSON_ID, 999L);
            when(qnaPersistencePort.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> queryService.getThread(query))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenThreadHasNoRoot")
        void shouldThrowQuestionNotFoundWhenThreadHasNoRoot() {
            // given
            Question reply = createReplyQuestion(200L, QUESTION_ID);
            List<Question> threadWithNoRoot = List.of(reply);

            GetQnaThreadQuery query = new GetQnaThreadQuery(COURSE_ID, LESSON_ID, 200L);
            when(qnaPersistencePort.findById(200L)).thenReturn(Optional.of(reply));
            when(qnaPersistencePort.findByThreadIdOrderByCreatedAtAscIdAsc(THREAD_ID)).thenReturn(threadWithNoRoot);

            // when & then
            assertThatThrownBy(() -> queryService.getThread(query))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }

        @Test
        @DisplayName("shouldThrowQuestionNotFoundWhenCourseLessonMismatch")
        void shouldThrowQuestionNotFoundWhenCourseLessonMismatch() {
            // given
            Question root = createRootQuestion(QUESTION_ID);
            List<Question> thread = List.of(root);

            GetQnaThreadQuery query = new GetQnaThreadQuery(999L, LESSON_ID, QUESTION_ID);
            when(qnaPersistencePort.findById(QUESTION_ID)).thenReturn(Optional.of(root));
            when(qnaPersistencePort.findByThreadIdOrderByCreatedAtAscIdAsc(THREAD_ID)).thenReturn(thread);

            // when & then
            assertThatThrownBy(() -> queryService.getThread(query))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.QUESTION_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("findUnansweredQuestionsForInstructor")
    class FindUnansweredQuestionsForInstructor {

        @Test
        @DisplayName("shouldThrowForbiddenInstructorOnlyWhenUserIsNotInstructor")
        void shouldThrowForbiddenInstructorOnlyWhenUserIsNotInstructor() {
            // given
            GetUnansweredQuestionsQuery query = new GetUnansweredQuestionsQuery(studentUser, 5);

            // when & then
            assertThatThrownBy(() -> queryService.findUnansweredQuestionsForInstructor(query))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(QnaErrorCode.FORBIDDEN_INSTRUCTOR_ONLY));
        }

        @Test
        @DisplayName("shouldUseDefaultLimitWhenNull")
        void shouldUseDefaultLimitWhenNull() {
            // given
            GetUnansweredQuestionsQuery query = new GetUnansweredQuestionsQuery(instructorUser, null);
            when(qnaPersistencePort.findOpenedRootQuestionsByInstructorId(anyLong(), eq(3)))
                    .thenReturn(Collections.emptyList());

            // when
            queryService.findUnansweredQuestionsForInstructor(query);

            // then
            verify(qnaPersistencePort).findOpenedRootQuestionsByInstructorId(INSTRUCTOR_ID, 3);
        }

        @Test
        @DisplayName("shouldCapLimitToTenWhenGreaterThanTen")
        void shouldCapLimitToTenWhenGreaterThanTen() {
            // given
            GetUnansweredQuestionsQuery query = new GetUnansweredQuestionsQuery(instructorUser, 100);
            when(qnaPersistencePort.findOpenedRootQuestionsByInstructorId(anyLong(), eq(10)))
                    .thenReturn(Collections.emptyList());

            // when
            queryService.findUnansweredQuestionsForInstructor(query);

            // then
            verify(qnaPersistencePort).findOpenedRootQuestionsByInstructorId(INSTRUCTOR_ID, 10);
        }

        @Test
        @DisplayName("shouldReturnUnansweredQuestionsResponseMappedFromQuestions")
        void shouldReturnUnansweredQuestionsResponseMappedFromQuestions() {
            // given
            Question q1 = createRootQuestion(1L);
            Question q2 = createRootQuestion(2L);
            List<Question> questions = List.of(q1, q2);

            GetUnansweredQuestionsQuery query = new GetUnansweredQuestionsQuery(instructorUser, 5);
            when(qnaPersistencePort.findOpenedRootQuestionsByInstructorId(INSTRUCTOR_ID, 5))
                    .thenReturn(questions);

            // when
            UnansweredQuestionsResponse response = queryService.findUnansweredQuestionsForInstructor(query);

            // then
            assertThat(response.getQuestions()).hasSize(2);
        }
    }

    private Question createRootQuestion(Long id) {
        Question root = Question.createRoot(COURSE_ID, LESSON_ID, AUTHOR_ID, INSTRUCTOR_ID, VALID_TITLE, VALID_CONTENT);
        setPrivateField(root, "id", id);
        setPrivateField(root, "threadId", THREAD_ID);
        return root;
    }

    private Question createReplyQuestion(Long id, Long rootId) {
        Question reply = Question.createReply(rootId, THREAD_ID, COURSE_ID, LESSON_ID, AUTHOR_ID, VALID_CONTENT);
        setPrivateField(reply, "id", id);
        return reply;
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

package com.example.lxp.qna.application.service;

import com.example.lxp.common.auth.model.Role;
import com.example.lxp.common.auth.model.User;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionItem;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsAllResponse;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsResponse;
import com.example.lxp.qna.adapter.in.web.dto.QuestionThreadItemResponse;
import com.example.lxp.qna.adapter.in.web.dto.QuestionThreadResponse;
import com.example.lxp.qna.adapter.in.web.dto.UnansweredQuestionsResponse;
import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.application.port.in.dto.GetLessonQuestionsQuery;
import com.example.lxp.qna.application.port.in.dto.GetQnaThreadQuery;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;
import com.example.lxp.qna.application.port.out.QnaUserClientPort;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QnaQueryService implements QuestionQueryUseCase {

    private static final int DEFAULT_UNANSWERED_QUESTIONS_QUERY_SIZE = 3;
    private static final String UNKNOWN_NICKNAME = "알 수 없음";

    private final QnaPersistencePort qnaPersistencePort;
    private final QnaUserClientPort userClientPort;

    public QnaQueryService(
            QnaPersistencePort qnaPersistencePort,
            QnaUserClientPort userClientPort
    ) {
        this.qnaPersistencePort = qnaPersistencePort;
        this.userClientPort = userClientPort;
    }

    @Override
    public UnansweredQuestionsResponse findUnansweredQuestionsForInstructor(GetUnansweredQuestionsQuery query) {
        validateInstructorRole(query.user());

        List<Question> questions = qnaPersistencePort.findOpenedRootQuestionsByInstructorId(
                query.user().getId(),
                query.limit() == null ? DEFAULT_UNANSWERED_QUESTIONS_QUERY_SIZE : Math.min(query.limit(), 10)
        );

        Map<Long, String> nicknameMap = loadNicknames(questions);
        return UnansweredQuestionsResponse.from(questions, nicknameMap, UNKNOWN_NICKNAME);
    }

    @Override
    public LessonQuestionsResponse findLessonRootQuestions(GetLessonQuestionsQuery query) {
        Sort sort = Sort.by("lastActivityAt").descending()
                .and(Sort.by("id").descending());
        PageRequest pageRequest = PageRequest.of(query.page(), query.size(), sort);

        Page<Question> questionPage = qnaPersistencePort.findRootQuestionsByLesson(
                query.courseId(),
                query.lessonId(),
                pageRequest
        );

        List<Question> roots = questionPage.getContent();
        List<Long> rootIds = roots.stream()
                .map(Question::getId)
                .toList();

        Map<Long, Long> replyCounts = qnaPersistencePort.countRepliesByRootIds(rootIds);

        Map<Long, String> nicknameMap = loadNicknames(roots);
        return LessonQuestionsResponse.from(questionPage, replyCounts, nicknameMap, UNKNOWN_NICKNAME);
    }

    @Override
    public LessonQuestionsAllResponse findLessonRootQuestionsAll(Long courseId, Long lessonId) {
        Sort sort = Sort.by("lastActivityAt").descending()
                .and(Sort.by("id").descending());

        List<Question> roots = qnaPersistencePort.findRootQuestionsAllByLesson(courseId, lessonId, sort);

        List<Long> rootIds = roots.stream()
                .map(Question::getId)
                .toList();

        Map<Long, Long> replyCounts = qnaPersistencePort.countRepliesByRootIds(rootIds);

        Map<Long, String> nicknameMap = loadNicknames(roots);
        List<LessonQuestionItem> items = roots.stream()
                .map(q -> LessonQuestionItem.from(
                        q,
                        replyCounts.getOrDefault(q.getId(), 0L),
                        nicknameMap.getOrDefault(q.getAuthorId(), UNKNOWN_NICKNAME)
                ))
                .toList();

        return LessonQuestionsAllResponse.of(items);
    }

    @Override
    public QuestionThreadResponse getThread(GetQnaThreadQuery query) {
        Question question = qnaPersistencePort.findById(query.questionId())
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        List<Question> questions = qnaPersistencePort.findByThreadIdOrderByCreatedAtAscIdAsc(question.getThreadId());

        Question root = questions.stream()
                .filter(Question::isRoot)
                .findFirst()
                .orElseThrow(() -> BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build());

        if (!root.getCourseId().equals(query.courseId()) || !root.getLessonId().equals(query.lessonId())) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_NOT_FOUND).build();
        }

        Map<Long, String> nicknameMap = loadNicknames(questions);
        List<QuestionThreadItemResponse> items = questions.stream()
                .map(q -> QuestionThreadItemResponse.from(
                        q,
                        nicknameMap.getOrDefault(q.getAuthorId(), UNKNOWN_NICKNAME)
                ))
                .toList();

        return QuestionThreadResponse.of(root, items);
    }

    private void validateInstructorRole(User user) {
        if (user.getRole() != Role.INSTRUCTOR) {
            throw BusinessException.builder(QnaErrorCode.FORBIDDEN_INSTRUCTOR_ONLY).build();
        }
    }

    private Map<Long, String> loadNicknames(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> authorIds = questions.stream()
                .map(Question::getAuthorId)
                .collect(Collectors.toSet());

        if (authorIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return userClientPort.getNicknames(authorIds);
    }

}

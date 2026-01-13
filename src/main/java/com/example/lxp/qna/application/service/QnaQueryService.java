package com.example.lxp.qna.application.service;

import com.example.lxp.common.auth.model.Role;
import com.example.lxp.common.auth.model.User;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionItem;
import com.example.lxp.qna.adapter.in.web.dto.LessonQuestionsResponse;
import com.example.lxp.qna.application.port.in.QuestionQueryUseCase;
import com.example.lxp.qna.application.port.in.dto.GetLessonQuestionsQuery;
import com.example.lxp.qna.application.port.in.dto.GetUnansweredQuestionsQuery;
import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.exception.QnaErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class QnaQueryService implements QuestionQueryUseCase {

    private static final int DEFAULT_UNANSWERED_QUESTIONS_QUERY_SIZE = 3;

    private final QnaPersistencePort qnaPersistencePort;

    public QnaQueryService(QnaPersistencePort qnaPersistencePort) {
        this.qnaPersistencePort = qnaPersistencePort;
    }

    @Override
    public List<Question> findUnansweredQuestionsForInstructor(GetUnansweredQuestionsQuery query) {

        validateInstructorRole(query.user());

        return qnaPersistencePort.findOpenedRootQuestionsByInstructorId(
                query.user().getId(),
                query.limit() == null ? DEFAULT_UNANSWERED_QUESTIONS_QUERY_SIZE : Math.min(query.limit(), 10)
        );
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

        List<Long> rootIds = questionPage.getContent().stream()
                .map(Question::getId)
                .toList();

        Map<Long, Long> replyCounts = qnaPersistencePort.countRepliesByRootIds(rootIds);

        return LessonQuestionsResponse.from(questionPage, replyCounts);
    }

    @Override
    public List<LessonQuestionItem> findLessonRootQuestionsAll(Long courseId, Long lessonId) {
        Sort sort = Sort.by("lastActivityAt").descending()
                .and(Sort.by("id").descending());

        List<Question> roots = qnaPersistencePort.findRootQuestionsAllByLesson(courseId, lessonId, sort);

        List<Long> rootIds = roots.stream()
                .map(Question::getId)
                .toList();

        Map<Long, Long> replyCounts = qnaPersistencePort.countRepliesByRootIds(rootIds);

        return roots.stream()
                .map(q -> LessonQuestionItem.from(q, replyCounts.getOrDefault(q.getId(), 0L)))
                .toList();
    }

    private void validateInstructorRole(User user) {
        if (user.getRole() != Role.INSTRUCTOR) {
            throw BusinessException.builder(QnaErrorCode.FORBIDDEN_INSTRUCTOR_ONLY).build();
        }
    }

}

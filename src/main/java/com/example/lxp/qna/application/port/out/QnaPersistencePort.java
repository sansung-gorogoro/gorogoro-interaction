package com.example.lxp.qna.application.port.out;

import com.example.lxp.qna.domain.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QnaPersistencePort {

    Question save(Question question);

    Optional<Question> findById(Long questionId);

    void delete(Question question);

    void deleteByThreadId(String threadId);

    void deleteAllByLessonId(Long lessonId);

    void deleteAllByLessonIds(List<Long> lessonIds);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByAuthorId(Long authorId);

    List<Question> findOpenedRootQuestionsByInstructorId(Long instructorId, int limit);

    Page<Question> findRootQuestionsByLesson(Long courseId, Long lessonId, Pageable pageable);

    Map<Long, Long> countRepliesByRootIds(List<Long> rootIds);

    List<Question> findRootQuestionsAllByLesson(Long courseId, Long lessonId, Sort sort);

}

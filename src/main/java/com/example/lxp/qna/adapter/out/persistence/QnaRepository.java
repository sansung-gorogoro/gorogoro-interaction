package com.example.lxp.qna.adapter.out.persistence;

import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaRepository extends JpaRepository<Question, Long> {

    void deleteByThreadId(String threadId);

    void deleteAllByLessonId(Long lessonId);

    void deleteAllByLessonIdIn(List<Long> lessonIds);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByAuthorId(Long authorId);

    @Query("SELECT q FROM Question q WHERE q.instructorId = :instructorId AND q.rootId IS NULL AND q.status = :status ORDER BY q.lastActivityAt DESC")
    List<Question> findRootQuestionsByInstructorIdAndStatus(
            @Param("instructorId") Long instructorId,
            @Param("status") QuestionStatus status,
            Pageable pageable
    );

    @Query("SELECT q FROM Question q WHERE q.courseId = :courseId AND q.lessonId = :lessonId AND q.rootId IS NULL")
    Page<Question> findRootQuestionsByCourseIdAndLessonId(
            @Param("courseId") Long courseId,
            @Param("lessonId") Long lessonId,
            Pageable pageable
    );

    @Query("SELECT q.rootId, COUNT(q) FROM Question q WHERE q.rootId IN :rootIds GROUP BY q.rootId")
    List<Object[]> countRepliesByRootIds(@Param("rootIds") List<Long> rootIds);

    @Query("SELECT q FROM Question q WHERE q.courseId = :courseId AND q.lessonId = :lessonId AND q.rootId IS NULL")
    List<Question> findAllRootQuestionsByCourseIdAndLessonId(
            @Param("courseId") Long courseId,
            @Param("lessonId") Long lessonId,
            Sort sort
    );

}

package com.example.lxp.qna.adapter.out.persistence;

import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import org.springframework.data.domain.Pageable;
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

}

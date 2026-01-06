package com.example.lxp.qna.adapter.out.persistence;

import com.example.lxp.qna.application.port.out.QnaPersistencePort;
import com.example.lxp.qna.domain.model.Question;
import com.example.lxp.qna.domain.model.QuestionStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QnaPersistenceAdapter implements QnaPersistencePort {

    private final QnaRepository qnaRepository;

    public QnaPersistenceAdapter(QnaRepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    @Override
    public Question save(Question question) {
        return qnaRepository.save(question);
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return qnaRepository.findById(questionId);
    }

    @Override
    public void delete(Question question) {
        qnaRepository.delete(question);
    }

    @Override
    public void deleteByThreadId(String threadId) {
        qnaRepository.deleteByThreadId(threadId);
    }

    @Override
    public void deleteAllByLessonId(Long lessonId) {
        qnaRepository.deleteAllByLessonId(lessonId);
    }

    @Override
    public void deleteAllByLessonIds(List<Long> lessonIds) {
        qnaRepository.deleteAllByLessonIdIn(lessonIds);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        qnaRepository.deleteAllByCourseId(courseId);
    }

    @Override
    public void deleteAllByAuthorId(Long authorId) {
        qnaRepository.deleteAllByAuthorId(authorId);
    }

    @Override
    public List<Question> findOpenedRootQuestionsByInstructorId(Long instructorId, int limit) {
        return qnaRepository.findRootQuestionsByInstructorIdAndStatus(
                instructorId,
                QuestionStatus.OPENED,
                PageRequest.of(0, limit)
        );
    }

}

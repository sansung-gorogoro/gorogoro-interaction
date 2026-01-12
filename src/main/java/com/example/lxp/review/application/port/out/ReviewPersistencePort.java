package com.example.lxp.review.application.port.out;

import com.example.lxp.review.domain.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewPersistencePort {

    Review save(Review review);

    Optional<Review> findById(Long reviewId);

    void delete(Review review);

    List<Review> findAllByCourseId(Long courseId);

    long countByCourseId(Long courseId);

    Optional<Review> findByCourseIdAndAuthorId(Long courseId, Long authorId);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByAuthorId(Long authorId);

}

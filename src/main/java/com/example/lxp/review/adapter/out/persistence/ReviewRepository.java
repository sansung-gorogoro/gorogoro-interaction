package com.example.lxp.review.adapter.out.persistence;

import com.example.lxp.review.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseId(Long courseId);

    long countByCourseId(Long courseId);

    Optional<Review> findByCourseIdAndAuthorId(Long courseId, Long authorId);

}

package com.example.lxp.review.adapter.out.persistence;

import com.example.lxp.review.application.port.out.ReviewPersistencePort;
import com.example.lxp.review.domain.model.Review;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReviewPersistenceAdapter implements ReviewPersistencePort {

    private final ReviewRepository reviewRepository;

    public ReviewPersistenceAdapter(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public void delete(Review review) {
        reviewRepository.delete(review);
    }

    @Override
    public List<Review> findAllByCourseId(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    @Override
    public List<Review> findAllByCourseId(Long courseId, Sort sort) {
        return reviewRepository.findByCourseId(courseId, sort);
    }

    @Override
    public long countByCourseId(Long courseId) {
        return reviewRepository.countByCourseId(courseId);
    }

    @Override
    public Optional<Review> findByCourseIdAndAuthorId(Long courseId, Long authorId) {
        return reviewRepository.findByCourseIdAndAuthorId(courseId, authorId);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        reviewRepository.deleteAllByCourseId(courseId);
    }

    @Override
    public void deleteAllByAuthorId(Long authorId) {
        reviewRepository.deleteAllByAuthorId(authorId);
    }

}

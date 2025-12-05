package com.example.lxp.review.domain.event;

import com.example.lxp.review.domain.model.Review;

public class ReviewCreatedEvent {

    private final Long reviewId;
    private final Long courseId;
    private final Long authorId;

    private ReviewCreatedEvent(Long reviewId, Long courseId, Long authorId) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.authorId = authorId;
    }

    public static ReviewCreatedEvent from(Review review) {
        return new ReviewCreatedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
        );
    }

}

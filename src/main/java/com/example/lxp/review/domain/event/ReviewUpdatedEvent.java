package com.example.lxp.review.domain.event;

import com.example.lxp.review.domain.model.Review;

public class ReviewUpdatedEvent {

    private final Long reviewId;
    private final Long courseId;
    private final Long authorId;

    private ReviewUpdatedEvent(Long reviewId, Long courseId, Long authorId) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.authorId = authorId;
    }

    public static ReviewUpdatedEvent from(Review review) {
        return new ReviewUpdatedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
        );
    }

}

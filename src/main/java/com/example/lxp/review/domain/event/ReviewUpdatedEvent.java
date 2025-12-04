package com.example.lxp.review.domain.event;

import com.example.lxp.review.domain.model.Review;

public record ReviewUpdatedEvent(
        Long reviewId,
        Long courseId,
        Long authorId
) {
    public static ReviewUpdatedEvent from(Review review) {
        return new ReviewUpdatedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
        );
    }
}

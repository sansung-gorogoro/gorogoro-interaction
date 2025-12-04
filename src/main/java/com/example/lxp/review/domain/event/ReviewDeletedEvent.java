package com.example.lxp.review.domain.event;

import com.example.lxp.review.domain.model.Review;

public record ReviewDeletedEvent(
        Long reviewId,
        Long courseId,
        Long authorId
) {
    public static ReviewDeletedEvent from(Review review) {
        return new ReviewDeletedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
        );
    }
}

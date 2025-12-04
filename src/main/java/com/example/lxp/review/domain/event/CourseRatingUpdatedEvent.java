package com.example.lxp.review.domain.event;

public record CourseRatingUpdatedEvent(
    Long courseId,
    double newAverageRating,
    long newReviewCount
) {
}

package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;

public record CourseRatingUpdatedEvent(

        Long courseId,
        double newAverageRating,
        long newReviewCount

) implements DomainEvent {

    public static CourseRatingUpdatedEvent of(
            Long courseId,
            double newAverageRating,
            long newReviewCount
    ) {
        return new CourseRatingUpdatedEvent(
                courseId,
                newAverageRating,
                newReviewCount
        );
    }

    @Override
    public String type() {
        return "review.course-rating.updated";
    }

    @Override
    public String version() {
        return DomainEvent.super.version();
    }

}

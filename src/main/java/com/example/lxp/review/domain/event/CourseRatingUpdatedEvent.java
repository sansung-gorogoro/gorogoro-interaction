package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;

public record CourseRatingUpdatedEvent(

        Long courseId,
        double newAverageRating,
        long newReviewCount

) implements DomainEvent {

    public static final String TYPE = "review.course-rating.updated";

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
        return TYPE;
    }

    @Override
    public String version() {
        return DomainEvent.super.version();
    }

}

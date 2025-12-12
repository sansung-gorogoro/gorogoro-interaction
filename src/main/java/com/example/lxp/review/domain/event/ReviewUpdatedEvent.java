package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.review.domain.model.Review;

public record ReviewUpdatedEvent(

        Long reviewId,
        Long courseId,
        Long authorId

) implements DomainEvent {

    public static final String TYPE = "review.updated";

    public static ReviewUpdatedEvent from(Review review) {
        return new ReviewUpdatedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
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

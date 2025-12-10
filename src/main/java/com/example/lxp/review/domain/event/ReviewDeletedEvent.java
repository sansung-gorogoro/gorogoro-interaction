package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.review.domain.model.Review;

public record ReviewDeletedEvent(

        Long reviewId,
        Long courseId,
        Long authorId

) implements DomainEvent {

    public static ReviewDeletedEvent from(Review review) {
        return new ReviewDeletedEvent(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId()
        );
    }

    @Override
    public String type() {
        return "";
    }

    @Override
    public String version() {
        return DomainEvent.super.version();
    }

}

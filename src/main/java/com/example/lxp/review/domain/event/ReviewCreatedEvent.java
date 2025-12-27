package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.review.domain.model.Review;

public class ReviewCreatedEvent implements DomainEvent {

    private static final String SERVICE_PREFIX = "review";
    private static final String EVENT_NAME = "review-created";

    private final long reviewId;
    private final long courseId;
    private final long authorId;

    public ReviewCreatedEvent(long reviewId, long courseId, long authorId) {
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

    @Override
    public String getServicePrefix() {
        return SERVICE_PREFIX;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    public long getReviewId() {
        return reviewId;
    }

    public long getCourseId() {
        return courseId;
    }

    public long getAuthorId() {
        return authorId;
    }

    @Override
    public String toString() {
        return "ReviewCreatedEvent{" +
                "reviewId=" + reviewId +
                ", courseId=" + courseId +
                ", authorId=" + authorId +
                '}';
    }

}

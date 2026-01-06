package com.example.lxp.review.domain.event;

import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.review.domain.model.Review;

public class ReviewDeletedEvent implements DomainEvent {

    private static final String SERVICE_PREFIX = "review";
    private static final String EVENT_NAME = "review-deleted";

    private final long reviewId;
    private final long courseId;
    private final long authorId;

    public ReviewDeletedEvent(long reviewId, long courseId, long authorId) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.authorId = authorId;
    }

    public static ReviewDeletedEvent from(Review review) {
        return new ReviewDeletedEvent(
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
        return "ReviewDeletedEvent{" +
                "reviewId=" + reviewId +
                ", courseId=" + courseId +
                ", authorId=" + authorId +
                '}';
    }

}

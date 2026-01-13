package com.example.lxp.review.adapter.in.web.dto;

import com.example.lxp.review.domain.model.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewItemResponse {

    private final Long reviewId;
    private final Long courseId;
    private final Long userId;
    private final String userNickname;
    private final String title;
    private final String comment;
    private final Integer rating;
    private final Instant updatedAt;

    public static ReviewItemResponse from(Review review, String userNickname) {
        return new ReviewItemResponse(
                review.getId(),
                review.getCourseId(),
                review.getAuthorId(),
                userNickname,
                review.getTitle(),
                review.getComment(),
                review.getRating().getStars(),
                review.getUpdatedAt()
        );
    }
}

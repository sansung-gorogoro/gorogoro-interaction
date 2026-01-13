package com.example.lxp.review.adapter.in.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewListResponse {

    private final List<ReviewItemResponse> reviews;

    public static ReviewListResponse of(List<ReviewItemResponse> reviews) {
        return new ReviewListResponse(reviews);
    }
}

package com.example.lxp.review.adapter.in.web.dto;

public record UpdateReviewRequest(
        String title,
        String comment,
        Integer stars
) {
}

package com.example.lxp.review.application.port.in.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteReviewCommand(
        @NotNull Long authorId,
        @NotNull Long courseId,
        @NotNull Long reviewId
) {
}

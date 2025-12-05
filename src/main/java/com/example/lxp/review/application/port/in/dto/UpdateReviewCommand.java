package com.example.lxp.review.application.port.in.dto;

import com.example.lxp.review.domain.model.Rating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateReviewCommand(
        @NotNull Long authorId,
        @NotNull Long courseId,
        @NotNull Long reviewId,
        @NotBlank String title,
        @NotBlank String comment,
        @NotNull Rating rating
) {
}

package com.example.lxp.review.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateReviewRequest(
        @NotBlank String title,
        @NotBlank String comment,
        @NotNull Integer stars
) {
}

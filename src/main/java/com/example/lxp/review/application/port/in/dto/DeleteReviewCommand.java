package com.example.lxp.review.application.port.in.dto;

import com.example.lxp.common.auth.model.User;
import jakarta.validation.constraints.NotNull;

public record DeleteReviewCommand(
        @NotNull Long courseId,
        @NotNull Long reviewId,
        @NotNull User user
) {
}

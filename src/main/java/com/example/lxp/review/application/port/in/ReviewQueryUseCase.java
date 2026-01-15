package com.example.lxp.review.application.port.in;

import com.example.lxp.review.adapter.in.web.dto.ReviewListResponse;

public interface ReviewQueryUseCase {

    ReviewListResponse getReviewsByCourseId(Long courseId);
}

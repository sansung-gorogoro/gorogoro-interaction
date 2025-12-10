package com.example.lxp.review.application.port.in;

import com.example.lxp.review.application.port.in.dto.DeleteReviewsByCourseCommand;

public interface ReviewIntegrationUseCase {

    void deleteReviewsByCourseId(DeleteReviewsByCourseCommand event);

}

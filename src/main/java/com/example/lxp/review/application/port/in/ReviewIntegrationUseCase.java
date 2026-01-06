package com.example.lxp.review.application.port.in;

import com.example.lxp.review.application.port.in.dto.DeleteReviewsByCourseCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByUserCommand;

public interface ReviewIntegrationUseCase {

    void deleteReviewsByCourseId(DeleteReviewsByCourseCommand command);

    void deleteReviewsByAuthorId(DeleteReviewsByUserCommand command);

}

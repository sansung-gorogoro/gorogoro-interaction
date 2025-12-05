package com.example.lxp.review.application.port.in;

import com.example.lxp.review.application.port.in.dto.CreateReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewCommand;
import com.example.lxp.review.application.port.in.dto.UpdateReviewCommand;

public interface ReviewCommandUseCase {

    void createReview(CreateReviewCommand command);

    void updateReview(UpdateReviewCommand command);

    void deleteReview(DeleteReviewCommand command);

}

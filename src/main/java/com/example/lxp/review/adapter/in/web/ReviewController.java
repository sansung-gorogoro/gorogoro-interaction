package com.example.lxp.review.adapter.in.web;

import com.example.lxp.review.adapter.in.web.dto.CreateReviewRequest;
import com.example.lxp.review.adapter.in.web.dto.UpdateReviewRequest;
import com.example.lxp.review.application.port.in.ReviewCommandUseCase;
import com.example.lxp.review.application.port.in.dto.CreateReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewCommand;
import com.example.lxp.review.application.port.in.dto.UpdateReviewCommand;
import com.example.lxp.review.domain.model.Rating;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
public class ReviewController {

    // TODO: replace with authenticated user id once credential is integrated
    private static final Long USER_ID_STUB = 1L;

    private final ReviewCommandUseCase reviewCommandUseCase;

    public ReviewController(ReviewCommandUseCase reviewCommandUseCase) {
        this.reviewCommandUseCase = reviewCommandUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createReview(
            @PathVariable Long courseId,
            @RequestBody @Valid CreateReviewRequest body
    ) {
        CreateReviewCommand command = new CreateReviewCommand(
                USER_ID_STUB,
                courseId,
                body.title(),
                body.comment(),
                Rating.of(body.stars())
        );
        reviewCommandUseCase.createReview(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest body
    ) {
        UpdateReviewCommand command = new UpdateReviewCommand(
                USER_ID_STUB,
                courseId,
                reviewId,
                body.title(),
                body.comment(),
                Rating.of(body.stars())
        );
        reviewCommandUseCase.updateReview(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long courseId,
            @PathVariable Long reviewId
    ) {
        DeleteReviewCommand command = new DeleteReviewCommand(
                USER_ID_STUB,
                courseId,
                reviewId
        );
        reviewCommandUseCase.deleteReview(command);
        return ResponseEntity.noContent().build();
    }

}

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

    private static final String HEADER_USER_ID = "X-User-Id";

    private final ReviewCommandUseCase reviewCommandUseCase;

    public ReviewController(ReviewCommandUseCase reviewCommandUseCase) {
        this.reviewCommandUseCase = reviewCommandUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long courseId,
            @RequestBody @Valid CreateReviewRequest body
    ) {
        CreateReviewCommand command = new CreateReviewCommand(
                userId,
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
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest body
    ) {
        UpdateReviewCommand command = new UpdateReviewCommand(
                userId,
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
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long courseId,
            @PathVariable Long reviewId
    ) {
        DeleteReviewCommand command = new DeleteReviewCommand(
                userId,
                courseId,
                reviewId
        );
        reviewCommandUseCase.deleteReview(command);
        return ResponseEntity.noContent().build();
    }

}

package com.example.lxp.review.adapter.in.web;

import com.example.lxp.common.auth.model.Role;
import com.example.lxp.common.auth.model.User;
import com.example.lxp.review.adapter.in.web.dto.CreateReviewRequest;
import com.example.lxp.review.adapter.in.web.dto.UpdateReviewRequest;
import com.example.lxp.review.application.port.in.ReviewCommandUseCase;
import com.example.lxp.review.application.port.in.dto.CreateReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewCommand;
import com.example.lxp.review.application.port.in.dto.UpdateReviewCommand;
import com.example.lxp.review.domain.model.Rating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
public class ReviewController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    private final ReviewCommandUseCase reviewCommandUseCase;

    public ReviewController(ReviewCommandUseCase reviewCommandUseCase) {
        this.reviewCommandUseCase = reviewCommandUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createReview(
            @PathVariable @Positive Long courseId,
            @RequestHeader(HEADER_USER_ID) @Positive Long userId,
            @RequestHeader(HEADER_USER_ROLE) @NotNull String userRole,
            @RequestBody @Valid CreateReviewRequest request
    ) {
        CreateReviewCommand command = new CreateReviewCommand(
                courseId,
                new User(userId, Role.valueOf(userRole)),
                request.title(),
                request.comment(),
                Rating.of(request.stars())
        );
        reviewCommandUseCase.createReview(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestHeader(HEADER_USER_ROLE) String userRole,
            @RequestBody @Valid UpdateReviewRequest request
    ) {
        UpdateReviewCommand command = new UpdateReviewCommand(
                courseId,
                reviewId,
                new User(userId, Role.valueOf(userRole)),
                request.title(),
                request.comment(),
                Rating.of(request.stars())
        );
        reviewCommandUseCase.updateReview(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestHeader(HEADER_USER_ROLE) String userRole
    ) {
        DeleteReviewCommand command = new DeleteReviewCommand(
                courseId,
                reviewId,
                new User(userId, Role.valueOf(userRole))
        );
        reviewCommandUseCase.deleteReview(command);
        return ResponseEntity.noContent().build();
    }

}

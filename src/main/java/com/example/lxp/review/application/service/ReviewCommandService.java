package com.example.lxp.review.application.service;

import com.example.lxp.common.port.out.PublishEventPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import com.example.lxp.review.application.port.in.ReviewCommandUseCase;
import com.example.lxp.review.application.port.in.dto.CreateReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewCommand;
import com.example.lxp.review.application.port.in.dto.UpdateReviewCommand;
import com.example.lxp.review.application.port.out.CheckEnrollmentPort;
import com.example.lxp.review.application.port.out.ReviewPersistencePort;
import com.example.lxp.review.domain.event.CourseRatingUpdatedEvent;
import com.example.lxp.review.domain.event.ReviewCreatedEvent;
import com.example.lxp.review.domain.event.ReviewDeletedEvent;
import com.example.lxp.review.domain.event.ReviewUpdatedEvent;
import com.example.lxp.review.domain.model.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewCommandService implements ReviewCommandUseCase {

    private final CheckEnrollmentPort checkEnrollmentPort;
    private final ReviewPersistencePort reviewPersistencePort;
    private final PublishEventPort publishEventPort;

    public ReviewCommandService(
            CheckEnrollmentPort checkEnrollmentPort,
            ReviewPersistencePort reviewPersistencePort,
            PublishEventPort publishEventPort
    ) {
        this.checkEnrollmentPort = checkEnrollmentPort;
        this.reviewPersistencePort = reviewPersistencePort;
        this.publishEventPort = publishEventPort;
    }

    @Override
    public void createReview(CreateReviewCommand command) {
        // EXTERNAL: OUTGOING
        if (!checkEnrollmentPort.isEnrolled(command.authorId(), command.courseId())) {
            throw BusinessException.builder(ErrorCode.USER_NOT_ENROLLED_IN_COURSE).build();
        }
        if (reviewPersistencePort.findByCourseIdAndAuthorId(command.authorId(), command.courseId()).isPresent()) {
            throw BusinessException.builder(ErrorCode.REVIEW_ALREADY_EXISTS).build();
        }
        Review review = Review.create(
                command.authorId(),
                command.courseId(),
                command.title(),
                command.comment(),
                command.rating()
        );
        Review savedReview = reviewPersistencePort.save(review);

        publishEventPort.publish(ReviewCreatedEvent.from(savedReview));
        calculateAndPublishCourseRatingUpdate(command.courseId());
    }

    @Override
    public void updateReview(UpdateReviewCommand command) {
        Review review = findAndValidateReview(command.reviewId(), command.authorId(), command.courseId());

        review.update(
                command.authorId(),
                command.title(),
                command.comment(),
                command.rating()
        );
        Review updatedReview = reviewPersistencePort.save(review);

        publishEventPort.publish(ReviewUpdatedEvent.from(updatedReview));
        calculateAndPublishCourseRatingUpdate(command.courseId());
    }

    @Override
    public void deleteReview(DeleteReviewCommand command) {
        Review review = findAndValidateReview(command.reviewId(), command.authorId(), command.courseId());

        reviewPersistencePort.delete(review);

        publishEventPort.publish(ReviewDeletedEvent.from(review));
        calculateAndPublishCourseRatingUpdate(command.courseId());
    }

    private Review findAndValidateReview(Long reviewId, Long authorId, Long courseId) {
        Review review = reviewPersistencePort.findById(reviewId)
                .orElseThrow(() -> BusinessException.builder(ErrorCode.REVIEW_NOT_FOUND).build());
        if (!review.getAuthorId().equals(authorId)) {
            throw BusinessException.builder(ErrorCode.FORBIDDEN_REVIEW_MODIFICATION).build();
        }
        if (!review.getCourseId().equals(courseId)) {
            throw BusinessException.builder(ErrorCode.INVALID_COURSE_ID_FOR_REVIEW).build();
        }
        return review;
    }

    private void calculateAndPublishCourseRatingUpdate(Long courseId) {
        List<Review> reviews = reviewPersistencePort.findAllByCourseId(courseId);
        long newReviewCount = reviews.size();
        double newAverageRating = reviews.stream()
                .mapToInt(review -> review.getRating().getStars())
                .average()
                .orElse(0.0);
        publishEventPort.publish(new CourseRatingUpdatedEvent(courseId, newAverageRating, newReviewCount));
    }

}

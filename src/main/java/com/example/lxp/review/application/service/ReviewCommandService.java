package com.example.lxp.review.application.service;

import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.application.port.in.ReviewCommandUseCase;
import com.example.lxp.review.application.port.in.ReviewIntegrationUseCase;
import com.example.lxp.review.application.port.in.dto.CreateReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByCourseCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByUserCommand;
import com.example.lxp.review.application.port.in.dto.UpdateReviewCommand;
import com.example.lxp.review.application.port.out.ReviewEnrollmentClientPort;
import com.example.lxp.review.application.port.out.ReviewPersistencePort;
import com.example.lxp.review.domain.event.ReviewCreatedEvent;
import com.example.lxp.review.domain.event.ReviewDeletedEvent;
import com.example.lxp.review.domain.event.ReviewUpdatedEvent;
import com.example.lxp.review.domain.model.Review;
import com.example.lxp.review.exception.ReviewErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewCommandService implements ReviewCommandUseCase, ReviewIntegrationUseCase {

    private final ReviewEnrollmentClientPort enrollmentClientPort;
    private final ReviewPersistencePort reviewPersistencePort;
    private final EventPublisherPort eventPublisherPort;

    public ReviewCommandService(
            ReviewEnrollmentClientPort enrollmentClientPort,
            ReviewPersistencePort reviewPersistencePort,
            EventPublisherPort eventPublisherPort
    ) {
        this.enrollmentClientPort = enrollmentClientPort;
        this.reviewPersistencePort = reviewPersistencePort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public void createReview(CreateReviewCommand command) {
        // EXTERNAL: OUTGOING
        if (!enrollmentClientPort.isEnrolled(command.courseId(), command.user().getId())) {
            throw BusinessException.builder(ReviewErrorCode.USER_NOT_ENROLLED_IN_COURSE).build();
        }
        if (reviewPersistencePort.findByCourseIdAndAuthorId(command.courseId(), command.user().getId()).isPresent()) {
            throw BusinessException.builder(ReviewErrorCode.REVIEW_ALREADY_EXISTS).build();
        }
        Review review = Review.create(
                command.user().getId(),
                command.courseId(),
                command.title(),
                command.comment(),
                command.rating()
        );
        Review savedReview = reviewPersistencePort.save(review);

        eventPublisherPort.publish(ReviewCreatedEvent.from(savedReview));
    }

    @Override
    public void updateReview(UpdateReviewCommand command) {
        Review review = findAndValidateReview(command.reviewId(), command.user().getId(), command.courseId());

        review.update(
                command.user().getId(),
                command.title(),
                command.comment(),
                command.rating()
        );
        Review updatedReview = reviewPersistencePort.save(review);

        eventPublisherPort.publish(ReviewUpdatedEvent.from(updatedReview));
    }

    @Override
    public void deleteReview(DeleteReviewCommand command) {
        Review review = findAndValidateReview(command.reviewId(), command.user().getId(), command.courseId());

        reviewPersistencePort.delete(review);

        eventPublisherPort.publish(ReviewDeletedEvent.from(review));
    }

    @Override
    public void deleteReviewsByCourseId(DeleteReviewsByCourseCommand command) {
        reviewPersistencePort.deleteAllByCourseId(command.courseId());
    }

    @Override
    public void deleteReviewsByAuthorId(DeleteReviewsByUserCommand command) {
        reviewPersistencePort.deleteAllByAuthorId(command.authorId());
    }

    private Review findAndValidateReview(Long reviewId, Long authorId, Long courseId) {
        Review review = reviewPersistencePort.findById(reviewId)
                .orElseThrow(() -> BusinessException.builder(ReviewErrorCode.REVIEW_NOT_FOUND).build());
        if (!review.getAuthorId().equals(authorId)) {
            throw BusinessException.builder(ReviewErrorCode.FORBIDDEN_REVIEW_MODIFICATION).build();
        }
        if (!review.getCourseId().equals(courseId)) {
            throw BusinessException.builder(ReviewErrorCode.REVIEW_COURSE_ID_MISMATCH).build();
        }
        return review;
    }

}

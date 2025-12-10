package com.example.lxp.review.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReviewErrorCode implements ErrorCode {

    // Web Controller Error ----------

    // 400
    RATING_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5"),
    PROGRESS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Progress must be between 0 and 100"),
    REVIEW_TITLE_IS_BLANK(HttpStatus.BAD_REQUEST, "Review title cannot be blank"),
    REVIEW_COMMENT_IS_BLANK(HttpStatus.BAD_REQUEST, "Review comment cannot be blank"),
    REVIEW_RATING_IS_BLANK(HttpStatus.BAD_REQUEST, "Review rating is required."),
    REVIEW_COURSE_ID_MISMATCH(HttpStatus.BAD_REQUEST, "The review's course ID does not match the requested course."),

    // 403
    FORBIDDEN_REVIEW_MODIFICATION(HttpStatus.FORBIDDEN, "Only the author can modify the review"),
    USER_NOT_ENROLLED_IN_COURSE(HttpStatus.FORBIDDEN, "You can only write reviews for courses you are enrolled in."),

    // 404
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Review not found"),

    // 409
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "You have already submitted a review for this course."),

    // Message Queue Error ----------

    // 500
    EVENT_COURSE_ID_IS_BLANK(HttpStatus.BAD_REQUEST, "courseId in event payload must not be null.");

    private final HttpStatus status;
    private final String message;

    ReviewErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}

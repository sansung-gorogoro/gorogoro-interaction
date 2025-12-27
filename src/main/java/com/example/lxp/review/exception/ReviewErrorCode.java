package com.example.lxp.review.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReviewErrorCode implements ErrorCode {

    // Web Controller Error ----------

    RATING_OUT_OF_RANGE("REV-0001", HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5"),
    PROGRESS_OUT_OF_RANGE("REV-0002", HttpStatus.BAD_REQUEST, "Progress must be between 0 and 100"),
    REVIEW_TITLE_IS_BLANK("REV-0003", HttpStatus.BAD_REQUEST, "Review title cannot be blank"),
    REVIEW_COMMENT_IS_BLANK("REV-0004", HttpStatus.BAD_REQUEST, "Review comment cannot be blank"),
    REVIEW_RATING_IS_BLANK("REV-0005", HttpStatus.BAD_REQUEST, "Review rating is required."),
    REVIEW_COURSE_ID_MISMATCH("REV-0006", HttpStatus.BAD_REQUEST, "The review's course ID does not match the requested course."),
    FORBIDDEN_REVIEW_MODIFICATION("REV-0007", HttpStatus.FORBIDDEN, "Only the author can modify the review"),
    USER_NOT_ENROLLED_IN_COURSE("REV-0008", HttpStatus.FORBIDDEN, "You can only write reviews for courses you are enrolled in."),
    REVIEW_NOT_FOUND("REV-0009", HttpStatus.NOT_FOUND, "Review not found"),
    REVIEW_ALREADY_EXISTS("REV-0010", HttpStatus.CONFLICT, "You have already submitted a review for this course."),

    // Message Queue Error ----------

    EVENT_COURSE_ID_IS_BLANK("REV-0011", HttpStatus.BAD_REQUEST, "courseId in event payload must not be null.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ReviewErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.httpStatus = status;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name();
    }

}

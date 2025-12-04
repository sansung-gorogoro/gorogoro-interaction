package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Internal Error
    RATING_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5"),
    PROGRESS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Progress must be between 0 and 100"),
    FORBIDDEN_REVIEW_MODIFICATION(HttpStatus.FORBIDDEN, "Only the author can modify the review"),

    INVALID_REVIEW_TITLE(HttpStatus.BAD_REQUEST, "Invalid review title"),
    INVALID_REVIEW_COMMENT(HttpStatus.BAD_REQUEST, "Invalid review comment"),
    INVALID_REVIEW_RATING(HttpStatus.BAD_REQUEST, "Invalid review rating"),

    // External Error
    USER_NOT_ENROLLED_IN_COURSE(HttpStatus.FORBIDDEN, "You can only write reviews for courses you are enrolled in.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
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

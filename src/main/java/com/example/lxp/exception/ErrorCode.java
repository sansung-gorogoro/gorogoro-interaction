package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 Bad Request & 409 Conflict: Domain/Business Rule Violations
    RATING_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5"),
    PROGRESS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Progress must be between 0 and 100"),
    INVALID_REVIEW_TITLE(HttpStatus.BAD_REQUEST, "Invalid review title"),
    INVALID_REVIEW_COMMENT(HttpStatus.BAD_REQUEST, "Invalid review comment"),
    INVALID_REVIEW_RATING(HttpStatus.BAD_REQUEST, "Invalid review rating"),
    INVALID_COURSE_ID_FOR_REVIEW(HttpStatus.BAD_REQUEST, "The specified course ID does not match the review's course ID."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "You have already submitted a review for this course."),

    // 403 Forbidden: Authorization Errors
    FORBIDDEN_REVIEW_MODIFICATION(HttpStatus.FORBIDDEN, "Only the author can modify the review"),
    USER_NOT_ENROLLED_IN_COURSE(HttpStatus.FORBIDDEN, "You can only write reviews for courses you are enrolled in."),

    // 404 Not Found: Resource Not Found Errors
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Review not found"),

    // QnA Domain Error Codes
    INVALID_QUESTION_TITLE(HttpStatus.BAD_REQUEST, "Invalid question title"),
    INVALID_QUESTION_CONTENT(HttpStatus.BAD_REQUEST, "Invalid question content"),
    FORBIDDEN_QUESTION_MODIFICATION(HttpStatus.FORBIDDEN, "Only the author can modify the question"),
    FORBIDDEN_QUESTION_REPLY(HttpStatus.FORBIDDEN, "Only the root author or instructor can reply to this question"),
    INVALID_QUESTION_OPERATION(HttpStatus.BAD_REQUEST, "Invalid operation for the current question type or status."),
    NOT_A_ROOT_QUESTION(HttpStatus.BAD_REQUEST, "This operation can only be performed on a root question."),
    ID_NOT_GENERATED(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot perform operation before ID is generated."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Question not found");

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

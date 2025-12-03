package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // BAD_REQUEST(HttpStatus.BAD_REQUEST, "This is sample message!");
    RATING_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5"),
    PROGRESS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Progress must be between 0 and 100");

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

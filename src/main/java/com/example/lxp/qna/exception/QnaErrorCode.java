package com.example.lxp.qna.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum QnaErrorCode implements ErrorCode {

    // 400
    QUESTION_TITLE_IS_BLANK(HttpStatus.BAD_REQUEST, "Question title cannot be blank for a root question"),
    QUESTION_CONTENT_IS_BLANK(HttpStatus.BAD_REQUEST, "Question content cannot be blank"),
    QUESTION_CONTEXT_MISMATCH(HttpStatus.BAD_REQUEST, "The course/lesson context of the reply does not match the root question"),
    CANNOT_REPLY_TO_REPLY(HttpStatus.BAD_REQUEST, "Cannot reply to a question that is already a reply"),
    CANNOT_RESOLVE_REPLY(HttpStatus.BAD_REQUEST, "Cannot resolve a question that is a reply"),

    // 403
    FORBIDDEN_QUESTION_MODIFICATION(HttpStatus.FORBIDDEN, "Only the author can modify the question"),
    FORBIDDEN_QUESTION_REPLY(HttpStatus.FORBIDDEN, "Only the root author or instructor can reply to this question"),

    // 404
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Question not found"),

    // 500
    QUESTION_THREAD_ID_IS_BLANK(HttpStatus.INTERNAL_SERVER_ERROR, "Question thread ID cannot be blank");

    private final HttpStatus status;
    private final String message;

    QnaErrorCode(HttpStatus status, String message) {
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

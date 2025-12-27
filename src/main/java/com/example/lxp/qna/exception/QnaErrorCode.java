package com.example.lxp.qna.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum QnaErrorCode implements ErrorCode {

    QUESTION_TITLE_IS_BLANK("QNA-0001", HttpStatus.BAD_REQUEST, "Question title cannot be blank for a root question"),
    QUESTION_CONTENT_IS_BLANK("QNA-0002", HttpStatus.BAD_REQUEST, "Question content cannot be blank"),
    QUESTION_CONTEXT_MISMATCH("QNA-0003", HttpStatus.BAD_REQUEST, "The course/lesson context of the reply does not match the root question"),
    CANNOT_REPLY_TO_REPLY("QNA-0004", HttpStatus.BAD_REQUEST, "Cannot reply to a question that is already a reply"),
    CANNOT_RESOLVE_REPLY("QNA-0005", HttpStatus.BAD_REQUEST, "Cannot resolve a question that is a reply"),
    FORBIDDEN_QUESTION_MODIFICATION("QNA-0006", HttpStatus.FORBIDDEN, "Only the author can modify the question"),
    FORBIDDEN_QUESTION_REPLY("QNA-0007", HttpStatus.FORBIDDEN, "Only the root author or instructor can reply to this question"),
    QUESTION_NOT_FOUND("QNA-0008", HttpStatus.NOT_FOUND, "Question not found"),
    QUESTION_THREAD_ID_IS_BLANK("QNA-0009", HttpStatus.INTERNAL_SERVER_ERROR, "Question thread ID cannot be blank");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    QnaErrorCode(String code, HttpStatus status, String message) {
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

package com.example.lxp.qna.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum QnaErrorCode implements ErrorCode {

    QUESTION_TITLE_IS_BLANK("QNA-0001", HttpStatus.BAD_REQUEST, "질문 제목은 필수 입력 항목입니다."),
    QUESTION_CONTENT_IS_BLANK("QNA-0002", HttpStatus.BAD_REQUEST, "질문 내용은 필수 입력 항목입니다."),
    QUESTION_CONTEXT_MISMATCH("QNA-0003", HttpStatus.BAD_REQUEST, "답변의 강의 정보가 원본 질문과 일치하지 않습니다."),
    CANNOT_REPLY_TO_REPLY("QNA-0004", HttpStatus.BAD_REQUEST, "답변에는 답변을 작성할 수 없습니다."),
    CANNOT_RESOLVE_REPLY("QNA-0005", HttpStatus.BAD_REQUEST, "답변은 해결 처리할 수 없습니다."),
    FORBIDDEN_QUESTION_MODIFICATION("QNA-0006", HttpStatus.FORBIDDEN, "질문을 수정할 권한이 없습니다."),
    FORBIDDEN_QUESTION_REPLY("QNA-0007", HttpStatus.FORBIDDEN, "질문에 답변할 권한이 없습니다."),
    QUESTION_NOT_FOUND("QNA-0008", HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    FORBIDDEN_INSTRUCTOR_ONLY("QNA-0010", HttpStatus.FORBIDDEN, "강사만 접근할 수 있습니다."),
    QUESTION_THREAD_ID_IS_BLANK("QNA-0009", HttpStatus.INTERNAL_SERVER_ERROR, "질문 스레드 ID가 누락되었습니다.");

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

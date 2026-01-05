package com.example.lxp.review.exception;

import com.example.lxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReviewErrorCode implements ErrorCode {

    // Web Controller Error ----------

    RATING_OUT_OF_RANGE("REV-0001", HttpStatus.BAD_REQUEST, "평점은 0점에서 5점 사이여야 합니다."),
    PROGRESS_OUT_OF_RANGE("REV-0002", HttpStatus.BAD_REQUEST, "진행률은 0%에서 100% 사이여야 합니다."),
    REVIEW_TITLE_IS_BLANK("REV-0003", HttpStatus.BAD_REQUEST, "후기 제목은 필수 입력 항목입니다."),
    REVIEW_COMMENT_IS_BLANK("REV-0004", HttpStatus.BAD_REQUEST, "후기 내용은 필수 입력 항목입니다."),
    REVIEW_RATING_IS_BLANK("REV-0005", HttpStatus.BAD_REQUEST, "평점은 필수 입력 항목입니다."),
    REVIEW_COURSE_ID_MISMATCH("REV-0006", HttpStatus.BAD_REQUEST, "후기의 강의 정보가 요청과 일치하지 않습니다."),
    FORBIDDEN_REVIEW_MODIFICATION("REV-0007", HttpStatus.FORBIDDEN, "후기를 수정할 권한이 없습니다."),
    USER_NOT_ENROLLED_IN_COURSE("REV-0008", HttpStatus.FORBIDDEN, "수강 중인 강의에만 후기를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND("REV-0009", HttpStatus.NOT_FOUND, "후기를 찾을 수 없습니다."),
    REVIEW_ALREADY_EXISTS("REV-0010", HttpStatus.CONFLICT, "이미 해당 강의에 후기를 작성하셨습니다."),

    // Message Queue Error ----------

    EVENT_COURSE_ID_IS_BLANK("REV-0011", HttpStatus.BAD_REQUEST, "이벤트 페이로드에 강의 ID가 누락되었습니다.");

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

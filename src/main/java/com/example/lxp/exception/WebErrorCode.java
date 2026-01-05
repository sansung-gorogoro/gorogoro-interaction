package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

public enum WebErrorCode implements ErrorCode {

    VALIDATION_FAILED("WEB-0001", HttpStatus.BAD_REQUEST, "요청 필드 검증에 실패했습니다."),
    // TYPE_MISMATCH("WEB-0002", HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다."),
    MESSAGE_NOT_READABLE("WEB-0003", HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."),
    PARAMETER_MISSING("WEB-0004", HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    PATH_VARIABLE_MISSING("WEB-0005", HttpStatus.BAD_REQUEST, "필수 경로 변수가 누락되었습니다."),
    HANDLER_NOT_FOUND("WEB-0006", HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_SUPPORTED("WEB-0007", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    MEDIA_TYPE_NOT_SUPPORTED("WEB-0008", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type 입니다."),
    MESSAGE_NOT_WRITABLE("WEB-0009", HttpStatus.INTERNAL_SERVER_ERROR, "응답을 생성하는 중 오류가 발생했습니다."),
    DATA_ACCESS_ERROR("WEB-0010", HttpStatus.INTERNAL_SERVER_ERROR, "데이터 처리 중 오류가 발생했습니다."),
    INTERNAL_ERROR("WEB-0011", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    WebErrorCode(String code, HttpStatus status, String message) {
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

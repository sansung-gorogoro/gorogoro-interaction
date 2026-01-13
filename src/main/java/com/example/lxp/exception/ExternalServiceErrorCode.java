package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

public enum ExternalServiceErrorCode implements ErrorCode {

    DEPENDENCY_TIMEOUT("EXT-0001", HttpStatus.GATEWAY_TIMEOUT, "외부 서비스 응답 시간 초과"),
    DEPENDENCY_UNAVAILABLE("EXT-0002", HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 연결 불가"),
    DEPENDENCY_BAD_RESPONSE("EXT-0003", HttpStatus.BAD_GATEWAY, "외부 서비스 응답 오류"),
    DEPENDENCY_CONTRACT_ERROR("EXT-0004", HttpStatus.INTERNAL_SERVER_ERROR, "외부 서비스 계약 위반");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ExternalServiceErrorCode(String code, HttpStatus status, String message) {
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

package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Throwable cause;

    private BusinessException(Builder builder) {
        super(builder.getMessage(), builder.cause);
        this.errorCode = builder.getErrorCode();
        this.cause = builder.cause;
    }

    public static BusinessExceptionBuilder builder(ErrorCode errorCode) {
        return new Builder(errorCode);
    }

    public HttpStatus getHttpStatus() {
        return this.errorCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return this.errorCode.getMessage();
    }

    public ErrorCode getCode() {
        return this.errorCode;
    }

    public Throwable getCause() {
        return cause;
    }

    public interface BusinessExceptionBuilder {
        BusinessExceptionBuilder withCause(Throwable cause);

        BusinessException build();
    }

    private static class Builder implements BusinessExceptionBuilder {
        private final ErrorCode errorCode;
        private final String message;
        private final List<Object> params = new ArrayList<>();
        private Throwable cause;

        public Builder(ErrorCode errorCode) {
            this.errorCode = errorCode;
            this.message = errorCode.getMessage();
        }

        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        private String getMessage() {
            return message;
        }

        private ErrorCode getErrorCode() {
            return this.errorCode;
        }

        public BusinessException build() {
            return new BusinessException(this);
        }

    }

}

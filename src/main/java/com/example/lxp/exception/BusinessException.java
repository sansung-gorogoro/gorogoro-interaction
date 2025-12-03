package com.example.lxp.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private Throwable cause;

    private BusinessException(Builder builder) {
        super(builder.getMessage(), builder.cause);
        this.httpStatus = builder.httpStatus;
        this.message = builder.getMessage();
        this.cause = builder.cause;
    }

    public interface BusinessExceptionBuilder {
        BusinessExceptionBuilder withId(Long... ids);
        BusinessExceptionBuilder withField(String... fields);
        BusinessExceptionBuilder withCount(int count);
        BusinessExceptionBuilder withCause(Throwable cause);

        BusinessException build();
    }

    public static BusinessExceptionBuilder builder(ErrorCode errorCode) {
        return new Builder(errorCode);
    }

    private static class Builder implements BusinessExceptionBuilder {
        private final HttpStatus httpStatus;
        private final String messageTemplate;
        private final List<Object> params = new ArrayList<>();
        private Throwable cause;

        public Builder(ErrorCode errorCode) {
            this.httpStatus = errorCode.getStatus();
            this.messageTemplate = errorCode.getMessage();
        }

        public Builder withId(Long... ids) {
            this.params.addAll(Arrays.asList(ids));
            return this;
        }

        public Builder withField(String... fields) {
            this.params.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder withCount(int count) {
            this.params.add(count);
            return this;
        }

        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        private String getMessage() {
            if (params.isEmpty()) {
                return messageTemplate;
            }
            return String.format(messageTemplate, params.toArray());
        }

        public BusinessException build() {
            return new BusinessException(this);
        }
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

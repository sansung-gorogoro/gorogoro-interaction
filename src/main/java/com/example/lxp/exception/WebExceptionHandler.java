package com.example.lxp.exception;

import com.example.lxp.exception.ErrorResponse.FieldError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    // Domain Business Exception ----------

    /**
     * 비즈니스 규칙 위반 시 도메인에서 발생시키는 {@link BusinessException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getCode();
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), errorCode.getMessage(), e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // Request Validation Exceptions ----------

    /**
     * {@code @RequestBody} Bean Validation(@Valid) 실패로 {@link MethodArgumentNotValidException}이 발생했을 때 호출된다.
     * 주로 필드별 오류 메시지를 묶어 400 응답을 내려줄 때 사용한다.
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return createValidationErrorResponse(e, this::getFieldErrors);
    }

    /**
     * {@code @ModelAttribute} 바인딩/검증 실패 시 발생하는 {@link BindException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        return createValidationErrorResponse(e, this::getFieldErrors);
    }

    /**
     * {@code @RequestParam/@PathVariable} 등에 {@code @Validated}를 적용했을 때 제약조건 위반이 일어나면 발생한다.
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return createValidationErrorResponse(e, this::getFieldErrors);
    }

    /**
     * 경로/쿼리 파라미터 타입 변환에 실패했을 때 발생하는 {@link MethodArgumentTypeMismatchException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return createValidationErrorResponse(e, this::getFieldErrors);
    }

    /**
     * 필수 {@code @RequestParam} 이 누락되었을 때 발생하는 {@link MissingServletRequestParameterException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        final WebErrorCode errorCode = WebErrorCode.PARAMETER_MISSING;
        final String message = String.format("%s, 이름: %s, 타입: %s", errorCode.getMessage(), e.getParameterName(), e.getParameterType());
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 필수 경로 변수{@code @PathVariable}가 누락되었을 때 발생하는 {@link MissingPathVariableException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException(MissingPathVariableException e) {
        final WebErrorCode errorCode = WebErrorCode.PATH_VARIABLE_MISSING;
        final String message = String.format("%s, 이름: %s", errorCode.getMessage(), e.getVariableName());
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // HTTP Exceptions ----------

    /**
     * 요청 본문을 읽거나 JSON 역직렬화에 실패했을 때 발생하는 {@link HttpMessageNotReadableException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return createHttpMessageErrorResponse(WebErrorCode.MESSAGE_NOT_READABLE, e);
    }

    /**
     * 응답 본문 직렬화에 실패했을 때 발생하는 {@link HttpMessageNotWritableException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotWritableException(HttpMessageNotWritableException e) {
        return createHttpMessageErrorResponse(WebErrorCode.MESSAGE_NOT_WRITABLE, e);
    }

    /**
     * 지원하지 않는 HTTP 메서드로 요청이 들어왔을 때 발생하는 {@link HttpRequestMethodNotSupportedException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        final WebErrorCode errorCode = WebErrorCode.METHOD_NOT_SUPPORTED;
        final String message = String.format("%s, 지원: %s, 요청: %s", errorCode.getMessage(), e.getSupportedHttpMethods(), e.getMethod());
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 지원하지 않는 Content-Type 으로 요청이 들어왔을 때 발생하는 {@link HttpMediaTypeNotSupportedException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        final WebErrorCode errorCode = WebErrorCode.MEDIA_TYPE_NOT_SUPPORTED;
        final String message = String.format("%s, 지원: %s, 요청: %s", errorCode.getMessage(), e.getSupportedMediaTypes(), e.getContentType());
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 매핑된 핸들러를 찾지 못했을 때(404를 예외로 던지도록 설정한 경우) 발생하는 {@link NoHandlerFoundException}을 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        final WebErrorCode errorCode = WebErrorCode.HANDLER_NOT_FOUND;
        final String message = String.format("%s, 경로: %s, 메서드: %s", errorCode.getMessage(), e.getRequestURL(), e.getHttpMethod());
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // Data Access Exception ----------

    /**
     * 데이터베이스 접근 중 발생하는 {@link DataAccessException} 계열 예외를 처리한다.
     */
    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        final WebErrorCode errorCode = WebErrorCode.DATA_ACCESS_ERROR;
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
        log.debug("{}({}): ", errorCode.getCode(), errorCode.getName(), e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // Default Fallback Exception ----------

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        final WebErrorCode errorCode = WebErrorCode.INTERNAL_ERROR;
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
        log.error("{}({}): ", errorCode.getCode(), errorCode.getName(), e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // Common Logic ----------

    private <E extends Exception> ResponseEntity<ErrorResponse> createValidationErrorResponse(
            E e,
            Function<? super E, List<FieldError>> extractor
    ) {
        final WebErrorCode errorCode = WebErrorCode.VALIDATION_FAILED;
        List<FieldError> fieldErrors = extractor.apply(e);
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
        log.debug("{}({}): {}, \n{}", errorCode.getCode(), errorCode.getName(), errorCode.getMessage(), fieldErrors, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    private ResponseEntity<ErrorResponse> createHttpMessageErrorResponse(
            WebErrorCode errorCode,
            HttpMessageConversionException e
    ) {
        final String cause = e.getMostSpecificCause().getMessage();
        final String message = String.format("%s, 원인: %s", errorCode.getMessage(), cause == null ? "알 수 없음" : cause);
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), message);
        log.warn("{}({}): {}", errorCode.getCode(), errorCode.getName(), message, e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // Helpers ----------

    private List<FieldError> getFieldErrors(BindException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(violation -> new FieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(HandlerMethodValidationException e) {
        return e.getParameterValidationResults().stream()
                .map(result -> new FieldError(
                        result.getMethodParameter().getParameterName(),
                        result.getResolvableErrors()
                                .stream()
                                .map(MessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", ")),
                        result.getArgument() == null ? null : result.getArgument().toString()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(MethodArgumentTypeMismatchException e) {
        return List.of(
                new FieldError(
                        e.getName(),
                        "expected type: " + (e.getRequiredType() == null ? null : e.getRequiredType().getSimpleName()),
                        e.getValue() == null ? null : e.getValue().toString()
                )
        );
    }

}

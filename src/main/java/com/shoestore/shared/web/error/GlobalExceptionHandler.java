package com.shoestore.shared.web.error;

import com.shoestore.shared.application.error.ApplicationException;
import com.shoestore.shared.application.error.CommonErrorCode;
import com.shoestore.shared.application.error.ErrorCode;
import com.shoestore.shared.web.correlation.RequestCorrelation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Translates application and web exceptions into the public HTTP error
 * response contract.
 *
 * <p>Exception details are logged server-side but are not exposed to API
 * clients.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String VALIDATION_FAILED_MESSAGE =
            "Request validation failed.";

    private static final String MALFORMED_BODY_MESSAGE =
            "Request body is malformed or unreadable.";

    private static final String INVALID_PARAMETER_MESSAGE =
            "A request parameter is missing or invalid.";

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = Objects.requireNonNull(
                clock,
                "clock must not be null"
        );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = resolveStatus(exception.getErrorCode());

        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        if (status.is5xxServerError()) {
            LOGGER.error(
                    "Application failure. correlationId={}, errorCode={}",
                    correlationId,
                    exception.getErrorCode().code(),
                    exception
            );
        }

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                status.value(),
                exception.getErrorCode(),
                safeApplicationMessage(exception, status),
                request.getRequestURI(),
                correlationId,
                List.of()
        );

        return response(status, correlationId, response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        List<FieldViolation> violations =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> new FieldViolation(
                                fieldError.getField(),
                                safeValidationMessage(
                                        fieldError.getDefaultMessage()
                                )
                        ))
                        .sorted(Comparator.comparing(
                                FieldViolation::field
                        ))
                        .toList();

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                HttpStatus.BAD_REQUEST.value(),
                CommonErrorCode.INVALID_REQUEST,
                VALIDATION_FAILED_MESSAGE,
                request.getRequestURI(),
                correlationId,
                violations
        );

        return response(
                HttpStatus.BAD_REQUEST,
                correlationId,
                response
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                HttpStatus.BAD_REQUEST.value(),
                CommonErrorCode.INVALID_REQUEST,
                VALIDATION_FAILED_MESSAGE,
                request.getRequestURI(),
                correlationId,
                List.of()
        );

        return response(
                HttpStatus.BAD_REQUEST,
                correlationId,
                response
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                HttpStatus.BAD_REQUEST.value(),
                CommonErrorCode.INVALID_REQUEST,
                MALFORMED_BODY_MESSAGE,
                request.getRequestURI(),
                correlationId,
                List.of()
        );

        return response(
                HttpStatus.BAD_REQUEST,
                correlationId,
                response
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequestParameter(
            Exception exception,
            HttpServletRequest request
    ) {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                HttpStatus.BAD_REQUEST.value(),
                CommonErrorCode.INVALID_REQUEST,
                INVALID_PARAMETER_MESSAGE,
                request.getRequestURI(),
                correlationId,
                List.of()
        );

        return response(
                HttpStatus.BAD_REQUEST,
                correlationId,
                response
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        LOGGER.error(
                "Unexpected request failure. correlationId={}",
                correlationId,
                exception
        );

        ErrorResponse response = ErrorResponse.of(
                currentInstant(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                CommonErrorCode.INTERNAL_ERROR,
                request.getRequestURI(),
                correlationId
        );

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                correlationId,
                response
        );
    }

    private Instant currentInstant() {
        return clock.instant();
    }

    private static ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String correlationId,
            ErrorResponse body
    ) {
        return ResponseEntity
                .status(status)
                .header(
                        RequestCorrelation.HEADER_NAME,
                        correlationId
                )
                .body(body);
    }

    private static HttpStatus resolveStatus(ErrorCode errorCode) {
        if (errorCode == CommonErrorCode.INVALID_REQUEST) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static String safeApplicationMessage(
            ApplicationException exception,
            HttpStatus status
    ) {
        if (status.is5xxServerError()) {
            return CommonErrorCode.INTERNAL_ERROR.defaultMessage();
        }

        return exception.getMessage();
    }

    private static String safeValidationMessage(String message) {
        if (message == null || message.isBlank()) {
            return "is invalid";
        }

        return message;
    }
}

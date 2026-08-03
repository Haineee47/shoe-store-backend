package com.shoestore.shared.web.error;

import com.shoestore.shared.application.error.CommonErrorCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorResponseTest {

    private static final Instant TIMESTAMP =
            Instant.parse("2026-08-03T06:00:00Z");

    private static final String PATH = "/api/products";

    private static final String CORRELATION_ID =
            "6ae2d8b2-94fe-460b-ab14-b73209197542";

    @Test
    void shouldCreateErrorResponseUsingDefaultMessage() {
        ErrorResponse response = ErrorResponse.of(
                TIMESTAMP,
                400,
                CommonErrorCode.INVALID_REQUEST,
                PATH,
                CORRELATION_ID
        );

        assertThat(response.timestamp()).isEqualTo(TIMESTAMP);
        assertThat(response.status()).isEqualTo(400);

        assertThat(response.code())
                .isEqualTo("COMMON_INVALID_REQUEST");

        assertThat(response.message())
                .isEqualTo("The request is invalid.");

        assertThat(response.path()).isEqualTo(PATH);

        assertThat(response.correlationId())
                .isEqualTo(CORRELATION_ID);

        assertThat(response.violations()).isEmpty();
        assertThat(response.hasViolations()).isFalse();
    }

    @Test
    void shouldCreateErrorResponseUsingCustomMessage() {
        ErrorResponse response = ErrorResponse.of(
                TIMESTAMP,
                400,
                CommonErrorCode.INVALID_REQUEST,
                "Page size must not exceed 100.",
                PATH,
                CORRELATION_ID,
                List.of()
        );

        assertThat(response.code())
                .isEqualTo("COMMON_INVALID_REQUEST");

        assertThat(response.message())
                .isEqualTo("Page size must not exceed 100.");
    }

    @Test
    void shouldCreateErrorResponseWithFieldViolations() {
        List<FieldViolation> violations = List.of(
                new FieldViolation(
                        "email",
                        "must be a well-formed email address"
                ),
                new FieldViolation(
                        "name",
                        "must not be blank"
                )
        );

        ErrorResponse response = ErrorResponse.of(
                TIMESTAMP,
                400,
                CommonErrorCode.INVALID_REQUEST,
                "Request validation failed.",
                "/api/users",
                CORRELATION_ID,
                violations
        );

        assertThat(response.violations())
                .containsExactlyElementsOf(violations);

        assertThat(response.hasViolations()).isTrue();
    }

    @Test
    void shouldUseEmptyViolationsWhenNullIsProvided() {
        ErrorResponse response = new ErrorResponse(
                TIMESTAMP,
                500,
                "COMMON_INTERNAL_ERROR",
                "An unexpected error occurred.",
                PATH,
                CORRELATION_ID,
                null
        );

        assertThat(response.violations()).isEmpty();
        assertThat(response.hasViolations()).isFalse();
    }

    @Test
    void shouldDefensivelyCopyViolations() {
        List<FieldViolation> mutableViolations =
                new ArrayList<>();

        mutableViolations.add(
                new FieldViolation(
                        "email",
                        "must not be blank"
                )
        );

        ErrorResponse response = ErrorResponse.of(
                TIMESTAMP,
                400,
                CommonErrorCode.INVALID_REQUEST,
                "Request validation failed.",
                "/api/users",
                CORRELATION_ID,
                mutableViolations
        );

        mutableViolations.clear();

        assertThat(response.violations()).hasSize(1);

        assertThatThrownBy(() -> response.violations().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectSuccessStatus() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        200,
                        "COMMON_INVALID_REQUEST",
                        "The request is invalid.",
                        PATH,
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("status must be between 400 and 599");
    }

    @Test
    void shouldRejectStatusGreaterThan599() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        600,
                        "COMMON_INTERNAL_ERROR",
                        "An unexpected error occurred.",
                        PATH,
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("status must be between 400 and 599");
    }

    @Test
    void shouldRejectNullTimestamp() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ErrorResponse(
                        null,
                        500,
                        "COMMON_INTERNAL_ERROR",
                        "An unexpected error occurred.",
                        PATH,
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("timestamp must not be null");
    }

    @Test
    void shouldRejectBlankCode() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        500,
                        "   ",
                        "An unexpected error occurred.",
                        PATH,
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("code must not be blank");
    }

    @Test
    void shouldRejectBlankMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        500,
                        "COMMON_INTERNAL_ERROR",
                        "   ",
                        PATH,
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("message must not be blank");
    }

    @Test
    void shouldRejectPathWithoutLeadingSlash() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        404,
                        "RESOURCE_NOT_FOUND",
                        "The resource was not found.",
                        "api/products/1",
                        CORRELATION_ID,
                        List.of()
                ))
                .withMessage("path must start with '/'");
    }

    @Test
    void shouldRejectBlankCorrelationId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ErrorResponse(
                        TIMESTAMP,
                        500,
                        "COMMON_INTERNAL_ERROR",
                        "An unexpected error occurred.",
                        PATH,
                        "   ",
                        List.of()
                ))
                .withMessage("correlationId must not be blank");
    }

    @Test
    void shouldRejectNullErrorCodeInFactoryMethod() {
        assertThatNullPointerException()
                .isThrownBy(() -> ErrorResponse.of(
                        TIMESTAMP,
                        500,
                        null,
                        PATH,
                        CORRELATION_ID
                ))
                .withMessage("errorCode must not be null");
    }

    @Test
    void fieldViolationShouldRejectBlankField() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FieldViolation(
                        "   ",
                        "must not be blank"
                ))
                .withMessage("field must not be blank");
    }

    @Test
    void fieldViolationShouldRejectBlankMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FieldViolation(
                        "email",
                        "   "
                ))
                .withMessage("message must not be blank");
    }
}

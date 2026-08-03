package com.shoestore.shared.application.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ApplicationExceptionTest {

    @Test
    void shouldUseDefaultMessageFromErrorCode() {
        ApplicationException exception =
                new ApplicationException(CommonErrorCode.INVALID_REQUEST);

        assertThat(exception.getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        assertThat(exception.getMessage())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST.defaultMessage());

        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldUseCustomMessage() {
        ApplicationException exception = new ApplicationException(
                CommonErrorCode.INVALID_REQUEST,
                "Page size must not exceed 100."
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        assertThat(exception.getMessage())
                .isEqualTo("Page size must not exceed 100.");

        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldPreserveCauseWithDefaultMessage() {
        IllegalStateException cause =
                new IllegalStateException("Underlying failure");

        ApplicationException exception = new ApplicationException(
                CommonErrorCode.INTERNAL_ERROR,
                cause
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(CommonErrorCode.INTERNAL_ERROR);

        assertThat(exception.getMessage())
                .isEqualTo(CommonErrorCode.INTERNAL_ERROR.defaultMessage());

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void shouldPreserveCauseWithCustomMessage() {
        IllegalStateException cause =
                new IllegalStateException("Underlying failure");

        ApplicationException exception = new ApplicationException(
                CommonErrorCode.INTERNAL_ERROR,
                "Unable to complete the operation.",
                cause
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(CommonErrorCode.INTERNAL_ERROR);

        assertThat(exception.getMessage())
                .isEqualTo("Unable to complete the operation.");

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void shouldRejectNullErrorCode() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ApplicationException(null))
                .withMessage("errorCode must not be null");
    }

    @Test
    void shouldRejectNullMessage() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ApplicationException(
                        CommonErrorCode.INVALID_REQUEST,
                        (String) null
                ))
                .withMessage("message must not be null");
    }

    @Test
    void shouldRejectBlankMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ApplicationException(
                        CommonErrorCode.INVALID_REQUEST,
                        "   "
                ))
                .withMessage("message must not be blank");
    }

    @Test
    void commonErrorCodesShouldExposeStableValues() {
        assertThat(CommonErrorCode.INVALID_REQUEST.code())
                .isEqualTo("COMMON_INVALID_REQUEST");

        assertThat(CommonErrorCode.INVALID_REQUEST.defaultMessage())
                .isEqualTo("The request is invalid.");

        assertThat(CommonErrorCode.INTERNAL_ERROR.code())
                .isEqualTo("COMMON_INTERNAL_ERROR");

        assertThat(CommonErrorCode.INTERNAL_ERROR.defaultMessage())
                .isEqualTo("An unexpected error occurred.");
    }
}

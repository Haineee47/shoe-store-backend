package com.shoestore.shared.web.correlation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class RequestCorrelationTest {

    private static final String VALID_CORRELATION_ID =
            "6ae2d8b2-94fe-460b-ab14-b73209197542";

    @Test
    void shouldUseValidRequestAttributeFirst() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setAttribute(
                RequestCorrelation.REQUEST_ATTRIBUTE,
                VALID_CORRELATION_ID
        );

        request.addHeader(
                RequestCorrelation.HEADER_NAME,
                "c99819a6-bc36-41ed-b355-0f9346693028"
        );

        String result =
                RequestCorrelation.resolveOrCreate(request);

        assertThat(result)
                .isEqualTo(VALID_CORRELATION_ID);
    }

    @Test
    void shouldUseValidRequestHeaderWhenAttributeIsMissing() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                RequestCorrelation.HEADER_NAME,
                VALID_CORRELATION_ID
        );

        String result =
                RequestCorrelation.resolveOrCreate(request);

        assertThat(result)
                .isEqualTo(VALID_CORRELATION_ID);
    }

    @Test
    void shouldGenerateUuidWhenHeaderIsMissing() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        String result =
                RequestCorrelation.resolveOrCreate(request);

        assertThat(result).isNotBlank();

        assertThatNoExceptionWhenParsingUuid(result);
    }

    @Test
    void shouldGenerateUuidWhenHeaderIsInvalid() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                RequestCorrelation.HEADER_NAME,
                "not-a-valid-uuid"
        );

        String result =
                RequestCorrelation.resolveOrCreate(request);

        assertThat(result)
                .isNotEqualTo("not-a-valid-uuid");

        assertThatNoExceptionWhenParsingUuid(result);
    }

    @Test
    void shouldGenerateUuidWhenAttributeIsInvalid() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setAttribute(
                RequestCorrelation.REQUEST_ATTRIBUTE,
                "invalid-attribute"
        );

        String result =
                RequestCorrelation.resolveOrCreate(request);

        assertThat(result)
                .isNotEqualTo("invalid-attribute");

        assertThatNoExceptionWhenParsingUuid(result);
    }

    @Test
    void shouldRecognizeCanonicalUuid() {
        assertThat(RequestCorrelation.isValid(
                VALID_CORRELATION_ID
        )).isTrue();
    }

    @Test
    void shouldRecognizeUppercaseUuid() {
        assertThat(RequestCorrelation.isValid(
                VALID_CORRELATION_ID.toUpperCase()
        )).isTrue();
    }

    @Test
    void shouldRejectNullValue() {
        assertThat(RequestCorrelation.isValid(null))
                .isFalse();
    }

    @Test
    void shouldRejectBlankValue() {
        assertThat(RequestCorrelation.isValid("   "))
                .isFalse();
    }

    @Test
    void shouldRejectUuidWithSurroundingWhitespace() {
        assertThat(RequestCorrelation.isValid(
                " " + VALID_CORRELATION_ID + " "
        )).isFalse();
    }

    @Test
    void shouldRejectNonUuidValue() {
        assertThat(RequestCorrelation.isValid(
                "request-123"
        )).isFalse();
    }

    @Test
    void shouldRejectNullRequest() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        RequestCorrelation.resolveOrCreate(null)
                )
                .withMessage("request must not be null");
    }

    private static void assertThatNoExceptionWhenParsingUuid(
            String value
    ) {
        org.assertj.core.api.Assertions
                .assertThatNoException()
                .isThrownBy(() -> UUID.fromString(value));
    }
}

package com.shoestore.shared.web.correlation;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class RequestCorrelationFilterTest {

    private static final String VALID_CORRELATION_ID =
            "6ae2d8b2-94fe-460b-ab14-b73209197542";

    private final RequestCorrelationFilter filter =
            new RequestCorrelationFilter();

    @AfterEach
    void cleanMdc() {
        MDC.remove(RequestCorrelation.MDC_KEY);
    }

    @Test
    void shouldReuseValidIncomingCorrelationId()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                RequestCorrelation.HEADER_NAME,
                VALID_CORRELATION_ID
        );

        AtomicReference<String> mdcValueDuringChain =
                new AtomicReference<>();

        FilterChain filterChain = (currentRequest, currentResponse) ->
                mdcValueDuringChain.set(
                        MDC.get(RequestCorrelation.MDC_KEY)
                );

        filter.doFilter(request, response, filterChain);

        assertThat(request.getAttribute(
                RequestCorrelation.REQUEST_ATTRIBUTE
        )).isEqualTo(VALID_CORRELATION_ID);

        assertThat(response.getHeader(
                RequestCorrelation.HEADER_NAME
        )).isEqualTo(VALID_CORRELATION_ID);

        assertThat(mdcValueDuringChain.get())
                .isEqualTo(VALID_CORRELATION_ID);

        assertThat(MDC.get(RequestCorrelation.MDC_KEY))
                .isNull();
    }

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsMissing()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> attributeDuringChain =
                new AtomicReference<>();

        FilterChain filterChain = (currentRequest, currentResponse) ->
                attributeDuringChain.set(
                        (String) currentRequest.getAttribute(
                                RequestCorrelation.REQUEST_ATTRIBUTE
                        )
                );

        filter.doFilter(request, response, filterChain);

        String responseCorrelationId =
                response.getHeader(
                        RequestCorrelation.HEADER_NAME
                );

        assertThat(responseCorrelationId).isNotBlank();

        assertThatNoException()
                .isThrownBy(() ->
                        UUID.fromString(responseCorrelationId)
                );

        assertThat(attributeDuringChain.get())
                .isEqualTo(responseCorrelationId);

        assertThat(MDC.get(RequestCorrelation.MDC_KEY))
                .isNull();
    }

    @Test
    void shouldReplaceInvalidIncomingCorrelationId()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                RequestCorrelation.HEADER_NAME,
                "not-a-valid-uuid"
        );

        filter.doFilter(
                request,
                response,
                (currentRequest, currentResponse) -> {
                    // No operation required.
                }
        );

        String correlationId = response.getHeader(
                RequestCorrelation.HEADER_NAME
        );

        assertThat(correlationId)
                .isNotEqualTo("not-a-valid-uuid");

        assertThatNoException()
                .isThrownBy(() ->
                        UUID.fromString(correlationId)
                );
    }

    @Test
    void shouldExposeSameCorrelationIdEverywhere()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> requestAttribute =
                new AtomicReference<>();

        AtomicReference<String> mdcValue =
                new AtomicReference<>();

        FilterChain filterChain = (currentRequest, currentResponse) -> {
            requestAttribute.set(
                    (String) currentRequest.getAttribute(
                            RequestCorrelation.REQUEST_ATTRIBUTE
                    )
            );

            mdcValue.set(
                    MDC.get(RequestCorrelation.MDC_KEY)
            );
        };

        filter.doFilter(request, response, filterChain);

        String responseHeader = response.getHeader(
                RequestCorrelation.HEADER_NAME
        );

        assertThat(requestAttribute.get())
                .isEqualTo(responseHeader);

        assertThat(mdcValue.get())
                .isEqualTo(responseHeader);
    }

    @Test
    void shouldRemoveMdcWhenFilterChainThrowsException() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain failingChain =
                (currentRequest, currentResponse) -> {
                    assertThat(MDC.get(
                            RequestCorrelation.MDC_KEY
                    )).isNotNull();

                    throw new IllegalStateException(
                            "Simulated downstream failure"
                    );
                };

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() ->
                        filter.doFilter(
                                request,
                                response,
                                failingChain
                        )
                )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Simulated downstream failure");

        assertThat(MDC.get(RequestCorrelation.MDC_KEY))
                .isNull();
    }
}

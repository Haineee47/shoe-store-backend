package com.shoestore.shared.web.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestLoggingFilterTest {

    private static final String CORRELATION_ID =
            "6ae2d8b2-94fe-460b-ab14-b73209197542";

    private final HttpRequestLoggingFilter filter =
            new HttpRequestLoggingFilter();

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(
                HttpRequestLoggingFilter.class
        );

        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        MDC.put("correlationId", CORRELATION_ID);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        listAppender.stop();
        MDC.remove("correlationId");
    }

    @Test
    void shouldLogSuccessfulRequestMetadata()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/products"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (currentRequest, currentResponse) ->
                        ((MockHttpServletResponse) currentResponse)
                                .setStatus(200);

        filter.doFilter(request, response, filterChain);

        ILoggingEvent event = singleLoggingEvent();

        assertThat(event.getLevel()).isEqualTo(Level.INFO);

        assertThat(event.getFormattedMessage())
                .contains("HTTP request completed")
                .contains("method=GET")
                .contains("path=/api/products")
                .contains("status=200")
                .containsPattern("durationMs=\\d+");

        assertThat(event.getMDCPropertyMap())
                .containsEntry(
                        "correlationId",
                        CORRELATION_ID
                );
    }

    @Test
    void shouldNotLogQueryString()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/password-reset"
                );

        request.setQueryString(
                "token=sensitive-reset-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (currentRequest, currentResponse) -> {
                    // No operation required.
                }
        );

        String message =
                singleLoggingEvent().getFormattedMessage();

        assertThat(message)
                .contains("path=/api/password-reset")
                .doesNotContain("token")
                .doesNotContain("sensitive-reset-token")
                .doesNotContain("?");
    }

    @Test
    void shouldNotLogRequestHeaders()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/users/me"
                );

        request.addHeader(
                "Authorization",
                "Bearer sensitive-access-token"
        );

        request.addHeader(
                "Cookie",
                "refreshToken=sensitive-refresh-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (currentRequest, currentResponse) -> {
                    // No operation required.
                }
        );

        String message =
                singleLoggingEvent().getFormattedMessage();

        assertThat(message)
                .doesNotContain("Authorization")
                .doesNotContain("sensitive-access-token")
                .doesNotContain("Cookie")
                .doesNotContain("sensitive-refresh-token");
    }

    @Test
    void shouldLogClientErrorAtInfoLevel()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/products"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (currentRequest, currentResponse) ->
                        ((MockHttpServletResponse) currentResponse)
                                .setStatus(400);

        filter.doFilter(request, response, filterChain);

        ILoggingEvent event = singleLoggingEvent();

        assertThat(event.getLevel()).isEqualTo(Level.INFO);

        assertThat(event.getFormattedMessage())
                .contains("method=POST")
                .contains("status=400");
    }

    @Test
    void shouldLogServerErrorAtWarnLevel()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/failure"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (currentRequest, currentResponse) ->
                        ((MockHttpServletResponse) currentResponse)
                                .setStatus(500);

        filter.doFilter(request, response, filterChain);

        ILoggingEvent event = singleLoggingEvent();

        assertThat(event.getLevel()).isEqualTo(Level.WARN);

        assertThat(event.getFormattedMessage())
                .contains("status=500");
    }

    @Test
    void shouldLogFiveHundredWhenDownstreamThrows()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/unexpected"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain failingChain =
                (currentRequest, currentResponse) -> {
                    throw new IllegalStateException(
                            "Sensitive implementation detail"
                    );
                };

        assertThatThrownBy(() ->
                filter.doFilter(
                        request,
                        response,
                        failingChain
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Sensitive implementation detail"
                );

        ILoggingEvent event = singleLoggingEvent();

        assertThat(event.getLevel()).isEqualTo(Level.WARN);

        assertThat(event.getFormattedMessage())
                .contains("status=500")
                .doesNotContain(
                        "Sensitive implementation detail"
                );
    }

    @Test
    void shouldPreserveExistingResponseStatus()
            throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "DELETE",
                        "/api/products/product-001"
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (currentRequest, currentResponse) ->
                        ((MockHttpServletResponse) currentResponse)
                                .setStatus(204);

        filter.doFilter(request, response, filterChain);

        assertThat(singleLoggingEvent().getFormattedMessage())
                .contains("method=DELETE")
                .contains(
                        "path=/api/products/product-001"
                )
                .contains("status=204");
    }

    private ILoggingEvent singleLoggingEvent() {
        List<ILoggingEvent> events =
                listAppender.list;

        assertThat(events).hasSize(1);

        return events.getFirst();
    }
}

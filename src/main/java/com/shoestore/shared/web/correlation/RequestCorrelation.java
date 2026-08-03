package com.shoestore.shared.web.correlation;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.util.UUID;

/**
 * Defines the HTTP request correlation contract.
 *
 * <p>This class centralizes the correlation header, request attribute,
 * MDC key and identifier validation rules so that filters and exception
 * handlers use the same convention.</p>
 */
public final class RequestCorrelation {

    public static final String HEADER_NAME = "X-Correlation-ID";

    public static final String REQUEST_ATTRIBUTE =
            RequestCorrelation.class.getName() + ".correlationId";

    public static final String MDC_KEY = "correlationId";

    private RequestCorrelation() {
        throw new UnsupportedOperationException(
                "RequestCorrelation must not be instantiated"
        );
    }

    /**
     * Resolves the current correlation identifier.
     *
     * <p>The request attribute populated by the correlation filter takes
     * precedence. If the filter has not run, a valid request header is used.
     * Otherwise, a new UUID is generated.</p>
     *
     * @param request current HTTP request
     * @return a valid correlation UUID
     */
    public static String resolveOrCreate(
            HttpServletRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        Object attribute = request.getAttribute(
                REQUEST_ATTRIBUTE
        );

        if (attribute instanceof String correlationId
                && isValid(correlationId)) {
            return correlationId;
        }

        String headerValue = request.getHeader(HEADER_NAME);

        if (isValid(headerValue)) {
            return headerValue;
        }

        return UUID.randomUUID().toString();
    }

    /**
     * Tests whether a value is a canonical UUID representation.
     *
     * @param value candidate identifier
     * @return true when the value is a valid canonical UUID
     */
    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            return UUID.fromString(value)
                    .toString()
                    .equalsIgnoreCase(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}

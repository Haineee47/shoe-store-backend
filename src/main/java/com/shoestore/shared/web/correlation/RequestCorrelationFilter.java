package com.shoestore.shared.web.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Establishes one correlation identifier for each HTTP request.
 *
 * <p>The identifier is exposed through the response header, request
 * attribute and SLF4J MDC for the lifetime of the filter chain.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId =
                RequestCorrelation.resolveOrCreate(request);

        request.setAttribute(
                RequestCorrelation.REQUEST_ATTRIBUTE,
                correlationId
        );

        response.setHeader(
                RequestCorrelation.HEADER_NAME,
                correlationId
        );

        MDC.put(
                RequestCorrelation.MDC_KEY,
                correlationId
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(RequestCorrelation.MDC_KEY);
        }
    }
}

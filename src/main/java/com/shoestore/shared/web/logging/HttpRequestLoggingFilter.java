package com.shoestore.shared.web.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Logs safe HTTP request completion metadata.
 *
 * <p>This filter intentionally excludes request and response bodies,
 * query parameters, headers, cookies, credentials and other potentially
 * sensitive values.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    private static final int MINIMUM_SERVER_ERROR_STATUS = 500;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startNanos = System.nanoTime();
        boolean downstreamFailure = false;

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            downstreamFailure = true;
            throw exception;
        } finally {
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startNanos
            );

            int status = resolveStatus(
                    response,
                    downstreamFailure
            );

            logRequestCompletion(
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    durationMillis
            );
        }
    }

    private static int resolveStatus(
            HttpServletResponse response,
            boolean downstreamFailure
    ) {
        int responseStatus = response.getStatus();

        /*
         * An exception thrown before an exception resolver or error dispatcher
         * sets the response status may leave the default status at 200.
         * The log must not describe that failed request as successful.
         */
        if (downstreamFailure
                && responseStatus < MINIMUM_SERVER_ERROR_STATUS) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        return responseStatus;
    }

    private static void logRequestCompletion(
            String method,
            String path,
            int status,
            long durationMillis
    ) {
        if (status >= MINIMUM_SERVER_ERROR_STATUS) {
            LOGGER.warn(
                    "HTTP request completed method={} path={} "
                            + "status={} durationMs={}",
                    method,
                    path,
                    status,
                    durationMillis
            );

            return;
        }

        LOGGER.info(
                "HTTP request completed method={} path={} "
                        + "status={} durationMs={}",
                method,
                path,
                status,
                durationMillis
        );
    }
}

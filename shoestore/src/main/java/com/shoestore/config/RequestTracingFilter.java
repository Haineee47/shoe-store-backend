package com.shoestore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Chạy đầu tiên để bắt trọn gói Request
public class RequestTracingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Chỉ sinh và lưu Duy nhất vào MDC
        String requestId = UUID.randomUUID().toString().substring(0, 8); // Rút ngắn còn 8 ký tự cho gọn log
        MDC.put(REQUEST_ID, requestId);

        // 2. Trả về Header cho Client tracking
        response.setHeader("X-Request-ID", requestId);

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        try {
            // Log START gọn gàng trên 1 dòng
            log.info("► START | ID: [{}] | Method: {} | URI: {} | IP: {}", requestId, method, uri, ip);

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            // Log END gọn gàng trên 1 dòng
            log.info("◄ END   | ID: [{}] | Status: {} | Duration: {} ms", requestId, response.getStatus(), duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            // Log FAILED gọn gàng
            log.error("✖ FAILED| ID: [{}] | Duration: {} ms | Error: {}", requestId, duration, e.getMessage());
            throw e;
        } finally {
            // 3. Giải phóng hoàn toàn MDC tránh leak thread pool
            MDC.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
                path.startsWith("/static") ||
                path.startsWith("/webjars") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
package com.shoestore.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shoestore.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // 1. Set HTTP Status là 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. Định dạng kiểu trả về là JSON kèm mã hóa UTF-8 chống lỗi font chữ tiếng Việt
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 3. Sử dụng đúng cấu trúc ApiResponse lỗi của bạn
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "UNAUTHORIZED",
                "Xác thực không thành công. Token không hợp lệ hoặc đã hết hạn."
        );

        // 4. Khởi tạo và cấu hình ObjectMapper để đồng bộ định dạng thời gian dạng Chuỗi String
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // CẤU HÌNH QUAN TRỌNG: Tắt tính năng ghi ngày tháng dạng mảng số (Xử lý dứt điểm lỗi [2026,6,5...])
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 5. Ghi dữ liệu chuỗi JSON chuẩn hóa ra response gửi về Postman
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
    }
}
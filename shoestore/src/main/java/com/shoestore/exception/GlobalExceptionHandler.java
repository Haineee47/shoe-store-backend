package com.shoestore.exception;

import com.shoestore.common.response.ApiResponse;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý BusinessException (Lỗi nghiệp vụ đã định nghĩa)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        // Lấy ErrorCode từ exception (Giả sử BusinessException của bạn có field ErrorCode)
        String code = ex.getErrorCode() != null ? ex.getErrorCode().name() : "BUSINESS_ERROR";

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(code, ex.getMessage()));
    }

    // 2. Xử lý Validation Exception (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        // Duyệt qua tất cả các trường bị lỗi và gom vào Map
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Gọi hàm error 3 tham số vừa tạo ở trên
        ApiResponse<Object> apiResponse = ApiResponse.error("VALIDATION_ERROR", "Validation failed", errors);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 3. Xử lý lỗi hệ thống không mong muốn
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        // Log lỗi ở đây nếu cần: log.error("Internal Server Error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        // Định nghĩa cấu hình response giống hệt với format chung của dự án bạn
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("code", "INVALID_ARGUMENT_TYPE");
        body.put("message", String.format("Tham số '%s' nhận giá trị '%s' không đúng kiểu dữ liệu yêu cầu (Kỳ vọng: %s)",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        body.put("data", null);
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST); // Trả về 400 Bad Request thay vì 500
    }
}
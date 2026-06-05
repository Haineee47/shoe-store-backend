package com.shoestore.exception;

import com.shoestore.common.response.ApiResponse;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
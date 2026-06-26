package com.shoestore.exception;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private static final String VERIFY_EMAIL_PATH = "/api/v1/auth/verify-email";

    /**
     * Xử lý lỗi nghiệp vụ hệ thống (BusinessException).
     * 🧠 KIẾN TRÚC TỐI ƯU: Nhận diện ngữ cảnh Request (Presentation Concern).
     * Nếu lỗi xảy ra tại luồng xác thực email qua trình duyệt -> Ép điều hướng (Redirect 303).
     * Nếu lỗi xảy ra tại các luồng gọi API thông thường -> Trả dữ liệu cấu trúc JSON (ApiResponse).
     */
    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        String codeName = errorCode != null ? errorCode.name() : "BUSINESS_ERROR";

        // Nhánh rẽ 1: Nếu lỗi xuất phát từ luồng Xác thực Email hiển thị trên trình duyệt
        if (request.getRequestURI().contains(VERIFY_EMAIL_PATH)) {
            log.warn("Xác thực email thất bại. Đang xử lý chuyển hướng Web Layer cho lỗi: {}", codeName);
            String targetRedirect = String.format("%s/login?verified=false&reason=%s", frontendUrl, codeName.toLowerCase());

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create(targetRedirect))
                    .build();
        }

        // Nhánh rẽ 2: Các API xử lý dữ liệu bất đồng bộ truyền thống
        HttpStatus status = errorCode != null ? errorCode.getHttpStatus() : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponse.error(codeName, ex.getMessage()));
    }

    /**
     * Xử lý lỗi ràng buộc dữ liệu đầu vào (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_ERROR", "Dữ liệu đầu vào không hợp lệ", errors));
    }

    /**
     * Xử lý lỗi sai lệch kiểu dữ liệu tham số trên URL (Type Mismatch).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String detailMessage = String.format("Tham số '%s' nhận giá trị '%s' không đúng kiểu dữ liệu yêu cầu (Kỳ vọng: %s)",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_ARGUMENT_TYPE", detailMessage));
    }

    /**
     * Điểm chặn cuối cùng cho các lỗi hệ thống không xác định (HTTP 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        log.error("[CRITICAL SYSTEM ERROR] Phát hiện lỗi hệ thống nghiêm trọng chưa được phân loại: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "Đã có lỗi hệ thống xảy ra, vui lòng thử lại sau."));
    }
}
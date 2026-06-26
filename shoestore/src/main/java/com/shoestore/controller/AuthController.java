package com.shoestore.controller;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.authRequest.*;
import com.shoestore.dto.response.authResponse.AuthResponse;
import com.shoestore.dto.response.authResponse.UserResponse;
import com.shoestore.exception.BusinessException;
import com.shoestore.service.AuthService;
import com.shoestore.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // =========================================================================
    // 🌐 1. ENDPOINTS CÔNG KHAI - KHÔNG CẦN ĐĂNG NHẬP (Public permitAll)
    // =========================================================================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        AuthResponse response = authService.googleLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Google login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Register successful", response));
    }

    /**
     * Xác thực email người dùng thông qua mã kích hoạt.
     * Service chỉ xử lý nghiệp vụ thuần túy. Quyết định điều hướng (Redirect)
     * khi thành công hoặc thất bại hoàn toàn được cô lập tại tầng Web.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyEmail(@RequestParam String token) {
        try {
            // Gọi service xử lý kích hoạt
            emailVerificationService.verifyEmail(token);

            // 🟢 NẾU THÀNH CÔNG: Trả về JSON thông báo rõ ràng cho Postman
            return ResponseEntity.ok(
                    ApiResponse.<Map<String, String>>success("Xác thực email thành công! Tài khoản của bạn đã được kích hoạt.", null)
            );

        } catch (BusinessException e) {
            // 🔴 NẾU THẤT BẠI DO TOKEN HẾT HẠN: Trả về kèm thông tin email để tiện cho việc bấm nút "Resend"
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {

                // Lấy email của User sở hữu token này để Frontend/Postman có thể dùng gọi API Resend
                String email = emailVerificationService.getEmailByToken(token);
                Map<String, String> data = Map.of(
                        "email", email,
                        "actionRequired", "RESEND_ALLOWED"
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.<Map<String, String>>error(
                                "TOKEN_EXPIRED",
                                "Mã xác thực đã hết hạn! Vui lòng bấm gửi lại mã mới.",
                                data
                        ));
            }

            // 🔴 NẾU THẤT BẠI DO CÁC LỖI KHÁC (Token sai, token đã dùng...)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Map<String, String>>error(
                            e.getErrorCode().name(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Map<String, String>>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        String token = emailVerificationService.resendVerificationEmail(request.getEmail());
        Map<String, String> data = Map.of("token", token);
        return ResponseEntity.ok(ApiResponse.success("Verification email sent", data));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Link đặt lại mật khẩu đã được gửi vào Email của bạn.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mật khẩu đã được cập nhật thành công.", null));
    }

    // =========================================================================
    // 🔐 2. ENDPOINTS BẢO MẬT - BẮT BUỘC ĐĂNG NHẬP (Private authenticated)
    // =========================================================================

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công.", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
package com.shoestore.dto.request.authRequest;

import com.shoestore.validator.password.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @PasswordMatch
public class ResetPasswordRequest {

    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "Mật khẩu mới phải từ 8-50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt"
    )
    private String newPassword;

    @NotBlank(message = "Nhập lại mật khẩu không được để trống")
    private String confirmPassword;
}
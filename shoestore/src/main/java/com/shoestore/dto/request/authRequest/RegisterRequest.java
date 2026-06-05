package com.shoestore.dto.request.authRequest;

import com.shoestore.validator.password.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatch // 🌟 Thêm Annotation Custom để check khớp mật khẩu 2 lần
public class RegisterRequest {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    // 🌟 Thêm Regex chuẩn bảo mật mạnh cho hệ thống E-commerce
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "Mật khẩu phải từ 8-50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt"
    )
    private String password;

    @NotBlank(message = "Nhập lại mật khẩu không được để trống")
    private String confirmPassword; // 🌟 Thêm trường nhập lại mật khẩu

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9,10}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
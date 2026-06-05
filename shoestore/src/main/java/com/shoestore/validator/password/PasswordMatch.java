package com.shoestore.validator.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Áp dụng ở cấp Class (DTO)
@Retention(RetentionPolicy.RUNTIME)
// 🌟 ĐĂNG KÝ CẢ 2 LỚP XỬ LÝ VÀO ĐÂY (Sẽ giải quyết lỗi "is never used")
@Constraint(validatedBy = {RegisterPasswordValidator.class, ResetPasswordValidator.class})
public @interface PasswordMatch {
    String message() default "Mật khẩu nhập lại không khớp";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
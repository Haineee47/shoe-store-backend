package com.shoestore.validator.password;

import com.shoestore.dto.request.authRequest.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegisterPasswordValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            return false;
        }

        boolean isValid = request.getPassword().equals(request.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            // 🌟 SỬA LỖI: Truyền thẳng chuỗi thông báo vào hàm (Không dùng getDefaultConstraintViolationTemplate)
            context.buildConstraintViolationWithTemplate("Mật khẩu nhập lại không khớp")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
package com.shoestore.validator.password;

import com.shoestore.dto.request.authRequest.ResetPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ResetPasswordValidator implements ConstraintValidator<PasswordMatch, ResetPasswordRequest> {

    @Override
    public boolean isValid(ResetPasswordRequest request, ConstraintValidatorContext context) {
        if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
            return false;
        }

        // So khớp mật khẩu mới (getNewPassword) với mật khẩu nhập lại (getConfirmPassword)
        boolean isValid = request.getNewPassword().equals(request.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            // 🌟 SỬA LỖI: Truyền thẳng chuỗi thông báo vào hàm
            context.buildConstraintViolationWithTemplate("Mật khẩu nhập lại không khớp")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
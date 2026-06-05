package com.shoestore.util;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.exception.BusinessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    // Chặn việc khởi tạo class này bên ngoài bằng từ khóa "new"
    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Lấy Email của người dùng hiện tại đang đăng nhập hệ thống (Dùng cho luồng Đổi pass, Đặt hàng, Giỏ hàng...)
     * @return String email người dùng
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return authentication.getName();
    }
}
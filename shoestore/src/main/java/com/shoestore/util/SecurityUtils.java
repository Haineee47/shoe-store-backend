package com.shoestore.util;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.exception.BusinessException;
import com.shoestore.security.user.UserPrincipal; // 🌟 BỔ SUNG: Import Principal tự định nghĩa của bạn
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    /**
     * 🌟 BỔ SUNG: Lấy ID của người dùng hiện tại (Admin / Staff) đang thao tác hệ thống.
     * Phục vụ đắc lực cho luồng ghi nhận thẻ kho, người tạo sản phẩm.
     * @return Long userId
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra phiên đăng nhập hợp lệ và tránh khách vãng lai
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        try {
            // Ép kiểu Principal về cấu trúc UserPrincipal của bạn để lấy ID mượt mà
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        } catch (ClassCastException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
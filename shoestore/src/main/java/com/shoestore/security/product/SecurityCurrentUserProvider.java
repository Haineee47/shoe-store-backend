package com.shoestore.security.product;

import com.shoestore.security.user.UserPrincipal; // 🌟 1. Import chính xác class Principal của bạn
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra an toàn hệ thống nếu Session chưa được thiết lập hoặc chạy ngầm
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return 0L; // 0L đại diện cho luồng tự động từ Hệ thống (System)
        }

        // 🌟 2. Ép kiểu về đúng class UserPrincipal của bạn và chấm lấy Id thông qua hàm đã thêm ở Bước 1
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }

        return 0L;
    }
}
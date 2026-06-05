package com.shoestore.security.user;

import com.shoestore.common.enums.user.PermissionName;
import com.shoestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate; // Dùng StringRedisTemplate để tối ưu
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("ss")
@RequiredArgsConstructor
@Slf4j
public class CustomSecurityExpression {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate; // Tiêm bộ đệm Redis

    public boolean hasPermission(String permissionStr) {
        // 1. Lấy thông tin phiên đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        try {
            // 2. Parse chuỗi truyền từ Controller thành Enum
            PermissionName permissionName = PermissionName.valueOf(permissionStr);

            // 3. Lấy userId từ UserPrincipal hiện tại
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Long userId = userPrincipal.getUser().getId();

            // 4. Định nghĩa cấu trúc Key trên Redis (Ví dụ: user:auth:perm:1:CATEGORY_CREATE)
            String redisKey = "user:auth:perm:" + userId + ":" + permissionStr;

            // Kịch bản A: Kiểm tra xem kết quả quyền này đã được lưu trên Redis chưa
            String cachedResult = redisTemplate.opsForValue().get(redisKey);
            if (cachedResult != null) {
                return Boolean.parseBoolean(cachedResult); // Trả về kết quả ngay lập tức (<1ms)
            }

            // Kịch bản B: Nếu Cache chưa có, truy vấn trực tiếp vào DB bằng câu lệnh tối ưu
            boolean hasPermission = userRepository.hasPermission(userId, permissionName);

            // 5. Lưu kết quả vừa tìm được vào Redis với thời gian hết hạn (TTL) là 10 phút
            redisTemplate.opsForValue().set(redisKey, String.valueOf(hasPermission), 10, TimeUnit.MINUTES);

            return hasPermission;

        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Lỗi phân quyền hệ thống: Cấu hình sai chuỗi Permission hoặc sai kiểu Principal", e);
            return false;
        }
    }
}
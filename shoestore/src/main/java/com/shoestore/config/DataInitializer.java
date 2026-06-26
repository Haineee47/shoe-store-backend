package com.shoestore.config;

import com.shoestore.common.enums.user.AuthProvider;
import com.shoestore.common.enums.user.RoleName;
import com.shoestore.entity.Role;
import com.shoestore.entity.User;
import com.shoestore.repository.RoleRepository;
import com.shoestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    // 🌟 Đã cập nhật: Sử dụng @NonNull của JSpecify để xóa sạch cảnh báo "deprecated" lẫn "@NullMarked"
    public void run(@NonNull String... args) {

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_ADMIN);
                    return roleRepository.save(newRole);
                });

        String adminEmail = "admin@shoestore.com";

        if (!userRepository.existsByEmail(adminEmail)) {

            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123")) // Mật khẩu đăng nhập: Admin@123
                    .fullName("Admin01")
                    .emailVerified(true)
                    .provider(com.shoestore.common.enums.user.AuthProvider.LOCAL) // Tên provider cũ của bạn
                    .status(com.shoestore.common.enums.user.UserStatus.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
            log.info("🌟 [DataInitializer] Tài khoản Super Admin mặc định đã được tạo: {} / Admin@123", adminEmail);
        }
    }
}
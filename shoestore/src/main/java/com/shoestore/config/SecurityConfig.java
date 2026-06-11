package com.shoestore.config;

import com.shoestore.security.jwt.JwtAuthenticationEntryPoint;
import com.shoestore.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // ===================================================================
                        // 🌐 1. PUBLIC API - KHÔNG CẦN ĐĂNG NHẬP (permitAll)
                        // ===================================================================
                        // Nhóm API xác thực công khai
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/verify-email",
                                "/api/v1/auth/google-login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/resend-verification"
                        ).permitAll()

                        // Nhóm API tài nguyên hiển thị phía Client (Khách vãng lai xem được)
                        .requestMatchers(
                                "/api/v1/categories/**",
                                "/api/v1/brands/**",       // 🌟 MỚI: Cho phép xem thương hiệu và chi tiết thương hiệu theo slug công khai
                                "/api/v1/admin/products/**"   // 🌟 THÊM DÒNG NÀY: Bảo vệ toàn bộ endpoint quản trị sản phẩm
                        ).permitAll()

                        // Nhóm API tài liệu kỹ thuật & Hệ thống
                        .requestMatchers(
                                "/error",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ===================================================================
                        // 🔐 2. PRIVATE API - BẮT BUỘC PHẢI ĐĂNG NHẬP (authenticated)
                        // ===================================================================
                        // Nhóm API cá nhân (User phải đăng nhập mới thực hiện được)
                        .requestMatchers(
                                "/api/v1/auth/change-password",
                                "/api/v1/auth/me",
                                "/api/v1/auth/logout"
                        ).authenticated()

                        // Nhóm API quản trị (Chỉ mở cửa cho Admin/Staff đi qua bộ lọc Filter, quyền chi tiết check tại Controller)
                        .requestMatchers(
                                "/api/v1/admin/categories/**",
                                "/api/v1/admin/brands/**",    // 🌟 MỚI: Bảo vệ toàn bộ endpoint quản trị thương hiệu
                                "/api/v1/admin/media/**"     // 🌟 MỚI: Bảo vệ toàn bộ endpoint upload/delete ảnh vật lý
                        ).authenticated()

                        // Tất cả các request còn lại chưa được chỉ định cấu hình đều phải login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
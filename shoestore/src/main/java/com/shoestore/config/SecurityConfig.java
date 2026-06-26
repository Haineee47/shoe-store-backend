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
@EnableMethodSecurity // Kích hoạt kiểm tra quyền mức method (@PreAuthorize) tại Controller
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
                        // 🌐 1. PUBLIC ENDPOINTS - KHÔNG CẦN ĐĂNG NHẬP (permitAll)
                        // ===================================================================
                        // Nhóm API xác thực và tài khoản công khai
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

                        // Nhóm API hiển thị phía Client (Xem danh mục, thương hiệu công khai)
                        .requestMatchers(
                                "/api/v1/categories/**",
                                "/api/v1/brands/**"
                        ).permitAll()

                        // Nhóm API tài liệu hệ thống & Swagger UI
                        .requestMatchers(
                                "/error",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ===================================================================
                        // 🔐 2. PRIVATE ENDPOINTS - BẮT BUỘC PHẢI ĐĂNG NHẬP (authenticated)
                        // ===================================================================
                        // Nhóm API cá nhân của người dùng (User/Admin/Staff)
                        .requestMatchers(
                                "/api/v1/auth/change-password",
                                "/api/v1/auth/me",
                                "/api/v1/auth/logout"
                        ).authenticated()

                        // 🛡️ NHÓM API QUẢN TRỊ ADMIN - BẢO VỆ CHẶT CHẼ QUA BỘ LỌC JWT FILTER
                        // (Mọi request vào phân vùng này bắt buộc có Token hợp lệ, quyền hạn chi tiết check ở Controller)
                        .requestMatchers(
                                "/api/v1/admin/categories/**",
                                "/api/v1/admin/brands/**",
                                "/api/v1/admin/products/**",
                                "/api/v1/admin/media/**",
                                "/api/v1/admin/inventory/**"
                        ).authenticated()

                        // Tất cả các request phát sinh ngoài luồng cấu hình trên đều bắt buộc phải login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
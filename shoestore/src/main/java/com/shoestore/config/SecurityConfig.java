package com.shoestore.config;

import com.shoestore.security.jwt.JwtAuthenticationEntryPoint;
import com.shoestore.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication
        .AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration
        .AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration
        .EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders
        .HttpSecurity;

import org.springframework.security.config.http
        .SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt
        .BCryptPasswordEncoder;

import org.springframework.security.crypto.password
        .PasswordEncoder;

import org.springframework.security.web
        .SecurityFilterChain;

import org.springframework.security.web.authentication
        .UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint
            jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // 🌟 1. Chỉ cho phép các API Auth công khai truy cập không cần login
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

                        // 🌟 2. Các API Auth nhạy cảm dưới đây BẮT BUỘC phải đăng nhập mới được gọi
                        .requestMatchers(
                                "/api/v1/auth/change-password",
                                "/api/v1/auth/me",
                                "/api/v1/auth/logout"
                        ).authenticated()

                        .requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
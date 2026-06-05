package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.user.AuthProvider;
import com.shoestore.common.enums.user.RoleName;
import com.shoestore.common.enums.user.UserStatus;
import com.shoestore.dto.request.authRequest.*;
import com.shoestore.dto.response.authResponse.AuthResponse;
import com.shoestore.dto.response.authResponse.GoogleUserInfo;
import com.shoestore.dto.response.authResponse.UserResponse;
import com.shoestore.entity.PasswordResetToken;
import com.shoestore.entity.RefreshToken;
import com.shoestore.entity.Role;
import com.shoestore.entity.User;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.PasswordResetTokenRepository;
import com.shoestore.repository.RoleRepository;
import com.shoestore.repository.UserRepository;
import com.shoestore.security.jwt.JwtService;
import com.shoestore.service.*;

import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional


public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final GoogleAuthService googleAuthService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;


    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL)
                .emailVerified(false) // 🌟 Mặc định chưa xác thực
                .build();

        user.getRoles().add(customerRole);
        User savedUser = userRepository.save(user);

        // Tạo mã kích hoạt gửi về hòm thư
        emailVerificationService.createAndSendVerificationToken(savedUser);

        // 🌟 KHÔNG sinh cặp Token nữa. Trả về Object rỗng kèm thông điệp bắt buộc kích hoạt tài khoản
        return AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .accessToken(null)  // Khóa lại
                .refreshToken(null) // Khóa lại
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm user theo Email
        User user = userRepository.findWithRolesAndPermissionsByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 2. Kiểm tra các rào cản trạng thái tài khoản (Giữ nguyên thứ tự chuẩn của bạn)
        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 3. So khớp mật khẩu băm
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            // Khóa tài khoản nếu thử sai từ 5 lần trở lên
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountLocked(true);
            }

            userRepository.save(user);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 4. Đăng nhập thành công -> Reset số lần thử sai và cập nhật log thời gian
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 5. Thu hồi token cũ và sinh cặp token mới
        refreshTokenService.revokeAllUserTokens(user);

        com.shoestore.security.user.UserPrincipal userPrincipal = new com.shoestore.security.user.UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(userPrincipal);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // 1. Xác thực và tìm RefreshToken cũ trong DB
        RefreshToken oldToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        // 🌟 2. TỐI ƯU HÓA: Nạp lại User cùng toàn bộ Roles/Permissions bằng EntityGraph
        // Thay vì chỉ lấy đối tượng User thô (Lazy) từ oldToken.getUser()
        User user = userRepository.findWithRolesAndPermissionsByEmail(oldToken.getUser().getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. Hủy bỏ token cũ để tránh hiện tượng replay attack
        refreshTokenService.revokeToken(request.getRefreshToken());

        // 4. Bọc đối tượng đầy đủ dữ liệu vào Principal để sinh Access Token mới
        com.shoestore.security.user.UserPrincipal userPrincipal = new com.shoestore.security.user.UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(userPrincipal);

        // 5. Tạo chuỗi Refresh Token mới xoay vòng
        RefreshToken newToken = refreshTokenService.createRefreshToken(user);

        return buildResponse(user, accessToken, newToken.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    private AuthResponse buildResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        // 1. Xác thực ID Token gửi từ phía Client Google lên Server
        GoogleUserInfo googleUser = googleAuthService.verifyToken(request.getIdToken());

        if (!Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            throw new BusinessException(ErrorCode.GOOGLE_EMAIL_NOT_VERIFIED);
        }

        // 2. Tìm kiếm sự tồn tại của User bằng email
        User user = userRepository.findWithRolesAndPermissionsByEmail(googleUser.getEmail()).orElse(null);

        if (user == null) {
            // --- LUỒNG TẠO USER MỚI ---
            Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

            user = User.builder()
                    .email(googleUser.getEmail())
                    .fullName(googleUser.getName())
                    .avatarUrl(googleUser.getPicture())
                    .status(UserStatus.ACTIVE)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(googleUser.getGoogleId())
                    .emailVerified(true)
                    .password(null)
                    .build();

            user.getRoles().add(customerRole);
        } else {
            // --- LUỒNG USER ĐÃ TỒN TẠI (Đồng bộ thông tin từ Google) ---
            if (user.getProvider() == AuthProvider.LOCAL) {
                user.setEmailVerified(true);
            }

            if (user.getAvatarUrl() == null) {
                user.setAvatarUrl(googleUser.getPicture());
            }

            if (user.getProviderId() == null) {
                user.setProviderId(googleUser.getGoogleId());
            }
        }

        // 3. Kiểm tra các rào cản trạng thái tài khoản
        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 4. Cập nhật log thời gian đăng nhập cuối cùng
        user.setLastLoginAt(LocalDateTime.now());

        // 5. Lưu toàn bộ thay đổi (Tạo mới/Cập nhật) xuống DB
        user = userRepository.save(user);

        // 6. Thu hồi toàn bộ JWT Token cũ của User này
        refreshTokenService.revokeAllUserTokens(user);

        // 🌟 7. TỐI ƯU HÓA: Nạp lại bản ghi "Sạch và Đầy Đủ" kèm theo đồ thị quyền hạn từ DB
        // Do luồng trên vừa lưu thực thể thô, bọc ngay vào Principal sẽ gây query rời rạc (Lazy load)
        User fullUser = userRepository.findWithRolesAndPermissionsByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        com.shoestore.security.user.UserPrincipal userPrincipal = new com.shoestore.security.user.UserPrincipal(fullUser);
        String accessToken = jwtService.generateAccessToken(userPrincipal);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(fullUser);

        return buildResponse(fullUser, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // 1. Kiểm tra email có tồn tại không
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return; // Bảo mật: Ẩn việc email không tồn tại
        }

        // 🌟 LÔGIC CHỐNG SPAM: Kiểm tra xem token cũ được sinh ra chưa quá 1 phút hay không

        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);
        if (existingToken.isPresent() && existingToken.get().isCreatedWithinOneMinute()) {
            throw new BusinessException(ErrorCode.PLEASE_WAIT_BEFORE_RESENDING);
        }

        // 2. Xóa token cũ nếu có để tránh rác DB (Chỉ chạy khi đã vượt qua bộ lọc chống spam ở trên)
        passwordResetTokenRepository.deleteByUser(user);

        // 3. Sinh token UUID ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // 4. Lưu token vào DB với thời gian hết hạn là 15 phút
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        passwordResetTokenRepository.save(resetToken);

        // 5. Gửi mail chứa link kèm token về cho user
        // Thay vì viết cứng chữ "https://frontend"
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        mailService.sendResetPasswordEmail(user.getEmail(), resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Kiểm tra token có tồn tại trong hệ thống không
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RESET_TOKEN));

        // 2. Kiểm tra token đã hết hạn chưa
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BusinessException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        // 3. Cập nhật mật khẩu mới (nhớ mã hóa bằng passwordEncoder)
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 4. Thu hồi toàn bộ JWT token đang hoạt động của user này để ép mọi thiết bị đăng xuất
        refreshTokenService.revokeAllUserTokens(user);

        // 5. Xóa token reset này đi vì đã dùng rồi
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 1. Gọi hàm tiện ích tập trung từ SecurityUtils để lấy email an toàn
        String currentEmail = com.shoestore.util.SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. Kiểm tra nếu tài khoản login bằng Google/Facebook mà không có mật khẩu (password == null)
        if (user.getPassword() == null) {
            throw new BusinessException(ErrorCode.SOCIAL_ACCOUNT_CANNOT_CHANGE_PASSWORD);
        }

        // 3. Kiểm tra nhanh: Mật khẩu mới không được trùng với mật khẩu cũ vừa nhập trên Form
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new BusinessException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        // 4. Khớp mật khẩu cũ xem có đúng với mật khẩu trong DB không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INCORRECT_OLD_PASSWORD);
        }

        // 5. Kiểm tra mật khẩu mới không được trùng mật khẩu cũ đã lưu trong DB (Phòng hờ trường hợp đổi lại pass cũ)
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        // 6. Mã hóa mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 7. Thu hồi toàn bộ token hoạt động cũ (Ép đăng xuất ở các thiết bị khác)
        refreshTokenService.revokeAllUserTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        // 1. Sử dụng SecurityUtils để lấy email an toàn
        String currentEmail = com.shoestore.util.SecurityUtils.getCurrentUserEmail();

        // 2. Tìm User trong DB
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. Đổi lại tên hàm getter chuẩn theo Entity User của bạn:
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())      // 🌟 Đúng tên fullName
                .avatarUrl(user.getAvatarUrl())    // 🌟 Đúng tên getAvatarUrl()
                .emailVerified(user.getEmailVerified()) // 🌟 Đúng tên getEmailVerified()
                // .roles(...) nếu cần
                .build();
    }



}
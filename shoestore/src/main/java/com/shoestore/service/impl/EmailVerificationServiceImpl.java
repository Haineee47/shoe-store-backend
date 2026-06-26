package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.entity.EmailVerificationToken;
import com.shoestore.entity.User;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.EmailVerificationTokenRepository;
import com.shoestore.repository.UserRepository;
import com.shoestore.service.EmailVerificationService;
import com.shoestore.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    // 🌟 1. Đọc URL từ file application.yml/properties để linh hoạt thay đổi giữa các môi trường
    @Value("${app.api-base-url:http://localhost:8080}")
    private String apiBaseUrl;

    // 🌟 2. CHỈ đặt @Transactional ở những nơi thực sự thao tác DB. Gửi mail nằm NGOÀI transaction.
    @Override
    public void createAndSendVerificationToken(User user) {
        // Tạo và lưu token trong 1 Transaction ngắn
        String token = saveVerificationToken(user);

        String verificationUrl = apiBaseUrl + "/api/v1/auth/verify-email?token=" + token;

        // 🌟 3. Gọi gửi mail bất đồng bộ (Async) - Không chặn luồng chính của API
        // Đảm bảo hàm sendVerificationEmail trong MailService đã được gắn @Async
        try {
            mailService.sendVerificationEmail(user.getEmail(), verificationUrl);
        } catch (Exception e) {
            log.error("Không thể gửi email kích hoạt tới: {}. Lỗi: {}", user.getEmail(), e.getMessage());
            // Tùy nghiệp vụ: có thể ném exception hoặc chỉ log lại để admin xử lý thủ công
        }
    }

    // Hàm phụ trợ tách biệt transaction để giải phóng DB Connection sớm
    @Transactional
    protected String saveVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiredAt(LocalDateTime.now().plusHours(24)) // 24 tiếng là chuẩn
                .used(false)
                .build();

        tokenRepository.save(verificationToken);
        return token;
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (Boolean.TRUE.equals(verificationToken.getUsed())) {
            throw new BusinessException(ErrorCode.TOKEN_ALREADY_USED);
        }

        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        verificationToken.setUsed(true);

        // Đối với Hibernate, khi kết thúc hàm @Transactional, thực thể được thay đổi (Dirty Checking)
        // sẽ tự động được UPDATE xuống DB. Không nhất thiết phải gọi hàm save() thủ công.
    }

    @Override
    @Transactional
    public String resendVerificationEmail(String email) { // 🌟 Đổi void -> String
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Xóa dữ liệu cũ
        tokenRepository.deleteByUser(user);
        tokenRepository.flush();

        // 🌟 Thay vì gọi hàm createAndSendVerificationToken(user) trực tiếp,
        // ta tự sinh token ở đây để trả về, hoặc chỉnh hàm kia trả về token.
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiredAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();

        tokenRepository.save(verificationToken);

        // Gửi mail chạy ngầm (Async)
        String verificationUrl = apiBaseUrl + "/api/v1/auth/verify-email?token=" + token;
        mailService.sendVerificationEmail(user.getEmail(), verificationUrl);

        return token; // 🌟 Trả token về cho Controller
    }

    @Override
    @Transactional(readOnly = true)
    public String getEmailByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> t.getUser().getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
    }
}
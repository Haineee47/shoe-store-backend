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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl
        implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public void createAndSendVerificationToken(
            User user
    ) {

        String token =
                UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .token(token)
                        .user(user)
                        .expiredAt(
                                LocalDateTime.now()
                                        .plusHours(24)
                        )
                        .build();

        tokenRepository.save(
                verificationToken
        );

        String verificationUrl =
                "http://localhost:8080/api/auth/verify-email?token="
                        + token;

        mailService.sendVerificationEmail(
                user.getEmail(),
                verificationUrl
        );
    }

    @Override
    public void verifyEmail(
            String token
    ) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.INVALID_TOKEN
                                ));

        if (verificationToken.getUsed()) {
            throw new BusinessException(
                    ErrorCode.TOKEN_ALREADY_USED
            );
        }

        if (
                verificationToken
                        .getExpiredAt()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new BusinessException(
                    ErrorCode.TOKEN_EXPIRED
            );
        }

        User user =
                verificationToken.getUser();

        user.setEmailVerified(true);

        verificationToken.setUsed(true);

        userRepository.save(user);
    }

    @Override
    public void resendVerificationEmail(
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        ));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_VERIFIED
            );
        }

        tokenRepository.findByUser(user)
                .ifPresent(tokenRepository::delete);

        createAndSendVerificationToken(user);
    }
}
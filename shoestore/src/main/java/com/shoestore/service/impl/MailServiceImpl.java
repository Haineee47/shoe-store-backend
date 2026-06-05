package com.shoestore.service.impl;

import com.shoestore.service.MailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl
        implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(
            String to,
            String verificationUrl
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(
                "Verify your ShoeStore account"
        );

        message.setText(
                """
                Welcome to ShoeStore.

                Click the link below to verify your email:

                %s

                This link expires in 24 hours.
                """.formatted(verificationUrl)
        );

        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h3>Yêu cầu đặt lại mật khẩu</h3>"
                    + "<p>Vui lòng click vào đường dẫn dưới đây để đổi mật khẩu (Hiệu lực 15 phút):</p>"
                    + "<a href=\"" + resetLink + "\">Đặt lại mật khẩu</a>";

            helper.setText(htmlMsg, true); // true = gửi dạng HTML
            helper.setTo(toEmail);
            helper.setSubject("ShoeStore - Reset Password Request");
            helper.setFrom("noreply@shoestore.com");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage());
        }
    }
}
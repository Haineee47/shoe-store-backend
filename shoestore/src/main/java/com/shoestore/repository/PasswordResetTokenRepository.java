package com.shoestore.repository;

import com.shoestore.entity.PasswordResetToken;
import com.shoestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);

    Optional<PasswordResetToken> findByUser(User user);
}
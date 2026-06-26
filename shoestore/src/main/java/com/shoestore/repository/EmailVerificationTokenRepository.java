package com.shoestore.repository;

import com.shoestore.entity.EmailVerificationToken;
import com.shoestore.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.user = :user")
    void deleteByUser(@Param("user") User user);
}
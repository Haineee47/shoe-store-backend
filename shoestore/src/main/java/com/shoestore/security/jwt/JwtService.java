package com.shoestore.security.jwt;

import com.shoestore.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(User user);

    String extractUsername(String token);

    boolean isValidToken(
            String token,
            UserDetails userDetails
    );

    Long extractExpiration(String token);
}
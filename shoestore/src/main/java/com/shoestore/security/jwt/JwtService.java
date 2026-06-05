package com.shoestore.security.jwt;


import com.shoestore.security.user.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(UserPrincipal userPrincipal);

    String extractUsername(String token);

    boolean isValidToken(
            String token,
            UserDetails userDetails
    );

    Long extractExpiration(String token);
}
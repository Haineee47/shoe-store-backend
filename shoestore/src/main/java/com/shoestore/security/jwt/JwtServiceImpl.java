package com.shoestore.security.jwt;

import com.shoestore.config.JwtProperties;
import com.shoestore.entity.User;

import com.shoestore.security.user.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(UserPrincipal userPrincipal) {

        Map<String, Object> claims = new HashMap<>();

        // 🌟 Lấy toàn bộ danh sách phẳng (gồm cả Role và Permission) đã được gom sẵn ở UserPrincipal
        var authorities = userPrincipal.getAuthorities()
                .stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        // Đẩy danh sách này vào claims với key là "authorities" (chuẩn Spring Security)
        claims.put("authorities", authorities);

        return buildToken(
                claims,
                userPrincipal.getUsername(), // userPrincipal.getUsername() chính là email của bạn
                jwtProperties.getAccessTokenExpiration()
        );
    }



    @Override
    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    @Override
    public Long extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).getTime();
    }

    @Override
    public boolean isValidToken(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private String buildToken(
            Map<String, Object> claims,
            String subject,
            Long expiration
    ) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
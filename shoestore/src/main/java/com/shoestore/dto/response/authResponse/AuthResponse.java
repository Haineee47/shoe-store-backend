package com.shoestore.dto.response.authResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;

    private String email;

    private String fullName;

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
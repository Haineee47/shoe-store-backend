package com.shoestore.dto.request.authRequest;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
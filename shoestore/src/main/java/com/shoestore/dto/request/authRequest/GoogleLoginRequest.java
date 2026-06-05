package com.shoestore.dto.request.authRequest;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleLoginRequest {

    @NotBlank
    private String idToken;
}
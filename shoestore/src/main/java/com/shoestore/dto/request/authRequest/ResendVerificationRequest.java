package com.shoestore.dto.request.authRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationRequest {

    @Email
    @NotBlank
    private String email;
}
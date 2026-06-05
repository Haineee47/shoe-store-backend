package com.shoestore.dto.response.authResponse;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;     // 🌟 Đổi từ firstName/lastName thành fullName
    private String avatarUrl;    // 🌟 Đổi từ avatar thành avatarUrl
    private Boolean emailVerified;
    private Set<String> roles;
}
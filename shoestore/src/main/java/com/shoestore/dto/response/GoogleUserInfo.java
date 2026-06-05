package com.shoestore.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {

    private String googleId;

    private String email;

    private String name;

    private String picture;

    private Boolean emailVerified;
}
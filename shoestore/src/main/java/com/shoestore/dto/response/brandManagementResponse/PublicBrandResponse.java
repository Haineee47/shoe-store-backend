package com.shoestore.dto.response.brandManagementResponse;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicBrandResponse {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
}
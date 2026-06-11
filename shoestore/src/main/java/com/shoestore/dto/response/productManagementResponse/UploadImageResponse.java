package com.shoestore.dto.response.productManagementResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageResponse {

    private String imageUrl;

    private String publicId;
}
package com.shoestore.dto.response.mediaManagementResponse;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String publicId;
    private String url;
    private String originalFilename; // 🌟 Trả ra để FE làm UI list ảnh tải lên
    private Long size;               // 🌟 Tính bằng byte để FE Format (KB, MB)
    private String contentType;      // 🌟 Định dạng ảnh (image/png, image/webp...)
}
package com.shoestore.dto.request.productManagementRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUpdateRequest {

    /**
     * null = ảnh mới
     * khác null = ảnh đã tồn tại
     */
    private Long id;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    @Size(max = 500)
    private String imageUrl;

    @NotBlank(message = "Public ID của ảnh không được để trống")
    private String publicId;

    @NotNull(message = "Thứ tự ảnh không được để trống")
    @Min(0)
    private Integer sortOrder;

    private boolean isPrimary;
}
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
public class ProductImageRequest {

    private Long id; // Null khi tạo mới (cả ở Create và Update), có giá trị khi Update ảnh cũ

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    @Size(max = 500, message = "Đường dẫn ảnh không vượt quá 500 ký tự")
    private String imageUrl;

    @NotBlank(message = "Public ID của ảnh không được để trống")
    @Size(max = 255, message = "Public ID không vượt quá 255 ký tự")
    private String publicId;

    @NotNull(message = "Thứ tự ảnh không được để trống")
    @Min(0)
    private Integer sortOrder;

    private Boolean primary = false;
}
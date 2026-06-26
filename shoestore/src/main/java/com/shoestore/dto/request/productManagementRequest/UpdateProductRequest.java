package com.shoestore.dto.request.productManagementRequest;

import com.shoestore.validator.product.ValidPrice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm không vượt quá 200 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả ngắn không vượt quá 500 ký tự")
    private String shortDescription;

    private String description;

    @NotBlank(message = "Ảnh đại diện sản phẩm không được để trống")
    private String thumbnailUrl;

    @NotNull(message = "Mã thương hiệu không được để trống")
    private Long brandId;

    @NotNull(message = "Mã danh mục không được để trống")
    private Long categoryId;

    @Builder.Default
    private Boolean featured = false;

    @Size(max = 150, message = "Meta Title không vượt quá 150 ký tự")
    private String metaTitle;

    @Size(max = 255, message = "Meta Description không vượt quá 255 ký tự")
    private String metaDescription;

    // Thay thế đoạn code cũ bằng đoạn này
    @Builder.Default
    @Valid
    private List<ProductImageRequest> images = List.of();

    @NotEmpty(message = "Sản phẩm phải có tối thiểu một SKU")
    @Valid
    private List<SkuUpdateRequest> skus;
}
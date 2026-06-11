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
public class CreateProductRequest {

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
    private boolean featured = false;

    @Size(max = 150, message = "Meta Title không vượt quá 150 ký tự")
    private String metaTitle;

    @Size(max = 255, message = "Meta Description không vượt quá 255 ký tự")
    private String metaDescription;

    // Gallery ảnh phụ (Không bắt buộc)
    @Valid
    private List<ProductImageRequest> galleryImages;


    @NotEmpty(message = "Sản phẩm phải có tối thiểu một phiên bản (SKU)")
    @Valid // Kích hoạt Validation sâu vào từng object con trong List
    private List<SkuRequest> skus;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ValidPrice
    public static class SkuRequest {

        @NotBlank(message = "Mã SKU không được để trống")
        @Size(max = 50, message = "Mã SKU không vượt quá 50 ký tự")
        private String skuCode;

        @NotBlank(message = "Kích cỡ không được để trống")
        private String size;

        @NotBlank(message = "Màu sắc không được để trống")
        private String color;

        @NotNull(message = "Giá vốn không được để trống")
        @DecimalMin(value = "0.0", inclusive = true, message = "Giá vốn không được là số âm")
        private java.math.BigDecimal costPrice;

        @NotNull(message = "Giá bán không được để trống")
        @DecimalMin(value = "0.0", inclusive = true, message = "Giá bán không được là số âm")
        private java.math.BigDecimal sellingPrice;

        @NotNull(message = "Số lượng tồn kho ban đầu không được để trống")
        @Min(value = 0, message = "Số lượng tồn kho không được âm")
        private Integer stockQuantity;

        @NotNull(message = "Ngưỡng cảnh báo tồn kho không được để trống")
        @Min(value = 1, message = "Ngưỡng cảnh báo tồn kho tối thiểu phải bằng 1")
        private Integer lowStockThreshold;

        @NotNull(message = "Trọng lượng không được để trống")
        @Min(value = 1, message = "Trọng lượng vận chuyển tối thiểu là 1 gram")
        private Integer weight;

        private Integer length;
        private Integer width;
        private Integer height;

        @Size(max = 500)
        private String skuImageUrl;

    }
}
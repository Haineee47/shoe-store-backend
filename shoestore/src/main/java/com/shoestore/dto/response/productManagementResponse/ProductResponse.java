package com.shoestore.dto.response.productManagementResponse;

import com.shoestore.common.enums.product.ProductStatus;
import com.shoestore.common.enums.product.SkuStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String thumbnailUrl;
    private ProductStatus status;
    private boolean isFeatured;
    private Integer totalStock;

    // SEO Info
    private String metaTitle;
    private String metaDescription;

    // Trích xuất thông tin quan hệ gọn nhẹ
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;

    // Audit Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SkuResponse> skus;
    private List<ImageResponse> images;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkuResponse {
        private Long id;
        private String skuCode;
        private String size;
        private String color;
        private BigDecimal costPrice; // Chỉ hiển thị ở API Admin, API Public sẽ ẩn trường này đi
        private BigDecimal sellingPrice;
        private Integer stockQuantity;
        private Integer lowStockThreshold;
        private Integer weight;
        private Integer length;
        private Integer width;
        private Integer height;
        private String skuImageUrl;
        private SkuStatus status;
        private Long version;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageResponse {
        private Long id;
        private String imageUrl;
        private String publicId;
        private Integer sortOrder;
        private boolean isPrimary;
    }
}
package com.shoestore.dto.response.productManagementResponse;

import com.shoestore.common.enums.product.ProductStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {
    private Long id;

    private String name;

    private String slug;

    private String thumbnailUrl;

    private ProductStatus status;

    private boolean isFeatured;

    private Integer totalStock;

    private Long brandId;

    private String brandName;

    private Long categoryId;

    private String categoryName;

    private LocalDateTime createdAt;
}
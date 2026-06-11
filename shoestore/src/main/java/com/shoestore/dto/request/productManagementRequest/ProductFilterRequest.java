package com.shoestore.dto.request.productManagementRequest;

import com.shoestore.common.enums.product.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {

    private String keyword;

    private Long brandId;

    private Long categoryId;

    private ProductStatus status;

    private Boolean featured;
}
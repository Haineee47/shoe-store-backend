package com.shoestore.service;

import com.shoestore.dto.request.productManagementRequest.ProductFilterRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogReadService {
    ProductResponse getProductDetail(Long id);
    Page<ProductSummaryResponse> getProductList(ProductFilterRequest filter, Pageable pageable);
}
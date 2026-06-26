package com.shoestore.service;

import com.shoestore.dto.request.productManagementRequest.CreateSkuRequest;
import com.shoestore.dto.request.productManagementRequest.SkuUpdateRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.entity.ProductSku;
import java.util.List;

public interface ProductSkuService {
    List<ProductSku> createSkusForProduct(Long productId, List<CreateSkuRequest> skuRequests);
    ProductResponse.SkuResponse createSingleSku(Long productId, CreateSkuRequest request);
    ProductResponse.SkuResponse updateSku(Long skuId, SkuUpdateRequest request);
    void discontinueSku(Long skuId);
    void discontinueAllSkusByProductId(Long productId);
    List<ProductSku> getSkusByProductId(Long productId);
    void updateProductSkus(Long productId, List<SkuUpdateRequest> skuRequests);
}
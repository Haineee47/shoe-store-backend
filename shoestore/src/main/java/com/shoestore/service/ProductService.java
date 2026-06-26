package com.shoestore.service;

import com.shoestore.dto.request.productManagementRequest.CreateProductRequest;
import com.shoestore.dto.request.productManagementRequest.UpdateProductRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(Long productId, UpdateProductRequest request);
    void deleteProduct(Long id);
    void archiveProduct(Long productId);
}
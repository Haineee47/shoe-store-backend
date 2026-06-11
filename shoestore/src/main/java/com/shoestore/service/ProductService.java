package com.shoestore.service;

import com.shoestore.dto.request.productManagementRequest.CreateProductRequest;
import com.shoestore.dto.request.productManagementRequest.ProductFilterRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.shoestore.dto.request.productManagementRequest.UpdateProductRequest;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    // 🌟 Bước 1: Lấy chi tiết sản phẩm (Dành cho trang sửa Admin hoặc chi tiết Client)
    ProductResponse getProductDetail(Long id);

    // 🌟 Bước 2: Lấy danh sách sản phẩm phân trang thu gọn (Tối ưu performance)
    Page<ProductSummaryResponse> getProductList(ProductFilterRequest filter, Pageable pageable);

    ProductResponse updateProduct(
            Long id,
            UpdateProductRequest request
    );

    void deleteProduct(Long id);


}
package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant; // 🌟 Thêm import constant của bạn
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.productManagementRequest.CreateProductRequest;
import com.shoestore.dto.request.productManagementRequest.ProductFilterRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.service.ProductService;
import com.shoestore.util.PageableUtils; // 🌟 Dùng chung class tiện ích đã chạy ngon ở Brand
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.shoestore.dto.request.productManagementRequest.UpdateProductRequest;

@RestController
@RequestMapping("/api/v1") // 🌟
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin/products") // 🌟 Viết tường minh đường dẫn admin
    @PreAuthorize("@ss.hasPermission('PRODUCT_CREATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo tổ hợp sản phẩm và các phiên bản SKU thành công", response));
    }

    @GetMapping("/admin/products/{id}") // 🌟 Viết tường minh đường dẫn admin
    @PreAuthorize("@ss.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chi tiết sản phẩm thành công", response));
    }

    // 🌟 SỬA TẠI ĐÂY: Bỏ hoàn toàn @PageableDefault, dùng param lẻ giống hệt BrandController
    @GetMapping("/admin/products")
    @PreAuthorize("@ss.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductList(

            ProductFilterRequest filter,

            @RequestParam(
                    defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER
            )
            int page,

            @RequestParam(
                    defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE
            )
            int size,

            @RequestParam(
                    defaultValue = "createdAt,desc"
            )
            String sort
    ) {

        Pageable pageable =
                PageableUtils.createPageable(
                        page,
                        size,
                        sort
                );

        Page<ProductSummaryResponse> response =
                productService.getProductList(
                        filter,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách sản phẩm phân trang thành công",
                        response
                )
        );
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật sản phẩm thành công",
                        response
                )
        );
    }

    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa sản phẩm thành công",
                        null
                )
        );
    }
}
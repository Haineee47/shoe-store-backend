package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.productManagementRequest.*;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.service.CatalogReadService;
import com.shoestore.service.ProductService;
import com.shoestore.service.ProductSkuService;
import com.shoestore.util.PageableUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSkuService productSkuService;
    private final CatalogReadService catalogReadService;

    // =========================================================================
    // 🔍 1. QUERY OPERATIONS - LUỒNG ĐỌC DỮ LIỆU (Read Operations)
    // =========================================================================

    /**
     * API tìm kiếm, lọc nâng cao và phân trang danh sách tổng quan sản phẩm.
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductList(
            ProductFilterRequest filter,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = PageableUtils.createPageable(page, size, sort);
        Page<ProductSummaryResponse> response = catalogReadService.getProductList(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Fetch product list successful", response));
    }

    /**
     * API lấy thông tin chi tiết toàn phần của một sản phẩm dựa trên ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        ProductResponse response = catalogReadService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Fetch product detail successful", response));
    }

    // =========================================================================
    // ⚔️ 2. COMMAND OPERATIONS - LUỒNG GHI DỮ LIỆU (Write/Alter Operations)
    // =========================================================================

    // --- Product Aggregation Core Operations ---

    /**
     * API tạo mới hoàn toàn một tổ hợp sản phẩm gốc cùng danh sách các phiên bản SKU đi kèm.
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermission('PRODUCT_CREATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Create product and SKUs successful", response));
    }

    /**
     * API cập nhật thông tin cơ bản thuộc tính chung của sản phẩm theo ID.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Update product core attributes successful", response));
    }

    /**
     * API xóa vĩnh viễn/Xóa mềm một sản phẩm gốc cùng tất cả các ràng buộc liên quan.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Delete product successful", null));
    }

    // --- Product Variant (SKU) Dependent Operations ---

    /**
     * API bổ sung thêm một biến thể SKU vào trong một tổ hợp sản phẩm hiện hữu.
     */
    @PostMapping("/{id}/skus")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<ProductResponse.SkuResponse>> addSingleSku(
            @PathVariable("id") Long productId,
            @Valid @RequestBody CreateSkuRequest request
    ) {
        ProductResponse.SkuResponse response = productSkuService.createSingleSku(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Add new SKU variant successful", response));
    }

    /**
     * API cập nhật thông tin cấu hình chi tiết của một biến thể SKU cụ thể.
     */
    @PatchMapping("/skus/{skuId}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<ProductResponse.SkuResponse>> updateSingleSku(
            @PathVariable Long skuId,
            @Valid @RequestBody SkuUpdateRequest request
    ) {
        ProductResponse.SkuResponse response = productSkuService.updateSku(skuId, request);
        return ResponseEntity.ok(ApiResponse.success("Update SKU configuration successful", response));
    }

    /**
     * API gắn cờ đóng/ngừng kinh doanh (Discontinue) đối với một phiên bản SKU cụ thể.
     */
    @DeleteMapping("/skus/{skuId}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> deleteSingleSku(@PathVariable Long skuId) {
        productSkuService.discontinueSku(skuId);
        return ResponseEntity.ok(ApiResponse.success("Discontinue SKU variant successful", null));
    }
}
package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.brandManagementRequest.CreateBrandRequest;
import com.shoestore.dto.request.brandManagementRequest.UpdateBrandRequest;
import com.shoestore.dto.response.brandManagementResponse.BrandResponse;
import com.shoestore.dto.response.brandManagementResponse.PublicBrandResponse;
import com.shoestore.service.BrandService;
import com.shoestore.util.PageableUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // ==========================================
    // 👑 1. ENDPOINTS DÀNH RIÊNG CHO QUẢN TRỊ (ADMIN / STAFF)
    // ==========================================

    @PostMapping("/admin/brands")
    @PreAuthorize("@ss.hasPermission('BRAND_CREATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo thương hiệu mới thành công", response));
    }

    @PutMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_UPDATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thương hiệu thành công", response));
    }

    @PatchMapping("/admin/brands/{id}/status")
    @PreAuthorize("@ss.hasPermission('BRAND_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        brandService.changeStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thương hiệu thành công", null));
    }

    @DeleteMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thương hiệu thành công", null));
    }

    @GetMapping("/admin/brands")
    @PreAuthorize("@ss.hasPermission('BRAND_VIEW')")
    public ResponseEntity<ApiResponse<Page<BrandResponse>>> searchBrandsForAdmin(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "sortOrder,asc") String sort) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort);
        Page<BrandResponse> response = brandService.searchBrands(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách quản trị thương hiệu thành công", response));
    }

    @GetMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_VIEW')")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Long id) {
        BrandResponse response = brandService.getBrandByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết thương hiệu thành công", response));
    }

    // ==========================================
    // 🌐 2. ENDPOINTS CÔNG KHAI (PUBLIC CLIENT)
    // ==========================================

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<PublicBrandResponse>>> getPublicBrands() { // 🌟 SỬA TẠI ĐÂY: BrandResponse -> PublicBrandResponse
        List<PublicBrandResponse> response = brandService.getPublicBrands();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thương hiệu thành công", response));
    }

    @GetMapping("/brands/{slug}")
    public ResponseEntity<ApiResponse<PublicBrandResponse>> getBrandBySlug(@PathVariable String slug) { // 🌟 SỬA TẠI ĐÂY: BrandResponse -> PublicBrandResponse
        PublicBrandResponse response = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thương hiệu thành công", response));
    }
}
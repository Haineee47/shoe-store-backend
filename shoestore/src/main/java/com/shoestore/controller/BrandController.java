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

    // =========================================================================
    // 🌐 1. ENDPOINTS CÔNG KHAI - KHÔNG CẦN ĐĂNG NHẬP (Public Client)
    // =========================================================================

    /**
     * API lấy toàn bộ danh sách thương hiệu đang hoạt động để hiển thị lên Menu/Bộ lọc của khách hàng.
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<PublicBrandResponse>>> getPublicBrands() {
        List<PublicBrandResponse> response = brandService.getPublicBrands();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thương hiệu thành công", response));
    }

    /**
     * API lấy chi tiết thông tin của một thương hiệu dựa trên đường dẫn tĩnh (Slug) phục vụ SEO.
     */
    @GetMapping("/brands/{slug}")
    public ResponseEntity<ApiResponse<PublicBrandResponse>> getBrandBySlug(@PathVariable String slug) {
        PublicBrandResponse response = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thương hiệu thành công", response));
    }

    // =========================================================================
    // 👑 2. ENDPOINTS DÀNH RIÊNG CHO QUẢN TRỊ - YÊU CẦU ĐĂNG NHẬP (Admin / Staff)
    // =========================================================================

    // --- Luồng Đọc dữ liệu (Query Side) ---

    /**
     * API tìm kiếm, phân trang và sắp xếp danh sách thương hiệu dành cho trang quản trị Admin.
     */
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

    /**
     * API lấy chi tiết một thương hiệu theo ID vật lý (Bao gồm cả các trường ẩn) phục vụ form chỉnh sửa của Admin.
     */
    @GetMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_VIEW')")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Long id) {
        BrandResponse response = brandService.getBrandByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết thương hiệu thành công", response));
    }

    // --- Luồng Ghi/Thay đổi dữ liệu (Command Side) ---

    /**
     * API tạo mới thương hiệu độc quyền cho dữ liệu hệ thống.
     */
    @PostMapping("/admin/brands")
    @PreAuthorize("@ss.hasPermission('BRAND_CREATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo thương hiệu mới thành công", response));
    }

    /**
     * API cập nhật thông tin toàn phần của một thương hiệu theo ID.
     */
    @PutMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_UPDATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thương hiệu thành công", response));
    }

    /**
     * API cập nhật một phần trạng thái Bật/Tắt (Active/Inactive) nhanh ngoài danh sách Admin.
     */
    @PatchMapping("/admin/brands/{id}/status")
    @PreAuthorize("@ss.hasPermission('BRAND_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        brandService.changeStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thương hiệu thành công", null));
    }

    /**
     * API Xóa vĩnh viễn/Xóa mềm một thương hiệu khỏi hệ thống.
     */
    @DeleteMapping("/admin/brands/{id}")
    @PreAuthorize("@ss.hasPermission('BRAND_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thương hiệu thành công", null));
    }
}
package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.categoryManagementRequest.*;
import com.shoestore.dto.response.categoryManagementResponse.*;
import com.shoestore.service.CategoryService;
import com.shoestore.util.PageableUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    // =========================================================================
    // 🌐 1. ENDPOINTS CÔNG KHAI - PUBLIC CLIENT
    // =========================================================================

    @GetMapping("/categories/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getPublicCategoryTree() {
        return ResponseEntity.ok(ApiResponse.success("Lấy cấu trúc cây danh mục thành công",
                categoryService.getPublicCategoryTree()));
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<ApiResponse<PublicCategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin danh mục thành công",
                categoryService.getCategoryBySlug(slug)));
    }

    // =========================================================================
    // 👑 2. ENDPOINTS QUẢN TRỊ - ADMIN / STAFF
    // =========================================================================

    @GetMapping("/admin/categories")
    @PreAuthorize("@ss.hasPermission('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<Page<CategorySummaryResponse>>> searchCategoriesForAdmin(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER) @Min(0) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE) @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "sortOrder,asc") String sort) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách quản trị danh mục thành công",
                categoryService.searchCategories(q, pageable)));
    }

    @GetMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết danh mục thành công",
                categoryService.getCategoryByIdForAdmin(id)));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("@ss.hasPermission('CATEGORY_CREATE')")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo danh mục mới thành công", categoryService.createCategory(request)));
    }

    @PatchMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công",
                categoryService.updateCategory(id, request)));
    }

    @PatchMapping("/admin/categories/{id}/status")
    @PreAuthorize("@ss.hasPermission('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody CategoryStatusRequest request) {
        categoryService.changeStatus(id, request.getActive());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái danh mục thành công", null));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_DELETE')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
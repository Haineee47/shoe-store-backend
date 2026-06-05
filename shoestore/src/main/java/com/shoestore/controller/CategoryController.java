package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.categoryManagementRequest.CategoryRequest;
import com.shoestore.dto.response.categoryManagementResponse.CategoryResponse;
import com.shoestore.dto.response.categoryManagementResponse.CategoryTreeResponse;
import com.shoestore.service.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;

    // ==========================================
    // 👑 1. ENDPOINTS DÀNH RIÊNG CHO QUẢN TRỊ (ADMIN / STAFF ĐƯỢC ỦY QUYỀN)
    // ==========================================

    @PostMapping("/admin/categories")
    @PreAuthorize("@ss.hasPermission('CATEGORY_CREATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo danh mục mới thành công", response));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", response));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa danh mục thành công", null));
    }

    @PatchMapping("/admin/categories/{id}/status")
    @PreAuthorize("@ss.hasPermission('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        categoryService.changeStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái danh mục thành công", null));
    }

    @GetMapping("/admin/categories")
    @PreAuthorize("@ss.hasPermission('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> searchCategoriesForAdmin(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "sortOrder,asc") String sort) { // Giữ sortOrder,asc cho đặc thù hiển thị thứ tự danh mục

        // Tận dụng PageableUtils để khởi tạo phân trang đồng bộ
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<CategoryResponse> response = categoryService.searchCategories(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách quản trị danh mục thành công", response));
    }

    @GetMapping("/admin/categories/{id}")
    @PreAuthorize("@ss.hasPermission('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết danh mục thành công", response));
    }

    // ==========================================
    // 🌐 2. ENDPOINTS CÔNG KHAI (Giữ nguyên - không cần Đăng nhập)
    // ==========================================

    @GetMapping("/categories/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getPublicCategoryTree() {
        List<CategoryTreeResponse> response = categoryService.getPublicCategoryTree();
        return ResponseEntity.ok(ApiResponse.success("Lấy cấu trúc cây danh mục thành công", response));
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin danh mục thành công", response));
    }
}
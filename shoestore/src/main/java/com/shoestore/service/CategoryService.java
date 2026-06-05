package com.shoestore.service;

import com.shoestore.dto.request.categoryManagementRequest.CategoryRequest;
import com.shoestore.dto.response.categoryManagementResponse.CategoryResponse;
import com.shoestore.dto.response.categoryManagementResponse.CategoryTreeResponse;
import com.shoestore.dto.response.categoryManagementResponse.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    // --- Admin APIs ---
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    void changeStatus(Long id, Boolean isActive);
    Page<CategoryResponse> searchCategories(String keyword, Pageable pageable);
    CategoryResponse getCategoryByIdForAdmin(Long id);

    // --- Public APIs ---
    List<CategoryTreeResponse> getPublicCategoryTree();
    CategoryResponse getCategoryBySlug(String slug);
}
package com.shoestore.service;

import com.shoestore.dto.request.categoryManagementRequest.*;
import com.shoestore.dto.response.categoryManagementResponse.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    AdminCategoryResponse createCategory(CreateCategoryRequest request);
    AdminCategoryResponse updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);
    void changeStatus(Long id, Boolean active);
    Page<CategorySummaryResponse> searchCategories(String keyword, Pageable pageable);
    AdminCategoryResponse getCategoryByIdForAdmin(Long id);
    List<CategoryTreeResponse> getPublicCategoryTree();
    PublicCategoryResponse getCategoryBySlug(String slug);
}
package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.dto.request.categoryManagementRequest.CategoryRequest;
import com.shoestore.dto.response.categoryManagementResponse.CategoryResponse;
import com.shoestore.dto.response.categoryManagementResponse.CategoryTreeResponse;
import com.shoestore.entity.Category;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.CategoryRepository;
import com.shoestore.service.CategoryService;
import com.shoestore.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // ✨ Tận dụng @NotBlank, không cần check null, tiến hành chuẩn hóa chuỗi
        String normalizedName = normalizeName(request.getName());

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }

        String slug = StringUtils.toSlug(normalizedName);
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
        }

        Category category = Category.builder()
                .name(normalizedName)
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // ✨ Chuẩn hóa tên khi update
        String normalizedName = normalizeName(request.getName());

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }

        category.setName(normalizedName);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());
        if (request.getIsActive() != null) category.setActive(request.getIsActive());

        // ✨ Chiến lược Immutable Slug: Hoàn toàn không đụng tới trường slug tại đây khi Update

        if (request.getParentId() != null) {
            if (id.equals(request.getParentId())) {
                throw new BusinessException(ErrorCode.CATEGORY_CANNOT_BE_ITS_OWN_PARENT);
            }

            validateCircularReference(id, request.getParentId());

            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByParentId(id)) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_CHILDREN_CANNOT_DELETE);
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Boolean isActive) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setActive(isActive);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategories(String keyword, Pageable pageable) {
        // ✨ Sửa đổi: Loại bỏ khoảng trắng đầu cuối của từ khóa tìm kiếm
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable).map(this::mapToResponse);
        }

        return categoryRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByIdForAdmin(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return mapToResponse(category);
    }

    // --- Public Logic APIs ---

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getPublicCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrderAsc();
        return rootCategories.stream()
                .map(this::mapToTreeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return mapToResponse(category);
    }

    // --- Helper Mappers ---

    private CategoryTreeResponse mapToTreeResponse(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .children(category.getChildren().stream()
                        .filter(Category::isActive)
                        .map(this::mapToTreeResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .sortOrder(category.getSortOrder())
                .isActive(category.isActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    // ✨ Helper Method: Chuẩn hóa loại bỏ khoảng trắng thừa (Đầu, cuối và giữa các từ)
    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    // Helper Method: Chống vòng lặp vô hạn cây cha con
    private void validateCircularReference(Long categoryId, Long parentId) {
        if (parentId == null) {
            return;
        }

        Long currentId = parentId;

        while (currentId != null) {
            if (currentId.equals(categoryId)) {
                throw new BusinessException(ErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            Category category = categoryRepository.findById(currentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            currentId = category.getParent() != null ? category.getParent().getId() : null;
        }
    }
}
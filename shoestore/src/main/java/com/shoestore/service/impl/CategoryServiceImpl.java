package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.dto.request.categoryManagementRequest.*;
import com.shoestore.dto.response.categoryManagementResponse.*;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public AdminCategoryResponse createCategory(CreateCategoryRequest request) {
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
                .isActive(true) // Tạo mới mặc định active
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }

        return mapToAdminResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public AdminCategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        if (request.getName() != null) {
            String normalizedName = normalizeName(request.getName());
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
                throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
            }
            category.setName(normalizedName);
        }

        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());

        // 🛡️ INVARIANT CHECK: Chống tự liên kết chính mình & Liên kết vòng (Vấn đề 8 từ review)
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

        return mapToAdminResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Boolean active) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 🛡️ INVARIANT CHECK: Cascade Deactivation
        // Nếu tắt cha -> Tự động tắt đệ quy toàn bộ cụm danh mục con để tránh dữ liệu mồ côi
        processStatusCascade(category, active);
        categoryRepository.save(category);
    }

    private void processStatusCascade(Category category, boolean active) {
        category.setActive(active);
        if (!active && category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                processStatusCascade(child, false);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 🛡️ INVARIANT CHECK: Chặn xóa nếu còn danh mục con liên kết
        if (categoryRepository.existsByParentId(id)) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_CHILDREN_CANNOT_DELETE);
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategorySummaryResponse> searchCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable).map(this::mapToSummaryResponse);
        }
        return categoryRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable)
                .map(this::mapToSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCategoryResponse getCategoryByIdForAdmin(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return mapToAdminResponse(category);
    }

    // =========================================================================
    // ⚡ GIẢI PHÁP TIÊU DIỆT N+1 QUERY: IN-MEMORY TREE BUILDING (Vấn đề 9)
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getPublicCategoryTree() {
        // 🔥 Bắn duy nhất 1 QUERY quét toàn bộ Category đang hoạt động lên RAM
        List<Category> allActiveCategories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();

        if (allActiveCategories.isEmpty()) return Collections.emptyList();

        // Map mượt mà sang cấu trúc DTO phẳng (Flat DTO) nhằm tách biệt Entity hoàn toàn khỏi RAM logic
        List<CategoryTreeResponse> flatNodes = allActiveCategories.stream()
                .map(c -> CategoryTreeResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .imageUrl(c.getImageUrl())
                        .parentId(c.getParent() != null ? c.getParent().getId() : null)
                        .children(new ArrayList<>())
                        .build())
                .toList();

        // Đưa vào Map O(1) để tăng tốc độ tìm kiếm nút cha
        Map<Long, CategoryTreeResponse> nodeMap = flatNodes.stream()
                .collect(Collectors.toMap(CategoryTreeResponse::getId, node -> node));

        List<CategoryTreeResponse> rootNodes = new ArrayList<>();

        // 🚀 Thuật toán dựng cây 1 vòng lặp (Single Pass) chạy trên RAM: Độ phức tạp O(N)
        for (CategoryTreeResponse node : flatNodes) {
            if (node.getParentId() == null) {
                rootNodes.add(node);
            } else {
                CategoryTreeResponse parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }
        return rootNodes;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicCategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return mapToPublicResponse(category);
    }

    // =========================================================================
    // 🔄 TẦNG ĐỒNG BỘ MAPPERS - ĐẢM BẢO ENCAPSULATION
    // =========================================================================

    private CategorySummaryResponse mapToSummaryResponse(Category category) {
        CategorySummaryResponse dto = new CategorySummaryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setActive(category.isActive());
        dto.setSortOrder(category.getSortOrder());
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        return dto;
    }

    private AdminCategoryResponse mapToAdminResponse(Category category) {
        AdminCategoryResponse dto = new AdminCategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setActive(category.isActive());
        dto.setSortOrder(category.getSortOrder());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

    private PublicCategoryResponse mapToPublicResponse(Category category) {
        PublicCategoryResponse dto = new PublicCategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        return dto;
    }

    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private void validateCircularReference(Long categoryId, Long parentId) {
        Long currentParentId = parentId;
        int maxDepth = 50; // Giới hạn an toàn phòng trường hợp rác dữ liệu DB cũ
        int depth = 0;

        while (currentParentId != null) {
            if (currentParentId.equals(categoryId)) {
                throw new BusinessException(ErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }
            if (++depth > maxDepth) {
                throw new BusinessException(ErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            Category parentNode = categoryRepository.findById(currentParentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));

            currentParentId = parentNode.getParent() != null ? parentNode.getParent().getId() : null;
        }
    }
}
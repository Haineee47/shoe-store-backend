package com.shoestore.repository;

import com.shoestore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // =====================================================
    // SLUG VALIDATION
    // =====================================================

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Optional<Category> findBySlug(String slug);

    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    // =====================================================
    // NAME VALIDATION
    // =====================================================

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    // =====================================================
    // PARENT CATEGORY
    // =====================================================

    List<Category> findByParentId(Long parentId);

    boolean existsByParentId(Long parentId);

    List<Category> findByParentIsNullOrderBySortOrderAsc();

    List<Category> findByParentIsNullAndIsActiveTrueOrderBySortOrderAsc();

    // =====================================================
    // PUBLIC CATEGORY
    // =====================================================

    List<Category> findByIsActiveTrueOrderBySortOrderAsc();

    // =====================================================
    // ADMIN SEARCH
    // =====================================================

    Page<Category> findByNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    // Kiểm tra trùng tên (Không phân biệt hoa thường)
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
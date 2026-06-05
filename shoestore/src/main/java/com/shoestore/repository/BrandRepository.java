package com.shoestore.repository;

import com.shoestore.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlug(String slug);

    Page<Brand> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 🌟 API Public: Chỉ lấy thương hiệu đang hoạt động và sắp xếp theo thứ tự
    List<Brand> findByIsActiveTrueOrderBySortOrderAsc();

    Optional<Brand> findBySlugAndIsActiveTrue(String slug);
}
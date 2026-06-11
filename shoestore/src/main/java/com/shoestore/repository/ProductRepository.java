package com.shoestore.repository;

import com.shoestore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    // 🌟 FIX ĐIỂM 6: Thêm kiểm tra trùng tên sản phẩm bỏ qua hoa thường (Phòng vệ chặn từ Admin)
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // 🌟 Chuẩn bị cho Detail API công khai / Admin
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    Optional<Product> findBySlugAndDeletedAtIsNull(String slug);

    // 🌟 Chuẩn bị cho Soft Delete kiểm tra tồn tại nhanh
    boolean existsByIdAndDeletedAtIsNull(Long id);

    // 🌟 Chuẩn bị cho Update Name: Kiểm tra trùng tên với sản phẩm khác (trừ chính nó)
    boolean existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
            String name,
            Long id
    );


    // 🌟 FIX ĐIỂM 2: Sửa lại cú pháp JPQL chuẩn bằng hàm CONCAT để Hibernate bốc dịch chính xác
    @Query("""
        SELECT COUNT(p) 
        FROM Product p 
        WHERE p.slug LIKE CONCAT(:slugPrefix, '%')
    """)
    long countBySlugPrefix(@Param("slugPrefix") String slugPrefix);

    // 🌟 Chuẩn bị cho List API phân trang mặc định luôn lọc các sản phẩm chưa xóa
    @EntityGraph(attributePaths = {"brand", "category"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "brand",
            "category"
    })
    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {
            "brand",
            "category",

    })
    @Query("""
    select p
    from Product p
    where p.id = :id
    and p.deletedAt is null
""")
    Optional<Product> findWithDetailsByIdAndDeletedAtIsNull(
            @Param("id") Long id
    );

    boolean existsBySlugAndIdNotAndDeletedAtIsNull(
            String slug,
            Long id
    );


}
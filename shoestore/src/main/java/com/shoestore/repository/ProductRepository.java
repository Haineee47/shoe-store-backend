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

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    Optional<Product> findBySlugAndDeletedAtIsNull(String slug);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(String name, Long id);

    @Query("""
        SELECT COUNT(p) 
        FROM Product p 
        WHERE p.slug LIKE CONCAT(:slugPrefix, '%')
    """)
    long countBySlugPrefix(@Param("slugPrefix") String slugPrefix);

    @EntityGraph(
            attributePaths = {"brand", "category"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "brand",
            "category"
    })
    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {
            "brand",
            "category",
            "images"
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
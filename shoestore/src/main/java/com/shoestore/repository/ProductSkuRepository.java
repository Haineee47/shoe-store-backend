package com.shoestore.repository;

import com.shoestore.entity.ProductSku;
import com.shoestore.repository.projection.ProductStockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    boolean existsBySkuCode(String skuCode);

    // 🌟 BỔ SUNG TẠI ĐÂY: Chặn trùng mã SKU khi cập nhật (loại trừ chính nó)
    boolean existsBySkuCodeAndIdNot(String skuCode, Long id);

    Optional<ProductSku> findById(Long id);

    List<ProductSku> findByProductId(Long productId);

    @Query("SELECT COALESCE(SUM(s.stockQuantity), 0) FROM ProductSku s WHERE s.productId = :productId AND s.status != 'DISCONTINUED'")
    Integer calculateTotalStockByProductId(@Param("productId") Long productId);

    // 🌟 BỔ SUNG ĐOẠN NÀY ĐỂ GOM TỔNG KHO CHỈ VỚI 1 CÂU QUERY DUY NHẤT
    @Query("""
        SELECT 
            s.productId as productId, 
            CAST(COALESCE(SUM(s.stockQuantity), 0) AS integer) as totalStock 
        FROM ProductSku s 
        WHERE s.productId IN :productIds 
        AND s.status <> 'DISCONTINUED' 
        GROUP BY s.productId
    """)
    List<ProductStockProjection> findStockSummaryByProductIds(@Param("productIds") List<Long> productIds);
}
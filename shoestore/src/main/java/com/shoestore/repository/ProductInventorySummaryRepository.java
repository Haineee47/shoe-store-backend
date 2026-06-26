package com.shoestore.repository;

import com.shoestore.entity.ProductInventorySummary;
import com.shoestore.repository.projection.SkuStatsProjection; // 🌟 Nhớ import Class DTO chuẩn vào đây
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInventorySummaryRepository extends JpaRepository<ProductInventorySummary, Long> {

    // ❌ XÓA BỎ DÒNG RECORD Ở ĐÂY (Vì đã có class SkuStatsProjection riêng bên ngoài rồi)

    // 🌟 SỬA: Bọc Optional<> quanh kiểu trả về
    @Query("SELECT new com.shoestore.repository.projection.SkuStatsProjection(" +
            "s.productId, " +
            "SUM(CAST(s.stockQuantity AS long)), " +
            "COUNT(CASE WHEN s.status <> 'DISCONTINUED' AND s.stockQuantity > 0 THEN 1 END)) " +
            "FROM ProductSku s " +
            "WHERE s.productId = :productId " +
            "GROUP BY s.productId")
    Optional<SkuStatsProjection> calculateSkuStatsByProductId(@Param("productId") Long productId);
}
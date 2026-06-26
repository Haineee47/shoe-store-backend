package com.shoestore.repository;

import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.common.enums.inventory.InventoryTransactionType;
import com.shoestore.dto.request.InventoryManagementRequest.InventoryFilterRequest;
import com.shoestore.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    // Tìm kiếm lịch sử biến động kho của 1 SKU cụ thể, sắp xếp từ mới nhất đến cũ nhất
    Page<InventoryTransaction> findBySkuIdOrderByCreatedAtDesc(Long skuId, Pageable pageable);

    boolean existsByReferenceTypeAndReferenceIdAndSkuId(InventoryReferenceType referenceType, Long referenceId, Long skuId);

    @Query("SELECT t FROM InventoryTransaction t WHERE " +
            "(:skuId IS NULL OR t.skuId = :skuId) AND " +
            "(:txType IS NULL OR t.transactionType = :txType) AND " +
            "(:refType IS NULL OR t.referenceType = :refType) AND " +
            "(:refId IS NULL OR t.referenceId = :refId) AND " +
            "(:createdBy IS NULL OR t.createdBy = :createdBy)")
    Page<InventoryTransaction> findByFilters(
            @Param("skuId") Long skuId,
            @Param("txType") InventoryTransactionType txType,
            @Param("refType") InventoryReferenceType refType,
            @Param("refId") Long refId,
            @Param("createdBy") Long createdBy,
            Pageable pageable
    );

    /**
     * 🎯 VỊ TRÍ ĐÚNG: Nơi thực thi HQL để tìm kiếm và phân trang lịch sử thẻ kho
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE " +
            "(:#{#filter.skuId} IS NULL OR t.skuId = :#{#filter.skuId}) AND " +
            "(:#{#filter.transactionType} IS NULL OR t.transactionType = :#{#filter.transactionType})")
    Page<InventoryTransaction> findAllByFilter(@Param("filter") InventoryFilterRequest filter, Pageable pageable);
}
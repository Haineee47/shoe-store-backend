package com.shoestore.entity;

import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.common.enums.inventory.InventoryTransactionType;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_transactions",
        // 🌟 SỬA TẠI ĐÂY: Thêm Tuyến phòng thủ vật lý chống trùng lặp dữ liệu trên DB
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_ref_sku",
                        columnNames = {"reference_type", "reference_id", "sku_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_id", nullable = false)
    private Long skuId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 50)
    private InventoryReferenceType referenceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private InventoryTransactionType transactionType;

    // 🌟 LƯU Ý: Trường này bắt buộc phải NOT NULL để Unique Constraint hoạt động chính xác trên mọi DB
    // (Bởi vì trong SQL chuẩn, giá trị NULL không được tính vào ràng buộc UNIQUE trùng lặp)
    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(nullable = false)
    private String actorName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
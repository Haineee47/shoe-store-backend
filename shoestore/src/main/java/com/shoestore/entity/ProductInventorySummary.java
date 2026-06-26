package com.shoestore.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventorySummary {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "total_stock", nullable = false)
    @Builder.Default
    private Integer totalStock = 0;

    @Column(name = "active_sku_count", nullable = false)
    @Builder.Default
    private Integer activeSkuCount = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        // 🌟 BẢO VỆ PORTABLE: Trình điều khiển Java tự lấy thời gian máy chủ ứng dụng, không phụ thuộc vào hàm SQL cụ thể
        this.updatedAt = LocalDateTime.now();
    }
}
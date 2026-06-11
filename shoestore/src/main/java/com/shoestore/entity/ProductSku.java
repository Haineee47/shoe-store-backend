package com.shoestore.entity;

import com.shoestore.common.enums.product.SkuStatus;
import com.shoestore.entity.base.BaseEntity;
import com.shoestore.exception.BusinessException;
import com.shoestore.common.enums.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "product_skus",
        uniqueConstraints = { @UniqueConstraint(name = "uk_sku_code", columnNames = "sku_code") },
        indexes = { @Index(name = "idx_sku_product", columnList = "product_id") }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "sku_code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String skuCode;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Column(nullable = false)
    private Integer weight;

    private Integer length;
    private Integer width;
    private Integer height;

    @Column(length = 500)
    private String skuImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkuStatus status; // 🌟 Không gán cứng Builder.Default nữa

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sku_product"))
    private Product product;

    // 🌟 FIX ĐIỂM 5: Tự động tính toán trạng thái Status chuẩn xác trước khi lưu vào Database
    @PrePersist
    @PreUpdate
    public void validateAndSetStatus() {

        // Nếu SKU đã bị ngừng kinh doanh thì giữ nguyên
        if (this.status == SkuStatus.DISCONTINUED) {
            return;
        }

        if (this.stockQuantity == null || this.stockQuantity <= 0) {
            this.stockQuantity = 0;
            this.status = SkuStatus.OUT_OF_STOCK;
        } else {
            this.status = SkuStatus.ACTIVE;
        }
    }

    // --- Thiết kế Đóng gói bảo vệ nghiệp vụ Tồn kho ---
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng cộng kho phải lớn hơn 0");
        }
        this.stockQuantity += quantity;
        validateAndSetStatus();
        if (this.product != null) {
            this.product.recalculateTotalStock();
        }
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trừ kho phải lớn hơn 0");
        }
        if (this.stockQuantity - quantity < 0) {
            throw new BusinessException(ErrorCode.SKU_STOCK_INSUFFICIENT);
        }
        this.stockQuantity -= quantity;
        validateAndSetStatus();
        if (this.product != null) {
            this.product.recalculateTotalStock();
        }
    }

    public void updateStock(Integer newStock) {

        this.stockQuantity =
                newStock == null
                        ? 0
                        : newStock;

        if (this.status == SkuStatus.DISCONTINUED) {
            return;
        }

        if (this.stockQuantity <= 0) {
            this.status = SkuStatus.OUT_OF_STOCK;
        } else {
            this.status = SkuStatus.ACTIVE;
        }
    }

    public void discontinue() {

        this.stockQuantity = 0;

        this.status = SkuStatus.DISCONTINUED;
    }
}
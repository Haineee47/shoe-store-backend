package com.shoestore.entity;

import com.shoestore.common.enums.product.SkuStatus;
import com.shoestore.domain.inventory.StockChangeResult;
import com.shoestore.entity.base.BaseEntity;
import com.shoestore.exception.BusinessException;
import com.shoestore.common.enums.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "product_skus",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sku_code", columnNames = "sku_code"),
                @UniqueConstraint(
                        name = "uk_product_variant",
                        columnNames = {"product_id", "color", "size"}
                )
        }
)
@org.hibernate.annotations.Check(constraints = "quantity_on_hand >= 0 AND low_stock_threshold >= 0 AND weight > 0")
@Getter
@NoArgsConstructor
public class ProductSku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true, length = 50)
    private String skuCode;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "quantity_on_hand", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "low_stock_threshold", nullable = false)
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
    private SkuStatus status;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 🌟 CUSTOM BUILDER TO ENFORCE INVARIANTS ON CREATION
    @Builder
    public ProductSku(Long id, String skuCode, String size, String color, BigDecimal costPrice,
                      BigDecimal sellingPrice, Integer stockQuantity, Integer lowStockThreshold,
                      Integer weight, Integer length, Integer width, Integer height,
                      String skuImageUrl, SkuStatus status, Long productId) {
        this.id = id;
        this.skuCode = skuCode != null ? skuCode.trim().toUpperCase() : null;
        this.size = size != null ? size.trim().toUpperCase() : null;
        this.color = color != null ? color.trim().toUpperCase() : null;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.lowStockThreshold = lowStockThreshold != null ? lowStockThreshold : 5;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.skuImageUrl = skuImageUrl != null ? skuImageUrl.trim() : null;
        this.status = status;
        this.productId = productId;
    }

    // --- CÁC HÀM NGHIỆP VỤ THUẦN KHIẾT (GIỮ NGUYÊN) ---
    public StockChangeResult increaseStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Số lượng cộng kho phải lớn hơn 0");
        if (this.status == SkuStatus.DISCONTINUED) throw new BusinessException(ErrorCode.SKU_DISCONTINUED);

        int before = this.stockQuantity;
        this.stockQuantity += amount;
        if (this.status == SkuStatus.OUT_OF_STOCK) this.status = SkuStatus.ACTIVE;
        return new StockChangeResult(before, this.stockQuantity, amount);
    }

    public StockChangeResult decreaseStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Số lượng trừ kho phải lớn hơn 0");
        if (this.stockQuantity - amount < 0) throw new BusinessException(ErrorCode.SKU_STOCK_INSUFFICIENT);

        int before = this.stockQuantity;
        this.stockQuantity -= amount;
        if (this.stockQuantity == 0 && this.status != SkuStatus.DISCONTINUED) this.status = SkuStatus.OUT_OF_STOCK;
        return new StockChangeResult(before, this.stockQuantity, -amount);
    }

    public StockChangeResult adjustStock(int actualQuantity) {
        if (actualQuantity < 0) throw new IllegalArgumentException("Số lượng thực tế sau kiểm kê không được âm");
        if (this.status == SkuStatus.DISCONTINUED) throw new BusinessException(ErrorCode.SKU_DISCONTINUED);

        int before = this.stockQuantity;
        int delta = actualQuantity - before;
        this.stockQuantity = actualQuantity;
        this.status = this.stockQuantity == 0 ? SkuStatus.OUT_OF_STOCK : SkuStatus.ACTIVE;
        return new StockChangeResult(before, this.stockQuantity, delta);
    }

    public void discontinue() {
        this.stockQuantity = 0;
        this.status = SkuStatus.DISCONTINUED;
    }

    // --- 🌟 REFACTOR CÁC HÀM CẬP NHẬT THÔNG TIN (DOMAIN MUTATORS) ---

    public void changeSkuCode(String newSkuCode) {
        if (newSkuCode == null || newSkuCode.trim().isBlank()) {
            throw new IllegalArgumentException("Mã SKU không được để trống");
        }
        // Ép invariant bắt buộc lưu chữ in hoa và không chứa khoảng trắng thừa
        this.skuCode = newSkuCode.trim().toUpperCase();
    }

    public void updateMetadata(
            String size, String color, BigDecimal costPrice, BigDecimal sellingPrice,
            Integer lowStockThreshold, Integer weight, Integer length, Integer width, Integer height,
            String skuImageUrl
    ) {
        if (size == null || size.trim().isBlank() || color == null || color.trim().isBlank()) {
            throw new BusinessException(ErrorCode.SKU_METADATA_REQUIRED);
        }

        // Thực hiện chuẩn hóa Invariant ngay tại cửa ngõ của thực thể
        this.size = size.trim().toUpperCase();
        this.color = color.trim().toUpperCase();

        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.lowStockThreshold = lowStockThreshold;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.skuImageUrl = skuImageUrl != null ? skuImageUrl.trim() : null;

        // Kích hoạt kiểm tra nghiệp vụ chéo sau khi map dữ liệu
        this.validateInvariants();
    }

    @PrePersist
    @PreUpdate
    protected void validateInvariants() {
        if (this.skuCode == null || this.skuCode.trim().isBlank()) {
            throw new BusinessException(ErrorCode.SKU_CODE_REQUIRED);
        }
        if (this.costPrice == null || this.costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.SKU_COST_PRICE_INVALID);
        }
        if (this.sellingPrice == null || this.sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.SKU_SELLING_PRICE_INVALID);
        }
        if (this.sellingPrice.compareTo(this.costPrice) < 0) {
            throw new BusinessException(ErrorCode.SKU_PRICE_CONFLICT);
        }
        if (this.lowStockThreshold == null || this.lowStockThreshold < 0) {
            throw new BusinessException(ErrorCode.SKU_LOW_STOCK_THRESHOLD_INVALID);
        }
        if (this.weight == null || this.weight <= 0) {
            throw new BusinessException(ErrorCode.SKU_WEIGHT_INVALID);
        }
        if (this.length == null || this.length <= 0 || this.width == null || this.width <= 0 || this.height == null || this.height <= 0) {
            throw new BusinessException(ErrorCode.SKU_DIMENSION_INVALID);
        }
    }
}
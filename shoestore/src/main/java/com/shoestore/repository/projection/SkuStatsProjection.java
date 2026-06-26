package com.shoestore.repository.projection;

public class SkuStatsProjection {
    private Long productId;
    private Long totalStock;
    private Long activeSkuCount;

    // ⚠️ BẮT BUỘC: Phải có Constructor đầy đủ tham số
    // Thứ tự tham số trong Constructor này phải khớp 100% với thứ tự bạn SELECT trong câu Query
    public SkuStatsProjection(Long productId, Long totalStock, Long activeSkuCount) {
        this.productId = productId;
        this.totalStock = totalStock;
        this.activeSkuCount = activeSkuCount;
    }

    // ⚠️ BẮT BUỘC: Có đầy đủ Getters và Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Long totalStock) {
        this.totalStock = totalStock;
    }

    public Long getActiveSkuCount() {
        return activeSkuCount;
    }

    public void setActiveSkuCount(Long activeSkuCount) {
        this.activeSkuCount = activeSkuCount;
    }
}
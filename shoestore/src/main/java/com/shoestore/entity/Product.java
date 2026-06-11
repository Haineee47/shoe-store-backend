package com.shoestore.entity;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.product.ProductStatus;
import com.shoestore.common.enums.product.SkuStatus; // 🌟 FIX ĐIỂM 1: Đã bổ sung import để tránh lỗi compile
import com.shoestore.entity.base.BaseEntity;
import com.shoestore.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        uniqueConstraints = { @UniqueConstraint(name = "uk_product_slug", columnNames = "slug") },
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_brand", columnList = "brand_id"),
                @Index(name = "idx_product_category", columnList = "category_id"),
                @Index(name = "idx_product_status", columnList = "status"),
                @Index(name = "idx_product_featured", columnList = "is_featured")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(
            nullable = false,
            unique = true,
            length = 255
    )
    private String slug;

    @Column(length = 500)
    private String shortDescription;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalStock = 0;

    @Column(length = 150)
    private String metaTitle;

    @Column(length = 255)
    private String metaDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_brand"))
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<ProductSku> skus = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    // ==========================================
    // 🛡️ INVARIANT GUARD & BUSINESS LOGIC METHODS
    // ==========================================

    /**
     * Hàm kiểm tra tính toàn vẹn (Invariant) của Product
     */
    private void validateInvariants() {
        validateActiveProduct();
    }

    /**
     * Thay đổi trạng thái Product một cách chủ động (Thay vì dùng setter trực tiếp)
     */
    public void updateStatus(ProductStatus newStatus) {
        this.status = newStatus;
        validateInvariants(); // Chặn lỗi khi ACTIVE mà không có SKU
    }

    public void recalculateTotalStock() {
        this.totalStock = this.skus.stream()
                .mapToInt(ProductSku::getStockQuantity)
                .sum();
    }

    /**
     * Thêm lẻ 1 SKU
     */
    public void addSku(ProductSku sku) {
        skus.add(sku);
        sku.setProduct(this);
        recalculateTotalStock();
    }

    /**
     * Xóa lẻ 1 SKU khỏi danh sách (Dùng cho API delete lẻ SKU)
     */
    public void removeSku(ProductSku sku) {
        this.skus.remove(sku);
        sku.setProduct(null);
        recalculateTotalStock();
        validateInvariants(); // Chặn lỗi nếu là SKU cuối cùng bị xóa khi Product đang ACTIVE
    }

    /**
     * Cập nhật toàn bộ danh sách SKUs (Dùng cho API updateSkus)
     */
    public void updateSkus(List<ProductSku> newSkus) {
        // Clear danh sách cũ để kích hoạt orphanRemoval = true
        this.skus.clear();
        if (newSkus != null) {
            newSkus.forEach(sku -> {
                sku.setProduct(this);
                this.skus.add(sku);
            });
        }
        recalculateTotalStock();
        validateInvariants(); // Chặn lỗi nếu request truyền lên list rỗng khi Product đang ACTIVE
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = ProductStatus.ARCHIVED;
        // Khi xóa (soft delete), trạng thái chuyển sang ARCHIVED nên không vi phạm invariant của ACTIVE nữa
    }

    public boolean canBeDeleted() {
        return this.deletedAt == null;
    }

    public void validateActiveProduct() {

        if (this.status != ProductStatus.ACTIVE) {
            return;
        }

        boolean hasActiveSku =
                this.skus != null
                        &&
                        this.skus.stream()
                                .anyMatch(
                                        sku -> sku.getStatus() == SkuStatus.ACTIVE
                                );

        if (!hasActiveSku) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_HAS_NO_ACTIVE_SKU
            );
        }
    }

    public void setPrimaryImage(ProductImage targetImage) {
        if (targetImage == null) return;

        // 1. Hạ tất cả các ảnh khác xuống làm ảnh phụ
        this.images.forEach(img -> img.setPrimary(false));

        // 2. Kích hoạt ảnh được chọn thành ảnh chính
        targetImage.setPrimary(true);

        // 3. Đồng bộ hóa tuyệt đối vào thumbnailUrl của Product cha
        this.thumbnailUrl = targetImage.getImageUrl();
    }

}
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

    @Column(name = "is_featured", nullable = false, columnDefinition = "BIT DEFAULT 0") // Hoặc BOOLEAN DEFAULT FALSE tùy DB
    @Builder.Default
    private Boolean isFeatured = false;

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
    private List<ProductImage> images = new ArrayList<>();

    // ==========================================
    // 🛡️ INVARIANT GUARD & BUSINESS LOGIC METHODS
    // ==========================================


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



    public void setPrimaryImage(ProductImage targetImage) {
        if (targetImage == null) return;

        // 1. Hạ tất cả các ảnh khác xuống làm ảnh phụ
        this.images.forEach(img -> img.setPrimary(false));

        // 2. Kích hoạt ảnh được chọn thành ảnh chính
        targetImage.setPrimary(true);

        // 3. Đồng bộ hóa tuyệt đối vào thumbnailUrl của Product cha
        this.thumbnailUrl = targetImage.getImageUrl();
    }

    // Thêm vào bên trong class Product.java
    public void validateActiveProduct() {
        if (this.name == null || this.name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_NAME);
        }
        if (this.thumbnailUrl == null || this.thumbnailUrl.isBlank()) {
            throw new BusinessException(ErrorCode.PRODUCT_MISSING_THUMBNAIL);
        }
        // Thêm các điều kiện validate cấu hình khác của riêng thực thể Product nếu cần
    }

    public void archive() {
        this.status = ProductStatus.ARCHIVED;
    }

}
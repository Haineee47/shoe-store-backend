package com.shoestore.specification;

import com.shoestore.common.enums.product.ProductStatus;
import com.shoestore.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    /**
     * Chỉ lấy sản phẩm chưa bị soft delete
     */
    public static Specification<Product> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }

    /**
     * Tìm theo tên sản phẩm
     */
    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            // 🌟 Đưa vào trong này thì 'cb' mới hợp lệ
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String searchKeyword = "%" + keyword.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), searchKeyword),
                    cb.like(cb.lower(root.get("slug")), searchKeyword)
            );
        };
    }

    /**
     * Lọc theo Brand
     */
    public static Specification<Product> hasBrand(Long brandId) {
        return (root, query, cb) -> {
            if (brandId == null) {
                return cb.conjunction(); // 🌟 Thay đổi: Không trả về null nữa
            }
            return cb.equal(root.get("brand").get("id"), brandId);
        };
    }

    /**
     * Lọc theo Category
     */
    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction(); // 🌟 Thay đổi: Không trả về null nữa
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    /**
     * Lọc theo trạng thái Product
     */
    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction(); // 🌟 Thay đổi: Không trả về null nữa
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Lọc sản phẩm nổi bật
     */
    public static Specification<Product> isFeatured(Boolean featured) {
        return (root, query, cb) -> {
            if (featured == null) {
                return cb.conjunction(); // 🌟 Thay đổi: Không trả về null nữa
            }
            return cb.equal(root.get("isFeatured"), featured);
        };
    }

    /**
     * Lọc theo Brand + Category
     */


    public static Specification<Product> activeOnly() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), ProductStatus.ACTIVE);
    }

}
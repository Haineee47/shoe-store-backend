package com.shoestore.mapper;

import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.entity.Product;
import com.shoestore.entity.ProductImage;
import com.shoestore.entity.ProductSku;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product, List<ProductSku> skusInDb, Integer calculatedTotalStock) {
        if (product == null) return null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .thumbnailUrl(product.getThumbnailUrl())
                .status(product.getStatus())
                .isFeatured(product.getIsFeatured() != null ? product.getIsFeatured() : false)

                // 🌟 SỬA TẠI ĐÂY: Lấy giá trị tính toán động truyền từ Service vào, không dùng product.getTotalStock()
                .totalStock(calculatedTotalStock != null ? calculatedTotalStock : 0)

                .metaTitle(product.getMetaTitle())
                .metaDescription(product.getMetaDescription())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())

                // 🌟 SỬA TẠI ĐÂY: Ánh xạ từ danh sách SKU độc lập do Service truyền vào, ngắt hoàn toàn product.getSkus()
                .skus(skusInDb != null ? skusInDb.stream().map(this::toSkuResponse).collect(Collectors.toList()) : new ArrayList<>())

                // (Giữ nguyên) Bản thân ảnh phụ vẫn nằm trong Aggregate Product nên vẫn dùng trực tiếp được
                .images(product.getImages() != null ? product.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public ProductResponse.SkuResponse toSkuResponse(ProductSku sku) {
        if (sku == null) return null;
        return ProductResponse.SkuResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .size(sku.getSize())
                .color(sku.getColor())
                .costPrice(sku.getCostPrice()) // Chỉ dùng cho luồng Admin
                .sellingPrice(sku.getSellingPrice())
                .stockQuantity(sku.getStockQuantity())
                .lowStockThreshold(sku.getLowStockThreshold())
                .weight(sku.getWeight())
                .length(sku.getLength())
                .width(sku.getWidth())
                .height(sku.getHeight())
                .skuImageUrl(sku.getSkuImageUrl())
                .status(sku.getStatus())
                .version(sku.getVersion())
                .build();
    }

    private ProductResponse.ImageResponse toImageResponse(ProductImage img) {
        if (img == null) return null;
        return ProductResponse.ImageResponse.builder()
                .id(img.getId())
                .imageUrl(img.getImageUrl())
                .publicId(img.getPublicId())
                .sortOrder(img.getSortOrder())
                .isPrimary(img.isPrimary())
                .build();
    }

    public ProductSummaryResponse toSummaryResponse(Product product, Integer calculatedTotalStock) {
        if (product == null) {
            return null;
        }

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .status(product.getStatus())
                .isFeatured(product.getIsFeatured() != null ? product.getIsFeatured() : false)

                // 🌟 FIX TẠI ĐÂY: Dùng giá trị truyền vào thay vì gọi từ entity Product
                .totalStock(calculatedTotalStock != null ? calculatedTotalStock : 0)

                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
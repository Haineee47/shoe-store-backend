package com.shoestore.mapper;

import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.entity.Product;
import com.shoestore.entity.ProductImage;
import com.shoestore.entity.ProductSku;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .thumbnailUrl(product.getThumbnailUrl())
                .status(product.getStatus())
                .isFeatured(product.isFeatured())
                .totalStock(product.getTotalStock())
                .metaTitle(product.getMetaTitle())
                .metaDescription(product.getMetaDescription())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .skus(product.getSkus() != null ? product.getSkus().stream().map(this::toSkuResponse).collect(Collectors.toList()) : null)
                .images(product.getImages() != null ? product.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) : null)
                .build();
    }

    private ProductResponse.SkuResponse toSkuResponse(ProductSku sku) {
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

    public ProductSummaryResponse toSummaryResponse(Product product) {

        if (product == null) {
            return null;
        }

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .status(product.getStatus())
                .isFeatured(product.isFeatured())
                .totalStock(product.getTotalStock())
                .brandId(
                        product.getBrand() != null
                                ? product.getBrand().getId()
                                : null
                )
                .brandName(
                        product.getBrand() != null
                                ? product.getBrand().getName()
                                : null
                )
                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory().getId()
                                : null
                )
                .categoryName(
                        product.getCategory() != null
                                ? product.getCategory().getName()
                                : null
                )
                .createdAt(product.getCreatedAt())
                .build();
    }
}
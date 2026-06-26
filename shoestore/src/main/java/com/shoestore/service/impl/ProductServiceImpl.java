package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.product.ProductStatus;
import com.shoestore.domain.event.ProductArchivedEvent;
import com.shoestore.domain.event.ProductCreatedEvent;
import com.shoestore.domain.event.ProductDeletedEvent;
import com.shoestore.dto.request.productManagementRequest.*;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.entity.*;
import com.shoestore.exception.BusinessException;
import com.shoestore.mapper.ProductMapper;
import com.shoestore.repository.*;
import com.shoestore.service.CloudinaryService;
import com.shoestore.service.ProductService;
import com.shoestore.service.ProductSkuService;
import com.shoestore.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher; // 🌟 Đã xóa dòng import trùng lặp
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductInventorySummaryRepository productInventorySummaryRepository; // 🌟 PHASE C1: Inject bảng tổng hợp kho O(1)
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final ProductSkuService productSkuService;
    private final ApplicationEventPublisher eventPublisher; // 🌟 PHASE C2: Phát tín hiệu bất đồng bộ

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String normalizedName = normalize(request.getName());
        if (productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(normalizedName)) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String finalSlug = generateUniqueSlug(normalizedName);

        // 🌟 THAY ĐỔI: Thêm .status(ProductStatus.DRAFT) vì sản phẩm mới tạo chưa có SKU
        Product product = Product.builder()
                .name(normalizedName)
                .slug(finalSlug)
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .isFeatured(request.getFeatured())
                .metaTitle(request.getMetaTitle() != null && !request.getMetaTitle().isBlank() ? request.getMetaTitle().trim() : normalizedName)
                .metaDescription(request.getMetaDescription() != null ? request.getMetaDescription().trim() : null)
                .brand(brand)
                .category(category)
                .status(ProductStatus.DRAFT)
                .build();

        if (request.getGalleryImages() != null) {
            int order = 1;
            for (ProductImageRequest imgReq : request.getGalleryImages()) {
                ProductImage image = ProductImage.builder()
                        .imageUrl(imgReq.getImageUrl().trim())
                        .publicId(imgReq.getPublicId().trim())
                        .sortOrder(imgReq.getSortOrder() != null ? imgReq.getSortOrder() : order++)
                        .isPrimary(false)
                        .build();
                product.addImage(image);
            }
        }

        product.validateActiveProduct();
        Product savedProduct = productRepository.save(product);

        // 🌟 ĐÂY LÀ ĐOẠN THAY ĐỔI CHÍNH: Loại bỏ khối try-catch gọi SkuService cũ
        // Bắn sự kiện ứng dụng để tầng Handler/Listener nhận và xử lý tiếp (hoặc chờ API SKU)
        eventPublisher.publishEvent(new ProductCreatedEvent(this, savedProduct.getId()));

        log.info("🎯 Tạo thành công sản phẩm cha [{}] độc lập hoàn toàn. Chờ cấu hình SKU.", savedProduct.getName());

        // Vì sản phẩm vừa tạo chưa có SKU nào, truyền vào List trống và tổng kho = 0
        return productMapper.toResponse(savedProduct, Collections.emptyList(), 0);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findWithDetailsByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        validateUpdateRequest(product, request);
        updateBasicInformation(product, request);
        updateImages(product, request);



        product.validateActiveProduct();

        try {
            Product saved = productRepository.save(product);
            List<ProductSku> updatedSkus = productSkuService.getSkusByProductId(productId);

            // 🌟 PHASE C1 ĐÃ SỬA: Lấy dữ liệu O(1) từ Projection, loại bỏ triệt để productSkuRepository thô cũ
            Integer updatedTotalStock = productInventorySummaryRepository.findById(productId)
                    .map(ProductInventorySummary::getTotalStock)
                    .orElse(0);

            return productMapper.toResponse(saved, updatedSkus, updatedTotalStock);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.PRODUCT_CONCURRENT_MODIFICATION);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.isDeleted()) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }

        validateProductCanBeDeleted(product);

        product.softDelete();
        productRepository.save(product);

        // 🌟 CHỈNH SỬA CHUẨN HÓA: Thay thế Event cũ sang Event đúng ngữ nghĩa xóa mềm
        eventPublisher.publishEvent(new ProductDeletedEvent(product.getId()));

        log.info("🗑️ Đã soft-delete Product id={} và phát tín hiệu ProductDeletedEvent", product.getId());
    }

    @Override
    @Transactional
    public void archiveProduct(Long productId) {
        log.info("📦 [Product Domain] Tiến hành đóng lưu trữ sản phẩm ID: [{}]", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.archive();
        productRepository.save(product);

        // 🌟 GIỮ NGUYÊN: Chỉ bắn ArchivedEvent khi sản phẩm thực sự chuyển trạng thái kinh doanh sang Archive
        eventPublisher.publishEvent(new ProductArchivedEvent(product.getId()));

        log.info("🚀 [Event Emitted] Đã phát đi tín hiệu ProductArchivedEvent cho hệ thống.");
    }

    // --- CÁC HÀM PRIVATE BỔ TRỢ QUẢN LÝ NỘI BỘ GIỮ NGUYÊN ---
    private void validateUpdateRequest(Product product, UpdateProductRequest request) {
        String normalizedName = normalize(request.getName());
        if (productRepository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(normalizedName, product.getId())) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }
        brandRepository.findById(request.getBrandId()).orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private void updateBasicInformation(Product product, UpdateProductRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId()).orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        String normalizedName = normalize(request.getName());

        if (!product.getName().equals(normalizedName)) {
            product.setSlug(generateUniqueSlug(normalizedName));
        }

        product.setName(normalizedName);
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setIsFeatured(request.getFeatured() != null ? request.getFeatured() : false);
        product.setMetaTitle(request.getMetaTitle() != null && !request.getMetaTitle().isBlank() ? request.getMetaTitle().trim() : normalizedName);
        product.setMetaDescription(request.getMetaDescription() != null && !request.getMetaDescription().isBlank() ? request.getMetaDescription().trim() : null);
        product.setBrand(brand);
        product.setCategory(category);
    }

    private void updateImages(Product product, UpdateProductRequest request) {
        if (request.getImages() == null) return;
        Map<Long, ProductImage> existingImageMap = product.getImages() != null
                ? product.getImages().stream().filter(img -> img.getId() != null).collect(Collectors.toMap(ProductImage::getId, Function.identity(), (e, r) -> e))
                : new HashMap<>();

        Set<Long> requestImageIds = new HashSet<>();
        ProductImage primaryImageTarget = null;

        for (ProductImageRequest imgReq : request.getImages()) {
            if (imgReq.getId() != null && existingImageMap.containsKey(imgReq.getId())) {
                ProductImage image = existingImageMap.get(imgReq.getId());
                image.setImageUrl(imgReq.getImageUrl().trim());
                image.setPublicId(imgReq.getPublicId().trim());
                image.setSortOrder(imgReq.getSortOrder() == null ? 0 : imgReq.getSortOrder());
                requestImageIds.add(imgReq.getId());
                if (imgReq.getPrimary()) primaryImageTarget = image;
            } else {
                ProductImage newImage = ProductImage.builder()
                        .imageUrl(imgReq.getImageUrl().trim())
                        .publicId(imgReq.getPublicId().trim())
                        .sortOrder(imgReq.getSortOrder() == null ? 0 : imgReq.getSortOrder())
                        .isPrimary(false)
                        .build();
                product.addImage(newImage);
                if (imgReq.getPrimary()) primaryImageTarget = newImage;
            }
        }

        List<ProductImage> imagesToRemove = product.getImages() != null
                ? product.getImages().stream().filter(img -> img.getId() != null && !requestImageIds.contains(img.getId())).toList()
                : List.of();

        for (ProductImage img : imagesToRemove) {
            if (img.getPublicId() != null && !img.getPublicId().isBlank()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            cloudinaryService.deleteImage(img.getPublicId());
                        } catch (Exception e) {
                            log.error("❌ Lỗi giải phóng Cloudinary: {}", img.getPublicId(), e);
                        }
                    }
                });
            }
        }

        if (product.getImages() != null) {
            product.getImages().removeIf(img -> img.getId() != null && !requestImageIds.contains(img.getId()));
        }
        if (primaryImageTarget != null) {
            product.setPrimaryImage(primaryImageTarget);
        }
    }

    private void validateProductCanBeDeleted(Product product) {
        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }
    }

    private String generateUniqueSlug(String normalizedName) {
        String baseSlug = SlugUtils.makeSlug(normalizedName);
        String slug = baseSlug;
        int suffix = 2;
        while (productRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug = baseSlug + "-" + suffix++;
        }
        return slug;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }
}
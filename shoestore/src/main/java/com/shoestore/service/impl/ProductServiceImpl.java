package com.shoestore.service.impl;

import com.shoestore.common.enums.product.ProductStatus;
import com.shoestore.common.enums.product.SkuStatus;
import com.shoestore.dto.request.productManagementRequest.*;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.mapper.ProductMapper;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.entity.*;
import com.shoestore.common.enums.ErrorCode;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.*;
import com.shoestore.service.CloudinaryService;
import com.shoestore.service.ProductService;
import com.shoestore.specification.ProductSpecification;
import com.shoestore.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;


    @Override
    @Transactional // Bảo toàn tính ACID hệ thống
    public ProductResponse createProduct(CreateProductRequest request) {

        // 1. Chặn trùng tên sản phẩm (Case-Insensitive) tránh rác dữ liệu DB
        String normalizedName =
                normalize(request.getName());
        if (productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(normalizedName)) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }

        // 2. 🌟 FIX ĐIỂM 4: Chặn đứng trùng mã SKU ngay trong cùng một Request Payload gửi lên bằng cấu trúc Set
        Set<String> skuCodesInRequest = new HashSet<>();
        Set<String> variantSet = new HashSet<>();

        if (request.getSkus() == null || request.getSkus().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_HAS_NO_ACTIVE_SKU
            );
        }

        for (CreateProductRequest.SkuRequest skuReq : request.getSkus()) {

            String cleanSkuCode =
                    normalizeSku(
                            skuReq.getSkuCode()
                    );

            String variantKey =
                    normalizeVariant(
                            skuReq.getColor(),
                            skuReq.getSize()
                    );

            /*
             * DUPLICATE SKU CODE IN REQUEST
             */
            if (!skuCodesInRequest.add(cleanSkuCode)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_SKU_DUPLICATE_IN_REQUEST
                );
            }

            /*
             * DUPLICATE COLOR + SIZE
             */
            if (!variantSet.add(variantKey)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_VARIANT_DUPLICATE
                );
            }

            /*
             * GLOBAL SKU CONFLICT
             */
            if (productSkuRepository.existsBySkuCode(cleanSkuCode)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_SKU_ALREADY_EXISTS
                );
            }
        }

        // 3. Khai thác dữ liệu quan hệ liên kết
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 4. 🌟 FIX ĐIỂM 4 (BUG SLUG): Chuyển sang vòng lặp While rà soát an toàn tuyệt đối cho hạ tầng SEO
        String baseSlug = SlugUtils.makeSlug(normalizedName);
        String finalSlug = baseSlug;
        int suffix = 2;
        while (productRepository.existsBySlugAndDeletedAtIsNull(finalSlug)) {
            finalSlug = baseSlug + "-" + suffix++;
        }

        // 5. Khởi tạo thực thể cha Product
        Product product = Product.builder()
                .name(normalizedName)
                .slug(finalSlug)
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .isFeatured(request.isFeatured())
                .metaTitle(request.getMetaTitle() != null && !request.getMetaTitle().isBlank() ? request.getMetaTitle().trim() : normalizedName)
                .metaDescription(request.getMetaDescription() != null ? request.getMetaDescription().trim() : null)
                .brand(brand)
                .category(category)
                .build();

        // 6. Xử lý Album ảnh phụ (Gallery)
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

        // 7. Xử lý danh sách SKU con
        for (CreateProductRequest.SkuRequest skuReq : request.getSkus()) {
            ProductSku sku = ProductSku.builder()
                    .skuCode(normalizeSku(skuReq.getSkuCode()))
                    .size(skuReq.getSize().trim())
                    .color(skuReq.getColor().trim())
                    .costPrice(skuReq.getCostPrice())
                    .sellingPrice(skuReq.getSellingPrice())
                    .lowStockThreshold(skuReq.getLowStockThreshold())
                    .weight(skuReq.getWeight())
                    .length(skuReq.getLength())
                    .width(skuReq.getWidth())
                    .height(skuReq.getHeight())
                    .skuImageUrl(skuReq.getSkuImageUrl() != null ? skuReq.getSkuImageUrl().trim() : null)
                    .build();

            sku.updateStock(
                    skuReq.getStockQuantity()
            );

            product.addSku(sku);
        }

        // 🌟 FIX ĐIỂM 3 (BUG LOGIC TOTAL STOCK): Gọi tính toán sau khi toàn bộ mảng SKU đã được Map đầy đủ vào bộ nhớ tạm
        product.recalculateTotalStock();
        product.validateActiveProduct();

        // 8. Lưu thông tin xuống Database thông qua cơ chế Cascade
        try {

            Product savedProduct = productRepository.save(product);

            log.info(
                    "🎯 Tạo thành công sản phẩm phức hợp [{}], Mã Slug: [{}], Tổng kho: [{}]",
                    savedProduct.getName(),
                    savedProduct.getSlug(),
                    savedProduct.getTotalStock()
            );

            return productMapper.toResponse(savedProduct);

        } catch (DataIntegrityViolationException e) {

            log.warn(
                    "Duplicate SKU detected by database constraint when creating product"
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_SKU_ALREADY_EXISTS
            );
        }
    }

    @Override
    @Transactional(readOnly = true) // Tối ưu hiệu năng đọc (Read-Only) cho Hibernate
    public ProductResponse getProductDetail(Long id) {
        log.info("🔍 Tiến hành truy vấn chi tiết sản phẩm ID: [{}]", id);

        Product product = productRepository
                .findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
                ); // Thêm mã lỗi này vào ErrorCode nếu chưa có

        return productMapper.toResponse(product);
    }

    // 🌟 BƯỚC 2: LẤY DANH SÁCH SẢN PHẨM PHÂN TRANG (LIST API)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductList(ProductFilterRequest filter, Pageable pageable) {
        log.info("📋 Truy vấn danh sách sản phẩm phân trang: Page[{}], Size[{}]", pageable.getPageNumber(), pageable.getPageSize());

        // Tạo Specification rỗng nhưng luôn ngầm định kiểm tra dữ liệu chưa xóa mềm
        // (Hoặc đơn giản là viết một Query Method lùng các record deletedAt IS NULL)
        // Để chuẩn bị cho Bước 3 (Search/Filter), ta truyền Specification nạp sẵn điều kiện chưa xóa:
        Specification<Product> spec =
                Specification.where(
                                ProductSpecification.notDeleted()
                        )
                        .and(
                                ProductSpecification.hasKeyword(
                                        filter.getKeyword()
                                )
                        )
                        .and(
                                ProductSpecification.hasBrand(
                                        filter.getBrandId()
                                )
                        )
                        .and(
                                ProductSpecification.hasCategory(
                                        filter.getCategoryId()
                                )
                        )
                        .and(
                                ProductSpecification.hasStatus(
                                        filter.getStatus()
                                )
                        )
                        .and(
                                ProductSpecification.isFeatured(
                                        filter.getFeatured()
                                )
                        );

        Page<Product> productPage = productRepository.findAll(spec, pageable);



        // Ánh xạ tập dữ liệu dầy sang dạng rút gọn nhẹ nhàng (Summary) thông qua Mapper
        return productPage.map(productMapper::toSummaryResponse);
    }

    private String normalize(String value) {
        return value == null
                ? null
                : value.trim().replaceAll("\\s+", " ");
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(
            Long productId,
            UpdateProductRequest request
    ) {

        Product product =
                productRepository
                        .findWithDetailsByIdAndDeletedAtIsNull(productId)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.PRODUCT_NOT_FOUND
                                )
                        );
        validateUpdateRequest(product, request);

        updateBasicInformation(product, request);

        updateImages(product, request);

        updateSkus(product, request);

        product.recalculateTotalStock();

        product.validateActiveProduct();

        try {

            Product saved =
                    productRepository.save(product);

            return productMapper.toResponse(saved);

        } catch (DataIntegrityViolationException e) {

            log.warn(
                    "Concurrent SKU update conflict for product {}",
                    productId
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_CONCURRENT_MODIFICATION
            );
        }
    }

    private void validateUpdateRequest(
            Product product,
            UpdateProductRequest request
    ) {

        String normalizedName =
                normalize(request.getName());

        if (
                productRepository
                        .existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
                                normalizedName,
                                product.getId()
                        )
        ) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_NAME_ALREADY_EXISTS
            );
        }

        brandRepository.findById(request.getBrandId())
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.BRAND_NOT_FOUND
                        )
                );

        categoryRepository.findById(
                        request.getCategoryId()
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );
    }

    private void updateBasicInformation(
            Product product,
            UpdateProductRequest request
    ) {

        Brand brand =
                brandRepository.findById(
                        request.getBrandId()
                ).orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.BRAND_NOT_FOUND
                        )
                );

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );

        String normalizedName =
                normalize(request.getName());

        if (!product.getName().equals(normalizedName)) {

            String baseSlug =
                    SlugUtils.makeSlug(normalizedName);

            String slug = baseSlug;
            int suffix = 2;

            while (
                    productRepository
                            .existsBySlugAndIdNotAndDeletedAtIsNull(
                                    slug,
                                    product.getId()
                            )
            ) {
                slug = baseSlug + "-" + suffix++;
            }

            product.setSlug(slug);
        }

        product.setName(normalizedName);

        product.setShortDescription(
                request.getShortDescription()
        );

        product.setDescription(
                request.getDescription()
        );

        product.setThumbnailUrl(
                request.getThumbnailUrl()
        );

        product.setFeatured(request.isFeatured());

        product.setMetaTitle(
                request.getMetaTitle() != null && !request.getMetaTitle().isBlank()
                        ? request.getMetaTitle().trim()
                        : normalizedName
        );

        product.setMetaDescription(
                request.getMetaDescription() != null && !request.getMetaDescription().isBlank()
                        ? request.getMetaDescription().trim()
                        : null
        );

        product.setBrand(brand);

        product.setCategory(category);
    }

    private void updateImages(Product product, UpdateProductRequest request) {
        if (request.getImages() == null) return;

        // 1. Khởi tạo map an toàn, lọc bỏ tuyệt đối các ảnh không có ID hoặc ID null
        Map<Long, ProductImage> existingImageMap = product.getImages() != null
                ? product.getImages().stream()
                .filter(img -> img.getId() != null)
                .collect(Collectors.toMap(ProductImage::getId, Function.identity(), (existing, replacing) -> existing))
                : new HashMap<>();

        Set<Long> requestImageIds = new HashSet<>();
        ProductImage primaryImageTarget = null;

        // 2. Duyệt qua danh sách ảnh từ Request gửi lên
        for (ProductImageRequest imgReq : request.getImages()) {
            // Tình huống A: Cập nhật ảnh ĐÃ TỒN TẠI trong Database
            if (imgReq.getId() != null && existingImageMap.containsKey(imgReq.getId())) {
                ProductImage image = existingImageMap.get(imgReq.getId());

                image.setImageUrl(imgReq.getImageUrl().trim());
                image.setPublicId(imgReq.getPublicId().trim());
                image.setSortOrder(imgReq.getSortOrder() == null ? 0 : imgReq.getSortOrder());

                requestImageIds.add(imgReq.getId());
                if (imgReq.isPrimary()) primaryImageTarget = image;

            } else {
                // Tình huống B: Ảnh mới hoàn toàn (id == null) HOẶC id truyền lên không nằm trong DB của sản phẩm này
                ProductImage newImage = ProductImage.builder()
                        .imageUrl(imgReq.getImageUrl().trim())
                        .publicId(imgReq.getPublicId().trim())
                        .sortOrder(imgReq.getSortOrder() == null ? 0 : imgReq.getSortOrder())
                        .isPrimary(false)
                        .build();

                product.addImage(newImage);
                if (imgReq.isPrimary()) primaryImageTarget = newImage;
            }
        }

        // 3. Xác định các ảnh cũ bị xóa bỏ (Có trong DB nhưng không được gửi lên trong Request)
        List<ProductImage> imagesToRemove = product.getImages() != null
                ? product.getImages().stream()
                .filter(img -> img.getId() != null && !requestImageIds.contains(img.getId()))
                .toList()
                : List.of();

        // 4. Đăng ký hàng đợi xóa ảnh vật lý trên Cloudinary SAU KHI DATABASE COMMIT THÀNH CÔNG
        for (ProductImage img : imagesToRemove) {
            if (img.getPublicId() != null && !img.getPublicId().isBlank()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            cloudinaryService.deleteImage(img.getPublicId());
                            log.info("🗑️ Đã giải phóng ảnh vật lý trên Cloudinary thành công: {}", img.getPublicId());
                        } catch (Exception e) {
                            log.error("❌ Lỗi: DB đã commit nhưng không thể xóa file trên Cloudinary cho PublicID: {}", img.getPublicId(), e);
                        }
                    }
                });
            }
        }

        // 5. Cập nhật trạng thái bộ nhớ cho Hibernate sync xuống DB
        if (product.getImages() != null) {
            product.getImages().removeIf(img -> img.getId() != null && !requestImageIds.contains(img.getId()));
        }

        if (primaryImageTarget != null) {
            product.setPrimaryImage(primaryImageTarget);
        }
    }

    private void updateSkus(
            Product product,
            UpdateProductRequest request
    ) {

        if (request.getSkus() == null || request.getSkus().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_HAS_NO_ACTIVE_SKU
            );
        }

        /*
         * 1. Map SKU hiện tại
         */
        Map<Long, ProductSku> existingSkuMap =
                product.getSkus()
                        .stream()
                        .filter(sku -> sku.getId() != null)
                        .collect(
                                Collectors.toMap(
                                        ProductSku::getId,
                                        Function.identity()
                                )
                        );

        /*
         * 2. Check duplicate SKU code trong request
         */
        Set<String> skuCodeSet = new HashSet<>();

        /*
         * 3. Check duplicate variant
         */
        Set<String> variantSet = new HashSet<>();

        /*
         * 4. Track SKU còn tồn tại sau update
         */
        Set<Long> requestSkuIds = new HashSet<>();

        for (SkuUpdateRequest skuReq : request.getSkus()) {

            String skuCode =
                    normalizeSku(
                            skuReq.getSkuCode()
                    );

            String variantKey =
                    normalizeVariant(
                            skuReq.getColor(),
                            skuReq.getSize()
                    );

            /*
             * DUPLICATE SKU CODE IN REQUEST
             */
            if (!skuCodeSet.add(skuCode)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_SKU_DUPLICATE_IN_REQUEST
                );
            }

            /*
             * DUPLICATE VARIANT
             */
            if (!variantSet.add(variantKey)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_VARIANT_DUPLICATE
                );
            }

            /*
             * ==========================
             * UPDATE EXISTING SKU
             * ==========================
             */
            if (skuReq.getId() != null) {

                ProductSku sku =
                        existingSkuMap.get(
                                skuReq.getId()
                        );

                if (sku == null) {
                    throw new BusinessException(
                            ErrorCode.PRODUCT_SKU_BELONGS_TO_OTHER_PRODUCT
                    );
                }

                /*
                 * CHECK SKU CODE GLOBAL CONFLICT
                 */
                if (!sku.getSkuCode().equalsIgnoreCase(skuCode)) {

                    if (productSkuRepository.existsBySkuCode(skuCode)) {
                        throw new BusinessException(
                                ErrorCode.PRODUCT_SKU_ALREADY_EXISTS
                        );
                    }
                }

                sku.setSkuCode(skuCode);
                sku.setSize(skuReq.getSize().trim());
                sku.setColor(skuReq.getColor().trim());
                sku.setCostPrice(skuReq.getCostPrice());
                sku.setSellingPrice(skuReq.getSellingPrice());

                /*
                 * DOMAIN METHOD
                 */
                sku.updateStock(
                        skuReq.getStockQuantity()
                );

                sku.setLowStockThreshold(
                        skuReq.getLowStockThreshold()
                );

                sku.setWeight(
                        skuReq.getWeight()
                );

                sku.setLength(
                        skuReq.getLength()
                );

                sku.setWidth(
                        skuReq.getWidth()
                );

                sku.setHeight(
                        skuReq.getHeight()
                );

                sku.setSkuImageUrl(
                        skuReq.getSkuImageUrl() != null
                                ? skuReq.getSkuImageUrl().trim()
                                : null
                );

                requestSkuIds.add(
                        skuReq.getId()
                );

                continue;
            }

            /*
             * ==========================
             * CREATE NEW SKU
             * ==========================
             */
            if (productSkuRepository.existsBySkuCode(skuCode)) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_SKU_ALREADY_EXISTS
                );
            }

            ProductSku newSku =
                    ProductSku.builder()
                            .skuCode(skuCode)
                            .size(skuReq.getSize().trim())
                            .color(skuReq.getColor().trim())
                            .costPrice(skuReq.getCostPrice())
                            .sellingPrice(skuReq.getSellingPrice())
                            .lowStockThreshold(
                                    skuReq.getLowStockThreshold()
                            )
                            .weight(
                                    skuReq.getWeight()
                            )
                            .length(
                                    skuReq.getLength()
                            )
                            .width(
                                    skuReq.getWidth()
                            )
                            .height(
                                    skuReq.getHeight()
                            )
                            .skuImageUrl(
                                    skuReq.getSkuImageUrl() != null
                                            ? skuReq.getSkuImageUrl().trim()
                                            : null
                            )
                            .build();

            /*
             * DOMAIN METHOD
             */
            newSku.updateStock(
                    skuReq.getStockQuantity()
            );

            product.addSku(newSku);
        }

        /*
         * DELETE SKU KHÔNG CÒN TRONG REQUEST
         */
        product.getSkus().removeIf(
                sku ->
                        sku.getId() != null
                                &&
                                !requestSkuIds.contains(
                                        sku.getId()
                                )
        );
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product product =
                productRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.PRODUCT_NOT_FOUND
                                )
                        );

        /*
         * 1. CHECK ĐÃ BỊ XÓA CHƯA (double safety)
         */
        if (product.isDeleted()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_ALREADY_DELETED
            );
        }

        /*
         * 2. BUSINESS SAFETY CHECK (RẤT QUAN TRỌNG)
         * Nếu sau này có ORDER → chặn delete cứng logic
         */
        validateProductCanBeDeleted(product);

        /*
         * 3. SOFT DELETE SKU + IMAGE (optional explicit state sync)
         * Hibernate cascade sẽ handle delete relation,
         * nhưng ta nên explicit state để tránh bug future
         */
        for (ProductSku sku : product.getSkus()) {
            sku.discontinue();
        }

        /*
         * 4. SOFT DELETE PRODUCT
         */
        product.softDelete();

        /*
         * 5. SAVE
         */
        productRepository.save(product);

        log.info(
                "🗑️ Soft delete product success: id={}, name={}",
                product.getId(),
                product.getName()
        );
    }

    private void validateProductCanBeDeleted(Product product) {

        /*
         * CASE 1: product đã ARCHIVED rồi
         */
        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_ALREADY_DELETED
            );
        }

        /*
         * CASE 2: SKU còn ACTIVE (optional business rule)
         */
        boolean hasActiveSku =
                product.getSkus()
                        .stream()
                        .anyMatch(sku ->
                                sku.getStatus() == SkuStatus.ACTIVE
                        );

        if (hasActiveSku) {
            log.warn(
                    "⚠️ Product still has active SKUs before delete: {}",
                    product.getId()
            );
        }

        /*
         * CASE 3 (FUTURE): check order dependency
         *
         * if(orderRepository.existsByProductId(product.getId())) {
         *     throw new BusinessException(PRODUCT_HAS_ORDER);
         * }
         */
    }

    private String normalizeSku(String sku) {

        return sku == null
                ? null
                : sku.trim().toUpperCase();
    }

    private String normalizeVariant(
            String color,
            String size
    ) {
        return color.trim().toUpperCase()
                + "_"
                + size.trim().toUpperCase();
    }
}
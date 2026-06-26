package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.common.enums.product.SkuStatus;
import com.shoestore.domain.event.InventoryChangedEvent;
import com.shoestore.domain.event.SkuCreatedEvent;
import com.shoestore.dto.request.productManagementRequest.CreateSkuRequest;
import com.shoestore.dto.request.productManagementRequest.SkuUpdateRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.entity.ProductSku;
import com.shoestore.exception.BusinessException;
import com.shoestore.mapper.ProductMapper;
import com.shoestore.repository.ProductSkuRepository;
import com.shoestore.security.product.CurrentUserProvider;
import com.shoestore.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSkuServiceImpl implements ProductSkuService {

    private final ProductSkuRepository productSkuRepository;
    private final ProductMapper productMapper;
    private final CurrentUserProvider currentUserProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public List<ProductSku> createSkusForProduct(Long productId, List<CreateSkuRequest> skuRequests) {
        if (skuRequests == null || skuRequests.isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_HAS_NO_ACTIVE_SKU);
        }
        Set<String> skuCodesInRequest = new HashSet<>();
        Set<String> variantSet = new HashSet<>();

        for (CreateSkuRequest skuReq : skuRequests) {
            String cleanSkuCode = skuReq.getSkuCode().trim().toUpperCase();
            String variantKey = skuReq.getColor().trim().toUpperCase() + "_" + skuReq.getSize().trim().toUpperCase();

            if (!skuCodesInRequest.add(cleanSkuCode)) {
                throw new BusinessException(ErrorCode.PRODUCT_SKU_DUPLICATE_IN_REQUEST);
            }
            if (!variantSet.add(variantKey)) {
                throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
            }
            if (productSkuRepository.existsBySkuCode(cleanSkuCode)) {
                throw new BusinessException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
            }
        }

        List<ProductSku> skusToSave = new ArrayList<>();
        for (CreateSkuRequest skuReq : skuRequests) {
            ProductSku sku = ProductSku.builder()
                    .productId(productId)
                    .skuCode(skuReq.getSkuCode())
                    .size(skuReq.getSize())
                    .color(skuReq.getColor())
                    .costPrice(skuReq.getCostPrice())
                    .sellingPrice(skuReq.getSellingPrice())
                    .lowStockThreshold(skuReq.getLowStockThreshold())
                    .weight(skuReq.getWeight())
                    .length(skuReq.getLength())
                    .width(skuReq.getWidth())
                    .height(skuReq.getHeight())
                    .skuImageUrl(skuReq.getSkuImageUrl())
                    .status(SkuStatus.OUT_OF_STOCK)
                    .build();
            skusToSave.add(sku);
        }

        List<ProductSku> savedSkus;
        try {
            savedSkus = productSkuRepository.saveAllAndFlush(skusToSave);
        } catch (DataIntegrityViolationException ex) {
            log.error("🚨 [CONCURRENCY VIOLATION] Trùng lặp tổ hợp biến thể hoặc SKU Code khi tạo sản phẩm hàng loạt!");
            throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
        }

        Long currentActorId = currentUserProvider.getCurrentUserId();
        Map<Long, Integer> skuStockMap = new HashMap<>();
        for (ProductSku savedSku : savedSkus) {
            CreateSkuRequest matchedReq = skuRequests.stream()
                    .filter(reqSku -> reqSku.getSkuCode().trim().equalsIgnoreCase(savedSku.getSkuCode()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

            skuStockMap.put(savedSku.getId(), matchedReq.getStockQuantity());
        }

        eventPublisher.publishEvent(new SkuCreatedEvent(this, productId, skuStockMap, InventoryReferenceType.PRODUCT_CREATION, currentActorId));
        return productSkuRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ProductResponse.SkuResponse createSingleSku(Long productId, CreateSkuRequest request) {
        String inputSkuCode = request.getSkuCode().trim().toUpperCase();
        if (productSkuRepository.existsBySkuCode(inputSkuCode)) {
            throw new BusinessException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
        }

        ProductSku sku = ProductSku.builder()
                .productId(productId)
                .skuCode(request.getSkuCode())
                .size(request.getSize())
                .color(request.getColor())
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .lowStockThreshold(request.getLowStockThreshold())
                .weight(request.getWeight())
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .skuImageUrl(request.getSkuImageUrl())
                .status(SkuStatus.OUT_OF_STOCK)
                .build();

        ProductSku saved;
        try {
            saved = productSkuRepository.saveAndFlush(sku);
        } catch (DataIntegrityViolationException ex) {
            log.error("🚨 [RACE CONDITION DETECTED] Biến thể Màu + Size hoặc SKU [{}] đã được tạo bởi luồng khác!", inputSkuCode);
            throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
        }

        Map<Long, Integer> skuStockMap = Map.of(saved.getId(), request.getStockQuantity() != null ? request.getStockQuantity() : 0);

        // 🌟 THAY ĐỔI: Chuyển PRODUCT_UPDATE thành PRODUCT_CREATION vì đây là tạo mới 1 SKU đơn lẻ
        eventPublisher.publishEvent(new SkuCreatedEvent(this, productId, skuStockMap, InventoryReferenceType.PRODUCT_CREATION, currentUserProvider.getCurrentUserId()));

        return productMapper.toSkuResponse(productSkuRepository.findById(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional
    public ProductResponse.SkuResponse updateSku(Long skuId, SkuUpdateRequest request) {
        ProductSku sku = productSkuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_SKU_NOT_FOUND));

        String targetSkuCode = request.getSkuCode().trim().toUpperCase();
        if (!sku.getSkuCode().equalsIgnoreCase(targetSkuCode) && productSkuRepository.existsBySkuCode(targetSkuCode)) {
            throw new BusinessException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
        }

        sku.changeSkuCode(request.getSkuCode());
        sku.updateMetadata(
                request.getSize(), request.getColor(), request.getCostPrice(),
                request.getSellingPrice(), request.getLowStockThreshold(),
                request.getWeight(), request.getLength(), request.getWidth(), request.getHeight(),
                request.getSkuImageUrl()
        );

        try {
            ProductResponse.SkuResponse response = productMapper.toSkuResponse(productSkuRepository.saveAndFlush(sku));
            if (sku.getProductId() != null) {
                eventPublisher.publishEvent(new InventoryChangedEvent(this, sku.getProductId()));
            }
            return response;
        } catch (DataIntegrityViolationException ex) {
            log.error("🚨 [UPDATE CONFLICT] Sửa lỗi trùng lặp sang thông tin biến thể đã tồn tại dưới DB!");
            throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
        }
    }

    @Override
    @Transactional
    public void updateProductSkus(Long productId, List<SkuUpdateRequest> skuRequests) {
        if (skuRequests == null || skuRequests.isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_HAS_NO_ACTIVE_SKU);
        }

        List<ProductSku> currentSkusInDb = productSkuRepository.findByProductId(productId);
        Map<Long, ProductSku> existingSkuMap = currentSkusInDb.stream()
                .filter(sku -> sku.getId() != null)
                .collect(Collectors.toMap(ProductSku::getId, Function.identity()));

        Set<String> skuCodeSet = new HashSet<>();
        Set<String> variantSet = new HashSet<>();
        Set<Long> requestSkuIds = new HashSet<>();
        List<ProductSku> newSkusToSave = new ArrayList<>();

        for (SkuUpdateRequest skuReq : skuRequests) {
            String checkSkuCode = skuReq.getSkuCode().trim().toUpperCase();
            String checkVariantKey = skuReq.getColor().trim().toUpperCase() + "_" + skuReq.getSize().trim().toUpperCase();

            if (!skuCodeSet.add(checkSkuCode)) {
                throw new BusinessException(ErrorCode.PRODUCT_SKU_DUPLICATE_IN_REQUEST);
            }
            if (!variantSet.add(checkVariantKey)) {
                throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
            }

            if (skuReq.getId() != null) {
                ProductSku sku = existingSkuMap.get(skuReq.getId());
                if (sku == null) {
                    throw new BusinessException(ErrorCode.PRODUCT_SKU_BELONGS_TO_OTHER_PRODUCT);
                }

                if (!sku.getSkuCode().equalsIgnoreCase(checkSkuCode) && productSkuRepository.existsBySkuCode(checkSkuCode)) {
                    throw new BusinessException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
                }

                sku.changeSkuCode(skuReq.getSkuCode());
                sku.updateMetadata(
                        skuReq.getSize(), skuReq.getColor(), skuReq.getCostPrice(),
                        skuReq.getSellingPrice(), skuReq.getLowStockThreshold(),
                        skuReq.getWeight(), skuReq.getLength(), skuReq.getWidth(), skuReq.getHeight(),
                        skuReq.getSkuImageUrl()
                );

                requestSkuIds.add(sku.getId());
                continue;
            }

            if (productSkuRepository.existsBySkuCode(checkSkuCode)) {
                throw new BusinessException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
            }

            ProductSku newSku = ProductSku.builder()
                    .productId(productId)
                    .skuCode(skuReq.getSkuCode())
                    .size(skuReq.getSize())
                    .color(skuReq.getColor())
                    .costPrice(skuReq.getCostPrice())
                    .sellingPrice(skuReq.getSellingPrice())
                    .lowStockThreshold(skuReq.getLowStockThreshold())
                    .weight(skuReq.getWeight())
                    .length(skuReq.getLength())
                    .width(skuReq.getWidth())
                    .height(skuReq.getHeight())
                    .skuImageUrl(skuReq.getSkuImageUrl())
                    .status(SkuStatus.OUT_OF_STOCK)
                    .build();

            newSkusToSave.add(newSku);
        }

        Long currentActorId = currentUserProvider.getCurrentUserId();

        if (!newSkusToSave.isEmpty()) {
            List<ProductSku> savedNewSkus;
            try {
                savedNewSkus = productSkuRepository.saveAllAndFlush(newSkusToSave);
            } catch (DataIntegrityViolationException ex) {
                log.error("🚨 [CONCURRENCY VIOLATION] Trùng lặp tổ hợp biến thể khi bổ sung SKU mới vào sản phẩm!");
                throw new BusinessException(ErrorCode.PRODUCT_VARIANT_DUPLICATE);
            }

            Map<Long, Integer> skuStockMap = new HashMap<>();
            for (ProductSku savedSku : savedNewSkus) {
                SkuUpdateRequest matchedReq = skuRequests.stream()
                        .filter(reqSku -> reqSku.getId() == null && reqSku.getSkuCode().trim().equalsIgnoreCase(savedSku.getSkuCode()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

                skuStockMap.put(savedSku.getId(), matchedReq.getStockQuantity());
            }

            eventPublisher.publishEvent(new SkuCreatedEvent(this, productId, skuStockMap, InventoryReferenceType.PRODUCT_UPDATE, currentActorId));
        }

        currentSkusInDb.stream()
                .filter(sku -> sku.getId() != null && !requestSkuIds.contains(sku.getId()))
                .forEach(ProductSku::discontinue);

        try {
            productSkuRepository.saveAllAndFlush(currentSkusInDb);
            eventPublisher.publishEvent(new InventoryChangedEvent(this, productId));
        } catch (DataIntegrityViolationException ex) {
            log.error("🚨 [CONCURRENCY ERROR] Thất bại khi đóng băng các SKU thừa!");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void discontinueSku(Long skuId) {
        ProductSku sku = productSkuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_SKU_NOT_FOUND));
        sku.discontinue();
        productSkuRepository.saveAndFlush(sku);

        if (sku.getProductId() != null) {
            eventPublisher.publishEvent(new InventoryChangedEvent(this, sku.getProductId()));
        }
    }

    @Override
    @Transactional
    public void discontinueAllSkusByProductId(Long productId) {
        List<ProductSku> skus = productSkuRepository.findByProductId(productId);
        skus.forEach(ProductSku::discontinue);
        productSkuRepository.saveAllAndFlush(skus);
        eventPublisher.publishEvent(new InventoryChangedEvent(this, productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSku> getSkusByProductId(Long productId) {
        return productSkuRepository.findByProductId(productId);
    }
}
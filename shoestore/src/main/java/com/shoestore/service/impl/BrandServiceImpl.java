package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.dto.request.brandManagementRequest.CreateBrandRequest;
import com.shoestore.dto.request.brandManagementRequest.UpdateBrandRequest;
import com.shoestore.dto.response.brandManagementResponse.BrandResponse;
import com.shoestore.dto.response.brandManagementResponse.PublicBrandResponse;
import com.shoestore.entity.Brand;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.BrandRepository;
import com.shoestore.service.BrandService;
import com.shoestore.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request) {
        String normalizedName = normalizeName(request.getName());

        if (brandRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BusinessException(ErrorCode.BRAND_NAME_ALREADY_EXISTS);
        }

        String slug = StringUtils.toSlug(normalizedName);
        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.BRAND_SLUG_ALREADY_EXISTS);
        }

        Brand brand = Brand.builder()
                .name(normalizedName)
                .slug(slug)
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, UpdateBrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

        String normalizedName = normalizeName(request.getName());

        if (brandRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new BusinessException(ErrorCode.BRAND_NAME_ALREADY_EXISTS);
        }

        brand.setName(normalizedName);
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());

        if (request.getSortOrder() != null) brand.setSortOrder(request.getSortOrder());
        if (request.getIsActive() != null) brand.setActive(request.getIsActive());

        // 🌟 Chiến lược Immutable Slug: Giữ nguyên slug ban đầu để bảo vệ SEO

        return mapToResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Boolean isActive) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brand.setActive(isActive);
        brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

        // 🚨 CRITICAL TODO PHASE 2.3 - PRODUCT REFERENCE CHECK
        // Bắt buộc phải chặn kiểm tra sản phẩm trước khi xóa khi module Product hoàn thành:
        // if (productRepository.existsByBrandId(id)) {
        //     throw new BusinessException(ErrorCode.BRAND_HAS_PRODUCTS_CANNOT_DELETE);
        // }

        brandRepository.delete(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> searchBrands(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return brandRepository.findAll(pageable).map(this::mapToResponse);
        }
        return brandRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandByIdForAdmin(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return mapToResponse(brand);
    }

    // --- Public Logic APIs ---

    @Override
    @Transactional(readOnly = true)
    public List<PublicBrandResponse> getPublicBrands() {
        return brandRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::mapToPublicResponse) // 🌟 Chuyển sang mapper gọn nhẹ
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PublicBrandResponse getBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return mapToPublicResponse(brand);
    }

    // --- Helper Mappers ---

    private PublicBrandResponse mapToPublicResponse(Brand brand) {
        return PublicBrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .build();
    }

    // --- Helper Mappers ---

    private BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .sortOrder(brand.getSortOrder())
                .isActive(brand.isActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }


}
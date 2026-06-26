package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.dto.request.productManagementRequest.ProductFilterRequest;
import com.shoestore.dto.response.productManagementResponse.ProductResponse;
import com.shoestore.dto.response.productManagementResponse.ProductSummaryResponse;
import com.shoestore.entity.Product;
import com.shoestore.entity.ProductSku;
import com.shoestore.entity.ProductInventorySummary; // 🌟 BỔ SUNG: Import Entity Projection mới
import com.shoestore.exception.BusinessException;
import com.shoestore.mapper.ProductMapper;
import com.shoestore.repository.ProductRepository;
import com.shoestore.repository.ProductSkuRepository;
import com.shoestore.repository.ProductInventorySummaryRepository; // 🌟 BỔ SUNG: Import Repo Projection mới
import com.shoestore.service.CatalogReadService;
import com.shoestore.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // 🔥 Hibernate tối ưu bộ nhớ đệm (FlushMode.MANUAL)
public class CatalogReadServiceImpl implements CatalogReadService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductInventorySummaryRepository productInventorySummaryRepository; // 🌟 BỔ SUNG: Inject Repo Projection
    private final ProductMapper productMapper;

    @Override
    public ProductResponse getProductDetail(Long id) {
        log.info("📖 [Query-Side] Đọc chi tiết sản phẩm phức hợp ID: [{}]", id);

        Product product = productRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<ProductSku> skus = productSkuRepository.findByProductId(id);

        // 🌟 ĐÃ SỬA: Lấy tổng kho O(1) từ bảng Projection thay vì chạy SUM nặng dưới DB
        Integer realTotalStock = productInventorySummaryRepository.findById(id)
                .map(ProductInventorySummary::getTotalStock)
                .orElse(0);

        return productMapper.toResponse(product, skus, realTotalStock);
    }

    @Override
    public Page<ProductSummaryResponse> getProductList(ProductFilterRequest filter, Pageable pageable) {
        log.info("📋 [Query-Side] Truy vấn danh sách Catalog phân trang nhẹ với kỹ thuật Projection Mapping.");

        // 🟢 QUERY 1: Kéo danh sách sản phẩm phân trang về
        Specification<Product> spec = Specification.where(ProductSpecification.notDeleted())
                .and(ProductSpecification.hasKeyword(filter.getKeyword()))
                .and(ProductSpecification.hasBrand(filter.getBrandId()))
                .and(ProductSpecification.hasCategory(filter.getCategoryId()))
                .and(ProductSpecification.hasStatus(filter.getStatus()))
                .and(ProductSpecification.isFeatured(filter.getFeatured()));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        if (productPage.isEmpty()) {
            return Page.empty();
        }

        // 1. Thu thập nhanh toàn bộ Product IDs của trang hiện tại lên bộ nhớ RAM
        List<Long> ids = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        // 🟢 QUERY 2 MỚI: Kéo thẳng dữ liệu có sẵn từ bảng Projection (Tốc độ ánh sáng O(1) không tính toán)
        List<ProductInventorySummary> summaries = productInventorySummaryRepository.findAllById(ids);

        // 2. Chuyển đổi List Projection sang cấu trúc Map để đạt tốc độ tìm kiếm O(1) trên RAM
        Map<Long, Integer> stockMap = summaries.stream()
                .collect(Collectors.toMap(
                        ProductInventorySummary::getProductId,
                        ProductInventorySummary::getTotalStock,
                        (existing, replacing) -> existing
                ));

        // 3. Khớp nối dữ liệu mượt mà, loại bỏ triệt để việc quét index s.status hay SUM(s.quantity_on_hand)
        return productPage.map(product ->
                productMapper.toSummaryResponse(
                        product,
                        stockMap.getOrDefault(product.getId(), 0) // Trả về 0 nếu sản phẩm chưa được khởi tạo kho
                )
        );
    }
}
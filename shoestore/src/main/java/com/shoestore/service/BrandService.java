package com.shoestore.service;

import com.shoestore.dto.request.brandManagementRequest.CreateBrandRequest;
import com.shoestore.dto.request.brandManagementRequest.UpdateBrandRequest;
import com.shoestore.dto.response.brandManagementResponse.BrandResponse;
import com.shoestore.dto.response.brandManagementResponse.PublicBrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    // Admin APIs
    BrandResponse createBrand(CreateBrandRequest request);
    BrandResponse updateBrand(Long id, UpdateBrandRequest request);
    void changeStatus(Long id, Boolean isActive);
    void deleteBrand(Long id);
    Page<BrandResponse> searchBrands(String keyword, Pageable pageable);
    BrandResponse getBrandByIdForAdmin(Long id);

    // Public APIs (🌟 Đã chuyển sang dùng PublicBrandResponse để tối ưu dữ liệu)
    List<PublicBrandResponse> getPublicBrands();
    PublicBrandResponse getBrandBySlug(String slug);
}
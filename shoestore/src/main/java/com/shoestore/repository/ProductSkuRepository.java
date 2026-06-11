package com.shoestore.repository;

import com.shoestore.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    boolean existsBySkuCode(String skuCode);

    // 🌟 BỔ SUNG TẠI ĐÂY: Chặn trùng mã SKU khi cập nhật (loại trừ chính nó)
    boolean existsBySkuCodeAndIdNot(String skuCode, Long id);

    Optional<ProductSku> findById(Long id);
}
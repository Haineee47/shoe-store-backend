package com.shoestore.repository;

import com.shoestore.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findById(Long id);
}
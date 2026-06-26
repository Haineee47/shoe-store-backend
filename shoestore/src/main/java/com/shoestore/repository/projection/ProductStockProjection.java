package com.shoestore.repository.projection; // Hoặc package chứa DTO của bạn

public interface ProductStockProjection {
    Long getProductId();
    Integer getTotalStock();
}
package com.shoestore.common.enums.product;

public enum ProductStatus {
    DRAFT,      // Admin đang lên bản nháp, chưa công khai ra web
    ACTIVE,     // Đang mở bán công khai
    INACTIVE,   // Tạm thời dừng bán (Ẩn khỏi UI)
    ARCHIVED    // Đã lưu trữ (Thay cho việc xóa cứng)
}
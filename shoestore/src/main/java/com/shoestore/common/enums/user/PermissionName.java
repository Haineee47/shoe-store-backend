package com.shoestore.common.enums.user;

public enum PermissionName {
    // Quyền liên quan đến Sản phẩm (Product)
    PRODUCT_CREATE,
    PRODUCT_VIEW,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Quyền liên quan đến Danh mục (Category)
    CATEGORY_CREATE,
    CATEGORY_UPDATE,
    CATEGORY_DELETE,
    CATEGORY_VIEW,

    // 🏷️ Quyền liên quan đến Thương hiệu (Brand) - 🌟 BỔ SUNG CHO PHASE 2.2
    BRAND_CREATE,
    BRAND_VIEW,
    BRAND_UPDATE,
    BRAND_DELETE,

    // 🖼️ Quyền liên quan đến Lưu trữ/Hình ảnh (Media) - 🌟 BỔ SUNG CHO PHASE 2.25
    MEDIA_UPLOAD,
    MEDIA_DELETE,

    // 📦 Quyền liên quan đến Kho hàng (Inventory) - 🌟 BỔ SUNG PHỤC VỤ SCALE SAU NÀY
    INVENTORY_VIEW,
    INVENTORY_UPDATE,

    // 🛒 Quyền liên quan đến Đơn hàng (Order) - 🌟 BỔ SUNG THÊM CREATE/DELETE CHO ĐỦ BỘ
    ORDER_CREATE,     // Admin/Staff có thể tạo đơn tay hộ khách tại quầy (POS)
    ORDER_VIEW,
    ORDER_UPDATE,
    ORDER_DELETE,     // Hủy đơn hoặc xóa log đơn nháp

    // 👥 Quyền liên quan đến Khách hàng & Nhân viên (User/Staff Management) - 🌟 BỔ SUNG BẮT BUỘC
    USER_VIEW,
    USER_UPDATE,
    USER_DELETE,

    // Quyền liên quan đến Hệ thống (Admin tối cao)
    SYSTEM_SETTING
}
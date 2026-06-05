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

    // Quyền liên quan đến Đơn hàng (Order)
    ORDER_VIEW,
    ORDER_UPDATE,

    // Quyền liên quan đến Hệ thống (Admin tối cao)
    SYSTEM_SETTING
}
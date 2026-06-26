package com.shoestore.common.enums.inventory;

public enum InventoryReferenceType {
    PRODUCT_CREATION,       // 🌟 MỚI: Khởi tạo sản phẩm mới tinh
    PRODUCT_UPDATE,         // 🌟 MỚI: Bổ sung biến thể (Variant) mới khi cập nhật
    STOCK_ADJUSTMENT,       // Phiên điều chỉnh kho tay của Admin
    PURCHASE_ORDER,         // Đơn nhập hàng từ Nhà cung cấp
    MANUAL_ADJUSTMENT,      // 🌟 BỔ SUNG: Dành cho Staff nhập/xuất thủ công tại quầy kho
    CUSTOMER_ORDER,          // Đơn mua hàng của Khách hàng
    ORDER_PLACEMENT,   // Dành cho luồng khách đặt hàng (trừ kho)
    ORDER_CANCELLATION // Dành cho luồng hủy đơn (hoàn kho)
}
package com.shoestore.common.enums.inventory;

public enum InventoryTransactionType {
    IMPORT,                 // Nhập hàng thủ công/theo lô từ nhà cung cấp (Tăng)
    ADJUSTMENT_INCREASE,    // Admin kiểm kho phát hiện thừa (Tăng)
    ADJUSTMENT_DECREASE,    // Admin kiểm kho phát hiện thiếu/hỏng (Giảm)

    // --- Đặt chỗ trước cho tương lai (Phase 4 - Order), hiện tại chưa dùng ---
    ORDER_RESERVED,         // Khách đặt hàng, giữ chỗ kho (Giảm Available)
    ORDER_RELEASED,         // Hủy đơn hàng, hoàn trả kho giữ chỗ (Tăng Available)
    ORDER_SHIPPED,          // Shipper lấy hàng đi, thực xuất kho (Giảm Physical)
    INBOUND,  // 🌟 BỔ SUNG: Giao dịch nhập kho (Cộng kho)
    OUTBOUND  // 🌟 BỔ SUNG: Giao dịch xuất kho (Trừ kho)
}
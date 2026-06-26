package com.shoestore.service;

import com.shoestore.dto.request.InventoryManagementRequest.InventoryFilterRequest;
import com.shoestore.dto.request.InventoryManagementRequest.StockAdjustmentRequest;
import com.shoestore.dto.response.InventoryManagementResponse.InventoryTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    // Hàm đọc lịch sử (Query Side)
    Page<InventoryTransactionResponse> getTransactionList(InventoryFilterRequest filter, Pageable pageable);

    // Các hàm ghi (Command Side) - Khớp chính xác số lượng tham số với Controller
    void increaseStock(Long skuId, StockAdjustmentRequest request, String idempotencyKey);

    void decreaseStock(Long skuId, StockAdjustmentRequest request, String idempotencyKey);

    void reconcileStock(Long skuId, StockAdjustmentRequest request);

}
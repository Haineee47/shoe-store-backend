package com.shoestore.listener;

import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.domain.event.SkuCreatedEvent;
import com.shoestore.dto.request.InventoryManagementRequest.StockAdjustmentRequest; // 🔥 Import DTO mới
import com.shoestore.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryInitializationListener {

    private final InventoryService inventoryService;

    @EventListener
    public void handleSkuCreated(SkuCreatedEvent event) {
        log.info("📦 [Inventory Init Listener] Khởi tạo tồn kho cho {} SKU thuộc sản phẩm ID [{}]",
                event.getSkuStockMap().size(), event.getProductId());

        String reason = event.getReferenceType() == InventoryReferenceType.PRODUCT_CREATION
                ? "Initial stock when creating product"
                : "Initial stock for newly added variant during product update";

        for (Map.Entry<Long, Integer> entry : event.getSkuStockMap().entrySet()) {
            Long skuId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity != null && quantity > 0) {
                // 🔄 ĐÓNG GÓI DỮ LIỆU: Tạo request object để khớp với cấu trúc mới của Service
                StockAdjustmentRequest request = new StockAdjustmentRequest();
                request.setQuantity(quantity);
                request.setReason(reason);
                request.setReferenceId(event.getProductId()); // Sử dụng Product ID làm mã chứng từ đối chiếu

                // Gọi hàm với đúng 3 tham số (skuId, request, idempotencyKey)
                // Gợi ý: Truyền Idempotency Key dạng UUID hoặc chuỗi tự chế để luồng event không bị lặp kho
                String uniqueEventKey = "EVENT_INIT_PROD_" + event.getProductId() + "_SKU_" + skuId;

                inventoryService.increaseStock(skuId, request, uniqueEventKey);
            }
        }
    }
}
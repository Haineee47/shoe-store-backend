package com.shoestore.listener;

import com.shoestore.domain.event.LowStockAlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LowStockAlertListener {

    @Async // Chạy bất đồng bộ để không chậm luồng mua hàng/trừ kho của khách
    @EventListener
    public void handleLowStockAlert(LowStockAlertEvent event) {
        log.error("🚨 [LOW STOCK ALERT] Biến thể SKU [{}] đã chạm ngưỡng cảnh báo hết hàng!", event.getSkuCode());
        log.error("   └─ SKU ID: [{}] | Tồn kho hiện tại: [{}] | Ngưỡng cấu hình: [{}]",
                event.getSkuId(), event.getCurrentStock(), event.getThreshold());

        // 💡 Ở Phase này, log ra màn hình console như thế này là đã đạt tiêu chí nghiệm thu "Low Stock Alert"!
        // Sau này khi làm module thông báo (Notification/Email), bạn chỉ việc inject Service thông báo vào đây.
    }
}
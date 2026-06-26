package com.shoestore.listener;

import com.shoestore.domain.event.InventoryChangedEvent;
import com.shoestore.entity.ProductInventorySummary;
import com.shoestore.repository.ProductInventorySummaryRepository;
import com.shoestore.repository.projection.SkuStatsProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryChangedListener {

    private final ProductInventorySummaryRepository summaryRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleInventoryChanged(InventoryChangedEvent event) {
        Long productId = event.getProductId();
        log.info("Transaction committed successfully. Refreshing inventory summary for product ID: {}", productId);

        try {
            // Bây giờ hàm đã trả về Optional nên lệnh .orElse() hoàn toàn hợp lệ!
            SkuStatsProjection stats = summaryRepository.calculateSkuStatsByProductId(productId)
                    .orElse(new SkuStatsProjection(productId, 0L, 0L));

            // Step 2: Tìm kiếm xem bản ghi Summary của Product này đã tồn tại trong DB chưa
            ProductInventorySummary summary = summaryRepository.findById(productId)
                    .orElse(null);

            if (summary != null) {
                // 🌟 SỬA TẠI ĐÂY: Dùng hàm getter chuẩn (.getTotalStock() và .getActiveSkuCount()) vì đây là Class
                summary.setTotalStock(stats.getTotalStock().intValue());
                summary.setActiveSkuCount(stats.getActiveSkuCount().intValue());
                log.info("♻️ Đã cập nhật (UPDATE) chỉ số tồn kho cho Product ID: {}", productId);
            } else {
                // 🌟 SỬA TẠI ĐÂY: Tương tự, đổi sang dùng hàm getter cho lối đi INSERT
                ProductInventorySummary newSummary = ProductInventorySummary.builder()
                        .productId(productId)
                        .totalStock(stats.getTotalStock().intValue())
                        .activeSkuCount(stats.getActiveSkuCount().intValue())
                        .build();

                summaryRepository.save(newSummary);
                log.info("✨ Đã khởi tạo mới (INSERT) chỉ số tồn kho cho Product ID: {}", productId);
            }

        } catch (Exception e) {
            log.error("Failed to refresh product inventory summary for product ID: {}", productId, e);
        }
    }
}
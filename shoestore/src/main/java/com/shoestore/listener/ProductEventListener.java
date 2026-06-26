package com.shoestore.listener;

import com.shoestore.domain.event.ProductCreatedEvent;
import com.shoestore.entity.ProductInventorySummary;
import com.shoestore.repository.ProductInventorySummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ProductInventorySummaryRepository summaryRepository;

    // 🌟 THAY ĐỔI: Chỉ chạy sau khi transaction tạo Product đã COMMIT thành công
    // Sử dụng REQUIRES_NEW để tạo một transaction độc lập cho listener này
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("🎯 Nhận sự kiện ProductCreatedEvent cho ID: {}", event.getProductId());

        // Tránh chèn trùng nếu vì lý do gì đó event bị gửi 2 lần (Idempotency)
        if (summaryRepository.existsById(event.getProductId())) {
            log.warn("⚠️ Summary cho Product ID {} đã tồn tại, bỏ qua khởi tạo.", event.getProductId());
            return;
        }

        ProductInventorySummary summary = ProductInventorySummary.builder()
                .productId(event.getProductId())
                .totalStock(0)
                .build();

        summaryRepository.save(summary);
        log.info("✅ Khởi tạo thành công bản ghi trống trong ProductInventorySummary cho ID: {}", event.getProductId());
    }
}
package com.shoestore.listener;

import com.shoestore.domain.event.ProductArchivedEvent;
import com.shoestore.domain.event.ProductDeletedEvent;
import com.shoestore.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async; // 🌟 BỔ SUNG
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation; // 🌟 BỔ SUNG
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSkuEventListener {

    private final ProductSkuService productSkuService;

    // 🌟 LUỒNG 1: XỬ LÝ KHI SẢN PHẨM BỊ LƯU TRỮ (BẤT ĐỒNG BỘ)
    @Async // 🌟 BỔ SUNG: Đẩy hàm này sang một Worker Thread khác, giải phóng Main Thread lập tức
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 🌟 CHỈNH SỬA: Ép Thread mới tạo transaction riêng để thực thi lệnh ghi
    public void handleProductArchived(ProductArchivedEvent event) {
        log.info("⚡ [Async Domain Event] [Thread: {}] Phát hiện Product ID [{}] bị đóng lưu trữ. Tiến hành hủy toàn bộ SKUs...",
                Thread.currentThread().getName(), event.productId());
        try {
            productSkuService.discontinueAllSkusByProductId(event.productId());
            log.info("✅ Đã dọn dẹp đóng băng toàn bộ biến thể SKU cho sản phẩm lưu trữ [{}] thành công.", event.productId());
        } catch (Exception e) {
            log.error("🚨 Thất bại khi đóng băng SKUs cho sản phẩm lưu trữ ID: {}", event.productId(), e);
        }
    }

    // 🌟 LUỒNG 2: XỬ LÝ KHI SẢN PHẨM BỊ XÓA MỀM (BẤT ĐỒNG BỘ)
    @Async // 🌟 BỔ SUNG: Đẩy hàm này sang một Worker Thread khác, giải phóng Main Thread lập tức
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 🌟 CHỈNH SỬA: Ép Thread mới tạo transaction riêng để thực thi lệnh ghi
    public void handleProductDeleted(ProductDeletedEvent event) {
        log.info("⚡ [Async Domain Event] [Thread: {}] Phát hiện Product ID [{}] bị xóa khỏi hệ thống. Tiến hành hủy toàn bộ SKUs liên quan...",
                Thread.currentThread().getName(), event.productId());
        try {
            productSkuService.discontinueAllSkusByProductId(event.productId());
            log.info("✅ Đã dọn dẹp đóng băng toàn bộ biến thể SKU cho sản phẩm bị xóa [{}] thành công.", event.productId());
        } catch (Exception e) {
            log.error("🚨 Thất bại khi đóng băng SKUs cho sản phẩm bị xóa ID: {}", event.productId(), e);
        }
    }
}
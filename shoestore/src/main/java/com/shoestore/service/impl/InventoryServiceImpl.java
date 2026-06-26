package com.shoestore.service.impl;

import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.common.enums.inventory.InventoryTransactionType;
import com.shoestore.domain.event.InventoryChangedEvent;
import com.shoestore.domain.event.LowStockAlertEvent;
import com.shoestore.domain.inventory.StockChangeResult;
import com.shoestore.dto.request.InventoryManagementRequest.InventoryFilterRequest;
import com.shoestore.dto.request.InventoryManagementRequest.StockAdjustmentRequest;
import com.shoestore.dto.response.InventoryManagementResponse.InventoryTransactionResponse;
import com.shoestore.entity.InventoryTransaction;
import com.shoestore.entity.ProductSku;
import com.shoestore.exception.BusinessException;
import com.shoestore.repository.InventoryTransactionRepository;
import com.shoestore.repository.ProductSkuRepository;
import com.shoestore.service.InventoryService;
import com.shoestore.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ProductSkuRepository productSkuRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository; // 🔥 Đã đồng bộ tên biến nhất quán
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void increaseStock(Long skuId, StockAdjustmentRequest request, String idempotencyKey) {
        // 🛡️ Bảo vệ biên đầu vào
        if (request.getQuantity() == null || request.getQuantity() <= 0) return;
        if (request.getReferenceId() == null) {
            throw new IllegalArgumentException("Reference ID không được để trống để đảm bảo tính Idempotent");
        }

        // 🛡️ TUYẾN PHÒNG THỦ CHỦ ĐỘNG (Idempotent Check)
        if (isDuplicateTransaction(InventoryReferenceType.MANUAL_ADJUSTMENT, request.getReferenceId(), skuId)) return;

        ProductSku sku = getSkuOrThrow(skuId);

        // Gọi nghiệp vụ domain thuần khiết
        StockChangeResult result = sku.increaseStock(request.getQuantity());

        // Lưu SKU bọc Optimistic Lock
        saveSkuWithOptimisticLock(sku, "Tăng kho");

        // 👑 LẤY ACTOR TỰ ĐỘNG TẠI SERVICE (Giảm dependency cho Controller)
        Long currentActorId = SecurityUtils.getCurrentUserId();
        String currentActorName = "STAFF_SNAPSHOT_NAME"; // Thực tế: userRepository.findNameById(currentActorId)

        // Lưu thẻ kho lịch sử (Đồng bộ theo cấu trúc Ledger mượt mà của bạn)
        saveTransaction(skuId, result, InventoryReferenceType.MANUAL_ADJUSTMENT,
                InventoryTransactionType.INBOUND, request.getReferenceId(), currentActorName, request.getReason());

        // Kích hoạt làm mới tổng kho bất đồng bộ
        publishInventoryChangedEvent(sku.getProductId());

        log.info("📦 [INVENTORY] Tăng kho thành công cho SKU [{}]: {} -> {}.",
                sku.getSkuCode(), result.getBeforeQuantity(), result.getAfterQuantity());
    }

    @Override
    @Transactional
    public void decreaseStock(Long skuId, StockAdjustmentRequest request, String idempotencyKey) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) return;
        if (request.getReferenceId() == null) {
            throw new IllegalArgumentException("Reference ID không được để trống để đảm bảo tính Idempotent");
        }

        // 🛡️ TUYẾN PHÒNG THỦ CHỦ ĐỘNG (Idempotent Check)
        if (isDuplicateTransaction(InventoryReferenceType.MANUAL_ADJUSTMENT, request.getReferenceId(), skuId)) return;

        ProductSku sku = getSkuOrThrow(skuId);

        // Gọi nghiệp vụ domain thuần khiết (Ném INSUFFICIENT_STOCK nếu âm kho bên trong)
        StockChangeResult result = sku.decreaseStock(request.getQuantity());

        // Lưu SKU bọc Optimistic Lock
        saveSkuWithOptimisticLock(sku, "Trừ kho");

        Long currentActorId = SecurityUtils.getCurrentUserId();
        String currentActorName = "STAFF_SNAPSHOT_NAME";

        // Lưu thẻ kho lịch sử
        saveTransaction(skuId, result, InventoryReferenceType.MANUAL_ADJUSTMENT,
                InventoryTransactionType.OUTBOUND, request.getReferenceId(), currentActorName, request.getReason());

        // Tuyển phòng thủ cảnh báo tồn thấp
        checkAndPublishLowStockAlert(sku);

        publishInventoryChangedEvent(sku.getProductId());

        log.info("📦 [INVENTORY] Trừ kho thành công cho SKU [{}]: {} -> {}.",
                sku.getSkuCode(), result.getBeforeQuantity(), result.getAfterQuantity());
    }

    @Override
    @Transactional
    public void reconcileStock(Long skuId, StockAdjustmentRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng kiểm kho thực tế không hợp lệ");
        }
        if (request.getReferenceId() == null) {
            throw new IllegalArgumentException("Mã chứng từ/Phiếu kiểm kê không được để trống");
        }

        if (isDuplicateTransaction(InventoryReferenceType.STOCK_ADJUSTMENT, request.getReferenceId(), skuId)) return;

        ProductSku sku = getSkuOrThrow(skuId);

        // Gọi hàm adjustStock của domain và hứng kết quả
        StockChangeResult result = sku.adjustStock(request.getQuantity());

        if (result.getDelta() == 0) {
            log.info("📊 [RECONCILE] Kết quả kiểm kho cho SKU [{}] hoàn toàn khớp ({}), không phát sinh giao dịch.",
                    sku.getSkuCode(), request.getQuantity());
            return;
        }

        saveSkuWithOptimisticLock(sku, "Kiểm kho / Cân bằng kho");

        InventoryTransactionType txType = result.getDelta() > 0
                ? InventoryTransactionType.ADJUSTMENT_INCREASE
                : InventoryTransactionType.ADJUSTMENT_DECREASE;

        Long currentActorId = SecurityUtils.getCurrentUserId();
        String currentActorName = "ADMIN_SNAPSHOT_NAME";

        // Lưu Thẻ Kho
        saveTransaction(skuId, result, InventoryReferenceType.STOCK_ADJUSTMENT, txType,
                request.getReferenceId(), currentActorName, request.getReason());

        checkAndPublishLowStockAlert(sku);
        publishInventoryChangedEvent(sku.getProductId());

        log.info("📊 [RECONCILE] Cân bằng kho thành công cho SKU [{}]. Biến động (Delta): {} (Trước: {} | Thực tế: {})",
                sku.getSkuCode(), result.getDelta(), result.getBeforeQuantity(), result.getAfterQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> getTransactionList(InventoryFilterRequest filter, Pageable pageable) {
        // SỬA LỖI: Gọi đúng tên biến cục bộ 'inventoryTransactionRepository'
        Page<InventoryTransaction> transactions = inventoryTransactionRepository.findAllByFilter(filter, pageable);

        return transactions.map(entity -> {
            InventoryTransactionResponse dto = new InventoryTransactionResponse();
            dto.setId(entity.getId());
            dto.setSkuId(entity.getSkuId());
            dto.setQuantity(entity.getQuantityChange());
            dto.setTransactionType(entity.getTransactionType());
            dto.setReferenceType(entity.getReferenceType());
            dto.setReferenceId(entity.getReferenceId());
            dto.setReason(entity.getReason());
            dto.setActorName(entity.getActorName()); // Sổ cái Immutable chứa trường này
            dto.setCreatedAt(entity.getCreatedAt());
            return dto;
        });
    }

    // =========================================================================
    // ⚙️ PRIVATE HELPERS
    // =========================================================================

    private ProductSku getSkuOrThrow(Long skuId) {
        return productSkuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private void saveSkuWithOptimisticLock(ProductSku sku, String actionName) {
        try {
            productSkuRepository.saveAndFlush(sku);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("🚨 [CONCURRENCY CONFLICT] Thao tác [{}] thất bại do SKU [{}] đã bị chỉnh sửa ở luồng khác.", actionName, sku.getId());
            throw new BusinessException(ErrorCode.SKU_CONCURRENT_MODIFICATION);
        }
    }

    private boolean isDuplicateTransaction(InventoryReferenceType refType, Long refId, Long skuId) {
        return inventoryTransactionRepository.existsByReferenceTypeAndReferenceIdAndSkuId(refType, refId, skuId);
    }

    private void checkAndPublishLowStockAlert(ProductSku sku) {
        if (sku.getStockQuantity() <= sku.getLowStockThreshold()) {
            eventPublisher.publishEvent(new LowStockAlertEvent(
                    this, sku.getId(), sku.getSkuCode(), sku.getStockQuantity(), sku.getLowStockThreshold()
            ));
        }
    }

    private void saveTransaction(Long skuId, StockChangeResult result, InventoryReferenceType refType,
                                 InventoryTransactionType txType, Long refId, String actorName, String reason) {
        InventoryTransaction tx = InventoryTransaction.builder()
                .skuId(skuId)
                .quantityBefore(result.getBeforeQuantity())
                .quantityAfter(result.getAfterQuantity())
                .quantityChange(result.getDelta())
                .referenceType(refType)
                .transactionType(txType)
                .referenceId(refId)
                .actorName(actorName) // Ghi cứng snapshot định danh bảo vệ Audit Trail
                .reason(reason != null ? reason.trim() : "")
                .build();

        inventoryTransactionRepository.saveAndFlush(tx);
    }

    private void publishInventoryChangedEvent(Long productId) {
        if (productId != null) {
            eventPublisher.publishEvent(new InventoryChangedEvent(this, productId));
        }
    }
}
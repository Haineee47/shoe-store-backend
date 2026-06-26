package com.shoestore.controller;

import com.shoestore.common.constant.PaginationConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.InventoryManagementRequest.InventoryFilterRequest;
import com.shoestore.dto.request.InventoryManagementRequest.StockAdjustmentRequest;
import com.shoestore.dto.response.InventoryManagementResponse.InventoryTransactionResponse;
import com.shoestore.service.InventoryService;
import com.shoestore.util.PageableUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@Validated // 🔥 Đảm bảo kích hoạt validation cho cả @PathVariable nguyên thủy
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // =========================================================================
    // 🔍 1. QUERY SIDE - LUỒNG ĐỌC DỮ LIỆU (Read Operations)
    // =========================================================================

    @GetMapping("/transactions")
    @PreAuthorize("@ss.hasPermission('INVENTORY_VIEW')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getInventoryHistory(
            @ModelAttribute InventoryFilterRequest filter,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER) @Min(0) int page,
            @RequestParam(defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE) @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id,desc") String sort // 💡 Note: Cần được Whitelist tại PageableUtils chống SQL Injection Injection qua order-by
    ) {
        // ✅ XÓA LOG INFO DƯ THỪA: Tránh spam ổ cứng môi trường Production khi Admin/Staff refresh dashboard liên tục
        Pageable pageable = PageableUtils.createPageable(page, size, sort);
        Page<InventoryTransactionResponse> response = inventoryService.getTransactionList(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch sử biến động kho thành công", response));
    }

    // ==========================================
    // ⚔️ 2. COMMAND SIDE - LUỒNG ACTION / COMMANDS (Write Operations)
    // ==========================================

    /**
     * ✅ QUAY LẠI POST METHOD: Chuẩn Business Command Semantic (DDD Action Endpoint).
     * Bổ sung @Positive ngăn chặn dữ liệu rác (skuId <= 0).
     */
    @PostMapping("/skus/{skuId}/increase")
    @PreAuthorize("@ss.hasPermission('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> increaseStock(
            @PathVariable @Positive(message = "SKU ID không hợp lệ") Long skuId,
            @Valid @RequestBody StockAdjustmentRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey // 💡 Chuẩn bị hạ tầng xử lý trùng lặp (Idempotency)
    ) {
        log.info("Nhận lệnh tăng kho vật lý cho SKU ID: [{}], Số lượng: [{}]", skuId, request.getQuantity());

        // 💡 Chuyển việc lấy Actor xuống Service hoặc pass qua lớp DTO / Params để Controller thuần khiết
        inventoryService.increaseStock(skuId, request, idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nhập kho vật lý cho biến thể SKU thành công", null));
    }

    @PostMapping("/skus/{skuId}/decrease")
    @PreAuthorize("@ss.hasPermission('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(
            @PathVariable @Positive(message = "SKU ID không hợp lệ") Long skuId,
            @Valid @RequestBody StockAdjustmentRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey
    ) {
        log.info("Nhận lệnh trừ kho vật lý cho SKU ID: [{}], Số lượng: [{}]", skuId, request.getQuantity());

        inventoryService.decreaseStock(skuId, request, idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Xuất kho điều chỉnh cho biến thể SKU thành công", null));
    }

    @PostMapping("/skus/{skuId}/reconcile")
    @PreAuthorize("@ss.hasPermission('INVENTORY_RECONCILE')")
    public ResponseEntity<ApiResponse<Void>> reconcileStock(
            @PathVariable @Positive(message = "SKU ID không hợp lệ") Long skuId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        log.info("Nhận lệnh cân bằng kho cho SKU ID: [{}]", skuId);

        inventoryService.reconcileStock(skuId, request);

        return ResponseEntity.ok(ApiResponse.success("Điều chỉnh cân bằng kho thành công", null));
    }
}
package com.shoestore.dto.request.InventoryManagementRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockAdjustmentRequest {
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng điều chỉnh phải lớn hơn 0")
    private Integer quantity;

    @NotBlank(message = "Lý do điều chỉnh không được để trống")
    private String reason;

    @NotNull(message = "Reference ID (Mã phiếu/Mã chứng từ) không được để trống")
    private Long referenceId;
}

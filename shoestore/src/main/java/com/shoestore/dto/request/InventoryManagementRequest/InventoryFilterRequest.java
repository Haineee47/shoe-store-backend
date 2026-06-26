package com.shoestore.dto.request.InventoryManagementRequest;

import lombok.Data;

@Data
public class InventoryFilterRequest {
    private Long skuId;
    private String transactionType;   // INBOUND hoặc OUTBOUND
    private String referenceType;     // MANUAL_ADJUSTMENT, PRODUCT_CREATION, v.v.
    private Long referenceId;
    private Long createdBy;           // Tìm theo ID nhân viên thực hiện
}
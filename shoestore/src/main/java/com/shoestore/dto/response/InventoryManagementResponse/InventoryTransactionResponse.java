package com.shoestore.dto.response.InventoryManagementResponse;

import com.shoestore.common.enums.inventory.InventoryReferenceType;
import com.shoestore.common.enums.inventory.InventoryTransactionType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryTransactionResponse {
    private Long id;
    private Long skuId;
    private String skuCode;
    private Integer quantity;
    private InventoryTransactionType transactionType;
    private InventoryReferenceType referenceType;
    private Long referenceId;
    private String reason;
    private String actorName;
    private LocalDateTime createdAt;
}
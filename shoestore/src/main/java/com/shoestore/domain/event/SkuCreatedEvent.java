package com.shoestore.domain.event;

import com.shoestore.common.enums.inventory.InventoryReferenceType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.Map;

@Getter
public class SkuCreatedEvent extends ApplicationEvent {
    private final Long productId;
    private final Map<Long, Integer> skuStockMap; // Key: skuId, Value: stockQuantity ban đầu
    private final InventoryReferenceType referenceType;
    private final Long actorId;

    public SkuCreatedEvent(Object source, Long productId, Map<Long, Integer> skuStockMap,
                           InventoryReferenceType referenceType, Long actorId) {
        super(source);
        this.productId = productId;
        this.skuStockMap = skuStockMap;
        this.referenceType = referenceType;
        this.actorId = actorId;
    }
}
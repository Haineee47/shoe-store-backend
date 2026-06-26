package com.shoestore.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LowStockAlertEvent extends ApplicationEvent {
    private final Long skuId;
    private final String skuCode;
    private final Integer currentStock;
    private final Integer threshold;

    public LowStockAlertEvent(Object source, Long skuId, String skuCode, Integer currentStock, Integer threshold) {
        super(source);
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.currentStock = currentStock;
        this.threshold = threshold;
    }
}
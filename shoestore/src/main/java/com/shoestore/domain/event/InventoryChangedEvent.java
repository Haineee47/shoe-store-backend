package com.shoestore.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InventoryChangedEvent extends ApplicationEvent {
    private final Long productId;

    public InventoryChangedEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }
}
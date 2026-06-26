package com.shoestore.domain.event;

import lombok.Getter;

@Getter
public class ProductCreatedEvent {
    private final Object source;
    private final Long productId;

    public ProductCreatedEvent(Object source, Long productId) {
        this.source = source;
        this.productId = productId;
    }
}
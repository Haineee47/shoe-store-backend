package com.shoestore.domain.inventory; // Nằm hoàn toàn ở Domain Layer

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockChangeResult {

    private final Integer beforeQuantity;

    private final Integer afterQuantity;

    private final Integer delta;
}
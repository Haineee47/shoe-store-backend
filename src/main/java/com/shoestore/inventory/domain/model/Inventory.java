package com.shoestore.inventory.domain.model;

import com.shoestore.inventory.domain.exception.InsufficientAvailableStockException;
import com.shoestore.inventory.domain.exception.InsufficientReservedStockException;
import com.shoestore.inventory.domain.exception.StockQuantityOverflowException;
import com.shoestore.shared.domain.model.AggregateRoot;

import java.util.Objects;

/**
 * Aggregate root responsible for protecting inventory quantity invariants.
 *
 * <p>The aggregate tracks physical on-hand stock and stock currently
 * reserved. Available stock is derived and is never stored independently.</p>
 */
public final class Inventory extends AggregateRoot<InventoryId> {

    private StockQuantity onHand;
    private StockQuantity reserved;

    private Inventory(InventoryId id) {
        super(id);
        this.onHand = StockQuantity.zero();
        this.reserved = StockQuantity.zero();
    }

    /**
     * Creates an empty inventory aggregate with an explicit identity.
     */
    public static Inventory create(InventoryId id) {
        return new Inventory(id);
    }

    public StockQuantity onHand() {
        return onHand;
    }

    public StockQuantity reserved() {
        return reserved;
    }

    public StockQuantity available() {
        return StockQuantity.of(
                onHand.value() - reserved.value()
        );
    }

    public boolean hasAvailableStock() {
        return available().isPositive();
    }

    /**
     * Adds physical stock to inventory.
     */
    public void increaseStock(StockAmount amount) {
        StockAmount requiredAmount = requireAmount(amount);

        int increasedQuantity;

        try {
            increasedQuantity = Math.addExact(
                    onHand.value(),
                    requiredAmount.value()
            );
        } catch (ArithmeticException exception) {
            throw new StockQuantityOverflowException(
                    onHand.value(),
                    requiredAmount.value()
            );
        }

        this.onHand = StockQuantity.of(increasedQuantity);
    }

    /**
     * Removes unreserved physical stock from inventory.
     */
    public void decreaseStock(StockAmount amount) {
        StockAmount requiredAmount = requireAmount(amount);
        int availableQuantity = available().value();

        if (requiredAmount.value() > availableQuantity) {
            throw new InsufficientAvailableStockException(
                    requiredAmount.value(),
                    availableQuantity
            );
        }

        int decreasedQuantity =
                onHand.value() - requiredAmount.value();

        this.onHand = StockQuantity.of(decreasedQuantity);
    }

    /**
     * Reserves currently available stock.
     */
    public void reserve(StockAmount amount) {
        StockAmount requiredAmount = requireAmount(amount);
        int availableQuantity = available().value();

        if (requiredAmount.value() > availableQuantity) {
            throw new InsufficientAvailableStockException(
                    requiredAmount.value(),
                    availableQuantity
            );
        }

        int increasedReservation = Math.addExact(
                reserved.value(),
                requiredAmount.value()
        );

        this.reserved = StockQuantity.of(
                increasedReservation
        );
    }

    /**
     * Releases stock from the current reservation quantity.
     */
    public void release(StockAmount amount) {
        StockAmount requiredAmount = requireAmount(amount);

        if (requiredAmount.value() > reserved.value()) {
            throw new InsufficientReservedStockException(
                    requiredAmount.value(),
                    reserved.value()
            );
        }

        int decreasedReservation =
                reserved.value() - requiredAmount.value();

        this.reserved = StockQuantity.of(
                decreasedReservation
        );
    }

    private static StockAmount requireAmount(
            StockAmount amount
    ) {
        return Objects.requireNonNull(
                amount,
                "Stock amount must not be null"
        );
    }
}

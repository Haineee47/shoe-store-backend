package com.shoestore.inventory.domain.model;

import com.shoestore.inventory.domain.exception.InsufficientAvailableStockException;
import com.shoestore.inventory.domain.exception.InsufficientReservedStockException;
import com.shoestore.inventory.domain.exception.StockQuantityOverflowException;
import com.shoestore.shared.domain.model.AggregateRoot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    @Test
    void shouldCreateEmptyInventory() {
        InventoryId id = InventoryId.generate();

        Inventory inventory = Inventory.create(id);

        assertThat(inventory.id()).isEqualTo(id);
        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.hasAvailableStock()).isFalse();
    }

    @Test
    void shouldRejectNullIdentity() {
        assertThatNullPointerException()
                .isThrownBy(() -> Inventory.create(null))
                .withMessage(
                        "Domain entity id must not be null"
                );
    }

    @Test
    void shouldIncreaseStock() {
        Inventory inventory = createInventory();

        inventory.increaseStock(StockAmount.of(10));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.hasAvailableStock()).isTrue();
    }

    @Test
    void shouldAccumulateStockIncreases() {
        Inventory inventory = createInventory();

        inventory.increaseStock(StockAmount.of(10));
        inventory.increaseStock(StockAmount.of(5));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(15));
    }

    @Test
    void shouldRejectStockIncreaseOverflow() {
        Inventory inventory = createInventory();

        inventory.increaseStock(
                StockAmount.of(Integer.MAX_VALUE)
        );

        assertThatThrownBy(
                () -> inventory.increaseStock(
                        StockAmount.of(1)
                )
        )
                .isInstanceOf(
                        StockQuantityOverflowException.class
                )
                .hasMessage(
                        "Stock quantity overflow: current "
                                + Integer.MAX_VALUE
                                + ", requested increase 1"
                );
    }

    @Test
    void shouldPreserveStateWhenStockIncreaseOverflows() {
        Inventory inventory = createInventory();

        inventory.increaseStock(
                StockAmount.of(Integer.MAX_VALUE)
        );

        assertThatThrownBy(
                () -> inventory.increaseStock(
                        StockAmount.of(1)
                )
        ).isInstanceOf(
                StockQuantityOverflowException.class
        );

        assertThat(inventory.onHand())
                .isEqualTo(
                        StockQuantity.of(Integer.MAX_VALUE)
                );
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.zero());
    }

    @Test
    void shouldDecreaseAvailableStock() {
        Inventory inventory = inventoryWithStock(10);

        inventory.decreaseStock(StockAmount.of(4));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(6));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(6));
    }

    @Test
    void shouldDecreaseAllAvailableStock() {
        Inventory inventory = inventoryWithStock(10);

        inventory.decreaseStock(StockAmount.of(10));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.zero());
    }

    @Test
    void shouldRejectDecreaseBeyondAvailableStock() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(7));

        assertThatThrownBy(
                () -> inventory.decreaseStock(
                        StockAmount.of(4)
                )
        )
                .isInstanceOf(
                        InsufficientAvailableStockException.class
                )
                .hasMessage(
                        "Insufficient available stock: requested 4, available 3"
                );
    }

    @Test
    void shouldPreserveStateWhenDecreaseFails() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(7));

        assertThatThrownBy(
                () -> inventory.decreaseStock(
                        StockAmount.of(4)
                )
        ).isInstanceOf(
                InsufficientAvailableStockException.class
        );

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.of(7));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(3));
    }

    @Test
    void shouldReserveAvailableStock() {
        Inventory inventory = inventoryWithStock(10);

        inventory.reserve(StockAmount.of(4));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.of(4));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(6));
    }

    @Test
    void shouldReserveAllAvailableStock() {
        Inventory inventory = inventoryWithStock(10);

        inventory.reserve(StockAmount.of(10));

        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.hasAvailableStock()).isFalse();
    }

    @Test
    void shouldRejectReservationBeyondAvailableStock() {
        Inventory inventory = inventoryWithStock(10);

        assertThatThrownBy(
                () -> inventory.reserve(
                        StockAmount.of(11)
                )
        )
                .isInstanceOf(
                        InsufficientAvailableStockException.class
                )
                .hasMessage(
                        "Insufficient available stock: requested 11, available 10"
                );
    }

    @Test
    void shouldPreserveStateWhenReservationFails() {
        Inventory inventory = inventoryWithStock(10);

        assertThatThrownBy(
                () -> inventory.reserve(
                        StockAmount.of(11)
                )
        ).isInstanceOf(
                InsufficientAvailableStockException.class
        );

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.zero());
    }

    @Test
    void shouldReleaseReservedStock() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(7));

        inventory.release(StockAmount.of(3));

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.of(4));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(6));
    }

    @Test
    void shouldReleaseAllReservedStock() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(7));

        inventory.release(StockAmount.of(7));

        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.zero());
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(10));
    }

    @Test
    void shouldRejectReleaseBeyondReservedStock() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(4));

        assertThatThrownBy(
                () -> inventory.release(
                        StockAmount.of(5)
                )
        )
                .isInstanceOf(
                        InsufficientReservedStockException.class
                )
                .hasMessage(
                        "Insufficient reserved stock: requested 5, reserved 4"
                );
    }

    @Test
    void shouldPreserveStateWhenReleaseFails() {
        Inventory inventory = inventoryWithStock(10);
        inventory.reserve(StockAmount.of(4));

        assertThatThrownBy(
                () -> inventory.release(
                        StockAmount.of(5)
                )
        ).isInstanceOf(
                InsufficientReservedStockException.class
        );

        assertThat(inventory.onHand())
                .isEqualTo(StockQuantity.of(10));
        assertThat(inventory.reserved())
                .isEqualTo(StockQuantity.of(4));
        assertThat(inventory.available())
                .isEqualTo(StockQuantity.of(6));
    }

    @Test
    void shouldRejectNullAmountForEveryStockOperation() {
        Inventory inventory = createInventory();

        assertThatNullPointerException()
                .isThrownBy(
                        () -> inventory.increaseStock(null)
                )
                .withMessage("Stock amount must not be null");

        assertThatNullPointerException()
                .isThrownBy(
                        () -> inventory.decreaseStock(null)
                )
                .withMessage("Stock amount must not be null");

        assertThatNullPointerException()
                .isThrownBy(
                        () -> inventory.reserve(null)
                )
                .withMessage("Stock amount must not be null");

        assertThatNullPointerException()
                .isThrownBy(
                        () -> inventory.release(null)
                )
                .withMessage("Stock amount must not be null");
    }

    @Test
    void shouldBeEqualWhenInventoryIdsAreEqual() {
        InventoryId id = InventoryId.generate();

        Inventory first = Inventory.create(id);
        Inventory second = Inventory.create(id);

        first.increaseStock(StockAmount.of(10));

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenInventoryIdsAreDifferent() {
        Inventory first = createInventory();
        Inventory second = createInventory();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldExtendAggregateRoot() {
        Inventory inventory = createInventory();

        assertThat(inventory)
                .isInstanceOf(AggregateRoot.class);
    }

    private static Inventory createInventory() {
        return Inventory.create(
                InventoryId.generate()
        );
    }

    private static Inventory inventoryWithStock(
            int quantity
    ) {
        Inventory inventory = createInventory();
        inventory.increaseStock(
                StockAmount.of(quantity)
        );
        return inventory;
    }
}

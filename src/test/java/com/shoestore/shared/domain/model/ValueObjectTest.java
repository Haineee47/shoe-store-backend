package com.shoestore.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ValueObjectTest {

    @Test
    void shouldUseValueEqualityForRecordValueObject() {
        TestTextValue first =
                new TestTextValue("shoe");

        TestTextValue second =
                new TestTextValue("shoe");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesDiffer() {
        TestTextValue first =
                new TestTextValue("shoe");

        TestTextValue second =
                new TestTextValue("boot");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldNormalizeValueDuringConstruction() {
        TestTextValue value =
                new TestTextValue("  shoe  ");

        assertThat(value.value()).isEqualTo("shoe");
    }

    @Test
    void shouldRejectNullTextValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TestTextValue(null))
                .withMessage(
                        "Test text value must not be null"
                );
    }

    @Test
    void shouldRejectBlankTextValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TestTextValue("   "))
                .withMessage(
                        "Test text value must not be blank"
                );
    }

    @Test
    void shouldUseAllMeaningfulFieldsForClassValueEquality() {
        TestRangeValue first =
                new TestRangeValue(1, 10);

        TestRangeValue second =
                new TestRangeValue(1, 10);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenAnyMeaningfulFieldDiffers() {
        TestRangeValue first =
                new TestRangeValue(1, 10);

        TestRangeValue second =
                new TestRangeValue(1, 11);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldBehaveConsistentlyInHashSet() {
        HashSet<TestTextValue> values = new HashSet<>();

        values.add(new TestTextValue("shoe"));
        values.add(new TestTextValue("shoe"));

        assertThat(values).hasSize(1);
    }

    @Test
    void shouldDefensivelyCopyMutableInputCollection() {
        ArrayList<String> source =
                new ArrayList<>(List.of("A", "B"));

        TestCollectionValue value =
                new TestCollectionValue(source);

        source.add("C");

        assertThat(value.values())
                .containsExactly("A", "B");
    }

    @Test
    void shouldExposeUnmodifiableCollection() {
        TestCollectionValue value =
                new TestCollectionValue(
                        List.of("A", "B")
                );

        assertThat(value.values())
                .containsExactly("A", "B");

        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> value.values().add("C")
        );
    }

    @Test
    void shouldRejectInvalidRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> new TestRangeValue(10, 1)
                )
                .withMessage(
                        "Minimum must not be greater than maximum"
                );
    }
}

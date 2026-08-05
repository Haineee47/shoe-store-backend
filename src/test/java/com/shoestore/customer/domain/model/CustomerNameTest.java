package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CustomerNameTest {

    @Test
    void shouldCreateCustomerName() {
        CustomerName name = CustomerName.of("Phan Tấn Hải");

        assertThat(name.value()).isEqualTo("Phan Tấn Hải");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        CustomerName name =
                CustomerName.of("  Phan Tấn Hải  ");

        assertThat(name.value()).isEqualTo("Phan Tấn Hải");
    }

    @Test
    void shouldPreserveInternalWhitespace() {
        CustomerName name =
                CustomerName.of("Phan  Tấn  Hải");

        assertThat(name.value())
                .isEqualTo("Phan  Tấn  Hải");
    }

    @Test
    void shouldPreserveLetterCasing() {
        CustomerName name =
                CustomerName.of("PHAN Tấn Hải");

        assertThat(name.value())
                .isEqualTo("PHAN Tấn Hải");
    }

    @Test
    void shouldSupportUnicodeCharacters() {
        CustomerName name =
                CustomerName.of("Nguyễn Ánh");

        assertThat(name.value())
                .isEqualTo("Nguyễn Ánh");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> CustomerName.of(null))
                .withMessage("Customer name must not be null");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CustomerName.of(""))
                .withMessage("Customer name must not be blank");
    }

    @Test
    void shouldRejectWhitespaceOnlyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CustomerName.of("   "))
                .withMessage("Customer name must not be blank");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        CustomerName first =
                CustomerName.of("Phan Tấn Hải");
        CustomerName second =
                CustomerName.of("Phan Tấn Hải");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        CustomerName first =
                CustomerName.of("Phan Tấn Hải");
        CustomerName second =
                CustomerName.of("Nguyễn Văn An");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnValueAsStringRepresentation() {
        CustomerName name =
                CustomerName.of("Phan Tấn Hải");

        assertThat(name.toString())
                .isEqualTo("Phan Tấn Hải");
    }

    @Test
    void shouldImplementValueObjectContract() {
        CustomerName name =
                CustomerName.of("Phan Tấn Hải");

        assertThat(name)
                .isInstanceOf(ValueObject.class);
    }
}

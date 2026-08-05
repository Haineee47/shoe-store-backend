package com.shoestore.customer.domain.model;

import com.shoestore.shared.domain.model.ValueObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class EmailAddressTest {

    @Test
    void shouldCreateEmailAddress() {
        EmailAddress email =
                EmailAddress.of("hai@example.com");

        assertThat(email.value())
                .isEqualTo("hai@example.com");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        EmailAddress email =
                EmailAddress.of("  hai@example.com  ");

        assertThat(email.value())
                .isEqualTo("hai@example.com");
    }

    @Test
    void shouldNormalizeDomainToLowercase() {
        EmailAddress email =
                EmailAddress.of("Hai@Example.COM");

        assertThat(email.value())
                .isEqualTo("Hai@example.com");
    }

    @Test
    void shouldPreserveLocalPartCasing() {
        EmailAddress upperLocalPart =
                EmailAddress.of("Hai@example.com");

        EmailAddress lowerLocalPart =
                EmailAddress.of("hai@example.com");

        assertThat(upperLocalPart)
                .isNotEqualTo(lowerLocalPart);
    }

    @Test
    void shouldSupportPlusAddressing() {
        EmailAddress email =
                EmailAddress.of(
                        "hai+orders@example.com"
                );

        assertThat(email.value())
                .isEqualTo(
                        "hai+orders@example.com"
                );
    }

    @Test
    void shouldSupportSubdomains() {
        EmailAddress email =
                EmailAddress.of(
                        "hai@orders.shop.example.com"
                );

        assertThat(email.value())
                .isEqualTo(
                        "hai@orders.shop.example.com"
                );
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> EmailAddress.of(null))
                .withMessage(
                        "Email address must not be null"
                );
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmailAddress.of(""))
                .withMessage(
                        "Email address must not be blank"
                );
    }

    @Test
    void shouldRejectWhitespaceOnlyValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmailAddress.of("   "))
                .withMessage(
                        "Email address must not be blank"
                );
    }

    @Test
    void shouldRejectMissingAtSign() {
        assertInvalidEmail("haiexample.com");
    }

    @Test
    void shouldRejectMissingLocalPart() {
        assertInvalidEmail("@example.com");
    }

    @Test
    void shouldRejectMissingDomainPart() {
        assertInvalidEmail("hai@");
    }

    @Test
    void shouldRejectMultipleAtSigns() {
        assertInvalidEmail("hai@@example.com");
    }

    @Test
    void shouldRejectInternalWhitespace() {
        assertInvalidEmail("hai user@example.com");
    }

    @Test
    void shouldRejectDomainWithoutDot() {
        assertInvalidEmail("hai@example");
    }

    @Test
    void shouldRejectConsecutiveDotsInDomain() {
        assertInvalidEmail("hai@example..com");
    }

    @Test
    void shouldRejectLocalPartStartingWithDot() {
        assertInvalidEmail(".hai@example.com");
    }

    @Test
    void shouldRejectLocalPartEndingWithDot() {
        assertInvalidEmail("hai.@example.com");
    }

    @Test
    void shouldRejectConsecutiveDotsInLocalPart() {
        assertInvalidEmail("hai..customer@example.com");
    }

    @Test
    void shouldRejectUnsupportedDomainCharacters() {
        assertInvalidEmail("hai@exam_ple.com");
    }

    @Test
    void shouldBeEqualWhenNormalizedValuesAreEqual() {
        EmailAddress first =
                EmailAddress.of("hai@EXAMPLE.COM");
        EmailAddress second =
                EmailAddress.of("hai@example.com");

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }

    @Test
    void shouldNotBeEqualWhenAddressesAreDifferent() {
        EmailAddress first =
                EmailAddress.of("hai@example.com");
        EmailAddress second =
                EmailAddress.of("an@example.com");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnNormalizedValueAsStringRepresentation() {
        EmailAddress email =
                EmailAddress.of("hai@EXAMPLE.COM");

        assertThat(email.toString())
                .isEqualTo("hai@example.com");
    }

    @Test
    void shouldImplementValueObjectContract() {
        EmailAddress email =
                EmailAddress.of("hai@example.com");

        assertThat(email)
                .isInstanceOf(ValueObject.class);
    }

    private static void assertInvalidEmail(String value) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmailAddress.of(value))
                .withMessage(
                        "Email address format is invalid"
                );
    }
}

package com.shoestore.shared.application.pagination;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PageRequestTest {

    @Test
    void shouldCreateValidPageRequest() {
        PageRequest request = new PageRequest(2, 25);

        assertThat(request.page()).isEqualTo(2);
        assertThat(request.size()).isEqualTo(25);
        assertThat(request.offset()).isEqualTo(50L);
    }

    @Test
    void shouldCreateDefaultPageRequest() {
        PageRequest request = PageRequest.defaultPage();

        assertThat(request.page())
                .isEqualTo(PageRequest.DEFAULT_PAGE);

        assertThat(request.size())
                .isEqualTo(PageRequest.DEFAULT_SIZE);

        assertThat(request.offset()).isZero();
    }

    @Test
    void shouldAcceptMaximumPageSize() {
        PageRequest request = new PageRequest(
                0,
                PageRequest.MAXIMUM_SIZE
        );

        assertThat(request.size())
                .isEqualTo(PageRequest.MAXIMUM_SIZE);
    }

    @Test
    void shouldCalculateOffsetUsingLongArithmetic() {
        PageRequest request = new PageRequest(
                Integer.MAX_VALUE,
                PageRequest.MAXIMUM_SIZE
        );

        assertThat(request.offset())
                .isEqualTo(
                        (long) Integer.MAX_VALUE
                                * PageRequest.MAXIMUM_SIZE
                );
    }

    @Test
    void shouldRejectNegativePage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageRequest(-1, 20))
                .withMessage(
                        "page must be greater than or equal to 0"
                );
    }

    @Test
    void shouldRejectZeroSize() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageRequest(0, 0))
                .withMessage(
                        "size must be between 1 and 100"
                );
    }

    @Test
    void shouldRejectNegativeSize() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageRequest(0, -1))
                .withMessage(
                        "size must be between 1 and 100"
                );
    }

    @Test
    void shouldRejectSizeAboveMaximum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageRequest(0, 101))
                .withMessage(
                        "size must be between 1 and 100"
                );
    }
}

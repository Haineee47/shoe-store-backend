package com.shoestore.shared.application.pagination;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResponseTest {

    @Test
    void shouldCreateFirstPageWithNextPage() {
        PageResponse<String> response = PageResponse.of(
                List.of("A", "B"),
                new PageRequest(0, 2),
                5
        );

        assertThat(response.content())
                .containsExactly("A", "B");

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.numberOfItems()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);

        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
        assertThat(response.hasPrevious()).isFalse();
        assertThat(response.hasNext()).isTrue();
    }

    @Test
    void shouldCreateMiddlePage() {
        PageResponse<String> response = PageResponse.of(
                List.of("C", "D"),
                new PageRequest(1, 2),
                5
        );

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
        assertThat(response.hasPrevious()).isTrue();
        assertThat(response.hasNext()).isTrue();
    }

    @Test
    void shouldCreateLastPartialPage() {
        PageResponse<String> response = PageResponse.of(
                List.of("E"),
                new PageRequest(2, 2),
                5
        );

        assertThat(response.numberOfItems()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
        assertThat(response.hasPrevious()).isTrue();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void shouldCreateEmptyFirstPage() {
        PageResponse<String> response = PageResponse.of(
                List.of(),
                PageRequest.defaultPage(),
                0
        );

        assertThat(response.content()).isEmpty();
        assertThat(response.numberOfItems()).isZero();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
        assertThat(response.hasPrevious()).isFalse();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void shouldCalculateTotalPagesWithoutFloatingPoint() {
        PageResponse<String> response = PageResponse.of(
                List.of(),
                new PageRequest(0, 20),
                41
        );

        assertThat(response.totalPages()).isEqualTo(3);
    }

    @Test
    void shouldAllowEmptyPageBeyondAvailableRange() {
        PageResponse<String> response = PageResponse.of(
                List.of(),
                new PageRequest(10, 20),
                41
        );

        assertThat(response.page()).isEqualTo(10);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
        assertThat(response.hasPrevious()).isTrue();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void shouldDefensivelyCopyContent() {
        List<String> mutableContent = new ArrayList<>(
                List.of("A", "B")
        );

        PageResponse<String> response = PageResponse.of(
                mutableContent,
                new PageRequest(0, 2),
                2
        );

        mutableContent.clear();

        assertThat(response.content())
                .containsExactly("A", "B");

        assertThatThrownBy(() ->
                response.content().clear()
        ).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectNullContent() {
        assertThatNullPointerException()
                .isThrownBy(() -> PageResponse.of(
                        null,
                        PageRequest.defaultPage(),
                        0
                ))
                .withMessage("content must not be null");
    }

    @Test
    void shouldRejectNullRequest() {
        assertThatNullPointerException()
                .isThrownBy(() -> PageResponse.of(
                        List.of(),
                        null,
                        0
                ))
                .withMessage("request must not be null");
    }

    @Test
    void shouldRejectNegativeTotalElements() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PageResponse.of(
                        List.of(),
                        PageRequest.defaultPage(),
                        -1
                ))
                .withMessage(
                        "totalElements must be greater than "
                                + "or equal to 0"
                );
    }

    @Test
    void shouldRejectContentLargerThanRequestedSize() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PageResponse.of(
                        List.of("A", "B", "C"),
                        new PageRequest(0, 2),
                        3
                ))
                .withMessage(
                        "content size must not exceed page size"
                );
    }

    @Test
    void shouldRejectContentLargerThanTotalElements() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PageResponse.of(
                        List.of("A", "B"),
                        new PageRequest(0, 20),
                        1
                ))
                .withMessage(
                        "content size must not exceed totalElements"
                );
    }

    @Test
    void constructorShouldRejectInconsistentItemCount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageResponse<>(
                        List.of("A"),
                        0,
                        20,
                        2,
                        1,
                        1,
                        true,
                        true,
                        false,
                        false
                ))
                .withMessage(
                        "numberOfItems must match content size"
                );
    }

    @Test
    void constructorShouldRejectInconsistentTotalPages() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageResponse<>(
                        List.of("A"),
                        0,
                        20,
                        1,
                        21,
                        1,
                        true,
                        true,
                        false,
                        false
                ))
                .withMessage(
                        "totalPages does not match "
                                + "totalElements and size"
                );
    }

    @Test
    void constructorShouldRejectInconsistentNavigationMetadata() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PageResponse<>(
                        List.of("A"),
                        0,
                        1,
                        1,
                        2,
                        2,
                        true,
                        true,
                        false,
                        true
                ))
                .withMessage(
                        "last does not match page and totalPages"
                );
    }
}

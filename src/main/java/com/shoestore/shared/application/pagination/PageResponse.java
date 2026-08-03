package com.shoestore.shared.application.pagination;

import java.util.List;
import java.util.Objects;

/**
 * Framework-independent paginated response.
 *
 * @param content       immutable page content
 * @param page          zero-based current page index
 * @param size          requested page size
 * @param numberOfItems actual number of items in this page
 * @param totalElements total number of matching elements
 * @param totalPages    total number of available pages
 * @param first         whether this is the first page
 * @param last          whether this is the last available page
 * @param hasPrevious   whether a previous page can be requested
 * @param hasNext       whether a following page can be requested
 * @param <T>           response item type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int numberOfItems,
        long totalElements,
        long totalPages,
        boolean first,
        boolean last,
        boolean hasPrevious,
        boolean hasNext
) {

    public PageResponse {
        content = List.copyOf(
                Objects.requireNonNull(
                        content,
                        "content must not be null"
                )
        );

        if (page < 0) {
            throw new IllegalArgumentException(
                    "page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > PageRequest.MAXIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "size must be between 1 and "
                            + PageRequest.MAXIMUM_SIZE
            );
        }

        if (numberOfItems != content.size()) {
            throw new IllegalArgumentException(
                    "numberOfItems must match content size"
            );
        }

        if (numberOfItems > size) {
            throw new IllegalArgumentException(
                    "content size must not exceed page size"
            );
        }

        if (totalElements < 0) {
            throw new IllegalArgumentException(
                    "totalElements must be greater than "
                            + "or equal to 0"
            );
        }

        if (numberOfItems > totalElements) {
            throw new IllegalArgumentException(
                    "content size must not exceed totalElements"
            );
        }

        long expectedTotalPages = calculateTotalPages(
                totalElements,
                size
        );

        if (totalPages != expectedTotalPages) {
            throw new IllegalArgumentException(
                    "totalPages does not match "
                            + "totalElements and size"
            );
        }

        boolean expectedFirst = page == 0;
        boolean expectedHasPrevious = page > 0;
        boolean expectedHasNext =
                (long) page + 1 < totalPages;
        boolean expectedLast = !expectedHasNext;

        if (first != expectedFirst) {
            throw new IllegalArgumentException(
                    "first does not match page"
            );
        }

        if (hasPrevious != expectedHasPrevious) {
            throw new IllegalArgumentException(
                    "hasPrevious does not match page"
            );
        }

        if (hasNext != expectedHasNext) {
            throw new IllegalArgumentException(
                    "hasNext does not match page and totalPages"
            );
        }

        if (last != expectedLast) {
            throw new IllegalArgumentException(
                    "last does not match page and totalPages"
            );
        }
    }

    /**
     * Creates a consistent page response and derives all metadata.
     *
     * @param content       page items
     * @param request       original pagination request
     * @param totalElements total number of matching elements
     * @param <T>           response item type
     * @return immutable page response
     */
    public static <T> PageResponse<T> of(
            List<T> content,
            PageRequest request,
            long totalElements
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        List<T> immutableContent = List.copyOf(
                Objects.requireNonNull(
                        content,
                        "content must not be null"
                )
        );

        long totalPages = calculateTotalPages(
                totalElements,
                request.size()
        );

        boolean first = request.page() == 0;
        boolean hasPrevious = request.page() > 0;
        boolean hasNext =
                (long) request.page() + 1 < totalPages;
        boolean last = !hasNext;

        return new PageResponse<>(
                immutableContent,
                request.page(),
                request.size(),
                immutableContent.size(),
                totalElements,
                totalPages,
                first,
                last,
                hasPrevious,
                hasNext
        );
    }

    private static long calculateTotalPages(
            long totalElements,
            int size
    ) {
        if (totalElements < 0) {
            throw new IllegalArgumentException(
                    "totalElements must be greater than "
                            + "or equal to 0"
            );
        }

        if (totalElements == 0) {
            return 0;
        }

        return 1 + ((totalElements - 1) / size);
    }
}

package com.shoestore.shared.application.pagination;

/**
 * Framework-independent pagination request.
 *
 * <p>The page index is zero-based. This contract must not depend on Spring
 * Data, JPA or HTTP-specific types.</p>
 *
 * @param page zero-based page index
 * @param size maximum number of elements requested
 */
public record PageRequest(
        int page,
        int size
) {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAXIMUM_SIZE = 100;

    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > MAXIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "size must be between 1 and "
                            + MAXIMUM_SIZE
            );
        }
    }

    /**
     * Creates the default first-page request.
     *
     * @return page zero with the default page size
     */
    public static PageRequest defaultPage() {
        return new PageRequest(
                DEFAULT_PAGE,
                DEFAULT_SIZE
        );
    }

    /**
     * Returns the zero-based row offset.
     *
     * <p>A long is returned to avoid integer overflow when a large page index
     * is multiplied by the page size.</p>
     *
     * @return row offset
     */
    public long offset() {
        return (long) page * size;
    }
}

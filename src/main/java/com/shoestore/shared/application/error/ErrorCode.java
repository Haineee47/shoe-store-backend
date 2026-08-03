package com.shoestore.shared.application.error;

/**
 * Describes a stable, machine-readable application error.
 *
 * <p>An error code must remain independent of HTTP and other transport
 * technologies. Translation to an HTTP status belongs to the presentation
 * boundary.</p>
 */
public interface ErrorCode {

    /**
     * Returns the stable machine-readable error identifier.
     *
     * @return non-blank error code
     */
    String code();

    /**
     * Returns the safe default message associated with the error.
     *
     * @return non-blank default message
     */
    String defaultMessage();
}

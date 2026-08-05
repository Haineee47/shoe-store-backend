package com.shoestore.customer.domain.model;

/**
 * Business lifecycle status of a customer profile.
 *
 * <p>An active customer may participate in supported business workflows.
 * An inactive customer profile is disabled from business use.</p>
 *
 * <p>This status does not represent authentication state, email
 * verification, authorization, fraud status, or security account state.</p>
 */
public enum CustomerStatus {

    /**
     * Customer profile is enabled for business use.
     */
    ACTIVE,

    /**
     * Customer profile is disabled from business use.
     */
    INACTIVE
}

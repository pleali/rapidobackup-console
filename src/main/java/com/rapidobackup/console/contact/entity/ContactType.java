package com.rapidobackup.console.contact.entity;

public enum ContactType {
    /**
     * Primary contact for the user/tenant
     */
    PRIMARY,

    /**
     * Billing a contact that will get updates about important changes in usage reporting in the platform. There can be several Billing contacts per tenant.
     */
    BILLING,

    /**
     * Technical contact for support and technical issues
     */
    TECHNICAL,

    /**
     * Management contact for overseeing account and service-related matters. There can be several Management contacts per tenant.
     */
    MANAGEMENT
}
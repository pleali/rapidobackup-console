package com.rapidobackup.console.tenant.entity;

public enum SubscriptionPlan {
    /**
     * Starter plan for small businesses
     */
    STARTER,

    /**
     * Professional plan for growing businesses
     */
    PROFESSIONAL,

    /**
     * Enterprise plan for large organizations
     */
    ENTERPRISE,

    /**
     * Custom plan with negotiated terms
     */
    CUSTOM,

    /**
     * Internal system plan (for root/admin tenants)
     */
    INTERNAL
}
package com.rapidobackup.console.tenant.entity;

public enum TenantStatus {
    /**
     * Tenant is active and operational
     */
    ACTIVE,

    /**
     * Tenant is suspended (temporary)
     */
    SUSPENDED,

    /**
     * Tenant is pending closure
     */
    PENDING_CLOSURE,

    /**
     * Tenant is closed but can be reinstated within 90 days
     */
    CLOSED
}
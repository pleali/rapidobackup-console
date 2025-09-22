package com.rapidobackup.console.tenant.entity;

public enum TenantType {
    /**
     * Root administrative tenant (RapidoBackup company)
     */
    ADMIN,

    /**
     * Wholesale distributor tenant
     */
    WHOLESALER,

    /**
     * Partner tenant
     */
    PARTNER,

    /**
     * End client tenant
     */
    CLIENT
}
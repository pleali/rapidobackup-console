package com.rapidobackup.console.user.entity;

public enum UserStatus {
    /**
     * User account is pending activation
     */
    PENDING,

    /**
     * User account is active and operational
     */
    ACTIVE,

    /**
     * User account is suspended (temporary)
     */
    SUSPENDED,

    /**
     * User account is locked due to security reasons
     */
    LOCKED,

    /**
     * User account is deleted (soft delete)
     */
    DELETED
}
package com.rapidobackup.console.audit.entity;

public enum AuditResult {
    /**
     * Operation completed successfully
     */
    SUCCESS,

    /**
     * Operation failed
     */
    FAILURE,

    /**
     * Operation partially completed
     */
    PARTIAL
}
package com.rapidobackup.console.audit.entity;

public enum AuditSeverity {
    /**
     * Informational events
     */
    INFO,

    /**
     * Warning events that should be monitored
     */
    WARNING,

    /**
     * Error events that indicate problems
     */
    ERROR,

    /**
     * Critical events that require immediate attention
     */
    CRITICAL
}
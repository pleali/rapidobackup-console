package com.rapidobackup.console.audit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidobackup.console.tenant.entity.Tenant;
import com.rapidobackup.console.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Événement
    @NotBlank
    @Size(max = 100)
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Size(max = 50)
    @Column(name = "event_category")
    private String eventCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private AuditSeverity severity;

    // Cible de l'action
    @Size(max = 50)
    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Size(max = 255)
    @Column(name = "target_name")
    private String targetName;

    // Détails
    @NotBlank
    @Size(max = 100)
    @Column(name = "action", nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private AuditResult result;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    // Contexte
    @Size(max = 45) // IPv6 compatible
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @Size(max = 255)
    @Column(name = "session_id")
    private String sessionId;

    @Size(max = 255)
    @Column(name = "request_id")
    private String requestId;

    // Données
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    // Timestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AuditLog() {}

    public AuditLog(String eventType, String action) {
        this.eventType = eventType;
        this.action = action;
        this.severity = AuditSeverity.INFO;
        this.result = AuditResult.SUCCESS;
    }

    public AuditLog(String eventType, String action, AuditSeverity severity) {
        this.eventType = eventType;
        this.action = action;
        this.severity = severity;
        this.result = AuditResult.SUCCESS;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Builder pattern for easy creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AuditLog auditLog = new AuditLog();

        public Builder tenant(Tenant tenant) {
            auditLog.tenant = tenant;
            return this;
        }

        public Builder user(User user) {
            auditLog.user = user;
            return this;
        }

        public Builder eventType(String eventType) {
            auditLog.eventType = eventType;
            return this;
        }

        public Builder eventCategory(String eventCategory) {
            auditLog.eventCategory = eventCategory;
            return this;
        }

        public Builder severity(AuditSeverity severity) {
            auditLog.severity = severity;
            return this;
        }

        public Builder target(String targetType, UUID targetId, String targetName) {
            auditLog.targetType = targetType;
            auditLog.targetId = targetId;
            auditLog.targetName = targetName;
            return this;
        }

        public Builder action(String action) {
            auditLog.action = action;
            return this;
        }

        public Builder result(AuditResult result) {
            auditLog.result = result;
            return this;
        }

        public Builder error(String errorMessage) {
            auditLog.errorMessage = errorMessage;
            auditLog.result = AuditResult.FAILURE;
            if (auditLog.severity == null || auditLog.severity == AuditSeverity.INFO) {
                auditLog.severity = AuditSeverity.ERROR;
            }
            return this;
        }

        public Builder context(String ipAddress, String userAgent, String sessionId, String requestId) {
            auditLog.ipAddress = ipAddress;
            auditLog.userAgent = userAgent;
            auditLog.sessionId = sessionId;
            auditLog.requestId = requestId;
            return this;
        }

        public Builder oldValues(Map<String, Object> oldValues) {
            auditLog.oldValues = oldValues != null ? oldValues : new HashMap<>();
            return this;
        }

        public Builder newValues(Map<String, Object> newValues) {
            auditLog.newValues = newValues != null ? newValues : new HashMap<>();
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            auditLog.metadata = metadata != null ? metadata : new HashMap<>();
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            if (auditLog.metadata == null) {
                auditLog.metadata = new HashMap<>();
            }
            auditLog.metadata.put(key, value);
            return this;
        }

        public AuditLog build() {
            if (auditLog.eventType == null || auditLog.action == null) {
                throw new IllegalStateException("eventType and action are required");
            }
            if (auditLog.severity == null) {
                auditLog.severity = AuditSeverity.INFO;
            }
            if (auditLog.result == null) {
                auditLog.result = AuditResult.SUCCESS;
            }
            return auditLog;
        }
    }

    // Helper methods
    public boolean isSuccess() {
        return result == AuditResult.SUCCESS;
    }

    public boolean isFailure() {
        return result == AuditResult.FAILURE;
    }

    public boolean isError() {
        return severity == AuditSeverity.ERROR || severity == AuditSeverity.CRITICAL;
    }

    public boolean hasChanges() {
        return (oldValues != null && !oldValues.isEmpty()) ||
               (newValues != null && !newValues.isEmpty());
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public AuditSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AuditSeverity severity) {
        this.severity = severity;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AuditResult getResult() {
        return result;
    }

    public void setResult(AuditResult result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, Object> getOldValues() {
        return oldValues;
    }

    public void setOldValues(Map<String, Object> oldValues) {
        this.oldValues = oldValues;
    }

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog)) return false;
        return id != null && id.equals(((AuditLog) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AuditLog{" +
            "id=" + id +
            ", eventType='" + eventType + '\'' +
            ", action='" + action + '\'' +
            ", result=" + result +
            ", severity=" + severity +
            ", createdAt=" + createdAt +
            '}';
    }
}
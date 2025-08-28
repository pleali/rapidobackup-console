package com.rapidobackup.console.agent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for Agent management with traditional blocking database access
 * Used for performance comparison with R2DBC Agent entity
 */
@Entity
@Table(name = "agents")
@EntityListeners(AuditingEntityListener.class)
public class AgentJpa {

    @Id
    @GeneratedValue
    // TODO  Use UUID V7 when supported by Hibernate
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "hostname", nullable = false)
    private String hostname;

    @Size(max = 45)
    @Column(name = "ip_address")
    private String ipAddress;

    @NotBlank
    @Size(max = 50)
    @Column(name = "os_type", nullable = false)
    private String osType;

    @Size(max = 100)
    @Column(name = "os_version")
    private String osVersion;

    @NotBlank
    @Size(max = 20)
    @Column(name = "agent_version", nullable = false)
    private String agentVersion;

    @NotBlank
    @Size(max = 64)
    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "api_key_created_date", nullable = false)
    private Instant apiKeyCreatedDate;

    @Column(name = "api_key_expires_date")
    private Instant apiKeyExpiresDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false)
    private Agent.ConnectionType connectionType = Agent.ConnectionType.WEBSOCKET;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Agent.AgentStatus status = Agent.AgentStatus.OFFLINE;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "last_seen")
    private Instant lastSeen;

    @Column(name = "assigned_user_id")
    private UUID assignedUserId;

    @Lob
    @Column(name = "configuration")
    private String configuration;

    @Size(max = 500)
    @Column(name = "tags")
    private String tags;

    @CreatedBy
    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Size(max = 50)
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    // Constructors
    public AgentJpa() {
        this.apiKeyCreatedDate = Instant.now();
    }

    public AgentJpa(String name, String hostname, String osType, String agentVersion, String apiKey) {
        this();
        this.name = name;
        this.hostname = hostname;
        this.osType = osType;
        this.agentVersion = agentVersion;
        this.apiKey = apiKey;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getOsType() { return osType; }
    public void setOsType(String osType) { this.osType = osType; }

    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

    public String getAgentVersion() { return agentVersion; }
    public void setAgentVersion(String agentVersion) { this.agentVersion = agentVersion; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public Instant getApiKeyCreatedDate() { return apiKeyCreatedDate; }
    public void setApiKeyCreatedDate(Instant apiKeyCreatedDate) { this.apiKeyCreatedDate = apiKeyCreatedDate; }

    public Instant getApiKeyExpiresDate() { return apiKeyExpiresDate; }
    public void setApiKeyExpiresDate(Instant apiKeyExpiresDate) { this.apiKeyExpiresDate = apiKeyExpiresDate; }

    public Agent.ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(Agent.ConnectionType connectionType) { this.connectionType = connectionType; }

    public Agent.AgentStatus getStatus() { return status; }
    public void setStatus(Agent.AgentStatus status) { this.status = status; }

    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }

    public UUID getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(UUID assignedUserId) { this.assignedUserId = assignedUserId; }

    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public Instant getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(Instant lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    // Utility methods
    public boolean isApiKeyExpired() {
        return apiKeyExpiresDate != null && Instant.now().isAfter(apiKeyExpiresDate);
    }

    public boolean isOnline() {
        return status == Agent.AgentStatus.ONLINE;
    }

    public boolean hasRecentHeartbeat(long secondsThreshold) {
        if (lastHeartbeat == null) {
            return false;
        }
        return Instant.now().minusSeconds(secondsThreshold).isBefore(lastHeartbeat);
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = Instant.now();
        this.lastSeen = Instant.now();
        if (this.status == Agent.AgentStatus.OFFLINE) {
            this.status = Agent.AgentStatus.ONLINE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentJpa)) return false;
        return id != null && id.equals(((AgentJpa) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AgentJpa{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hostname='" + hostname + '\'' +
                ", osType='" + osType + '\'' +
                ", agentVersion='" + agentVersion + '\'' +
                ", connectionType=" + connectionType +
                ", status=" + status +
                '}';
    }
}
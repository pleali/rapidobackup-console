package com.rapidobackup.console.agent.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

/**
 * R2DBC Entity for Agent management with reactive database access
 */
@Table("agents")
public class Agent {

    @Id
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column("name")
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column("hostname")
    private String hostname;

    @Size(max = 45)
    @Column("ip_address")
    private String ipAddress;

    @NotBlank
    @Size(max = 50)
    @Column("os_type")
    private String osType;

    @Size(max = 100)
    @Column("os_version")
    private String osVersion;

    @NotBlank
    @Size(max = 20)
    @Column("agent_version")
    private String agentVersion;

    @NotBlank
    @Size(max = 64)
    @Column("api_key")
    private String apiKey;

    @Column("api_key_created_date")
    private Instant apiKeyCreatedDate;

    @Column("api_key_expires_date")
    private Instant apiKeyExpiresDate;

    @Column("connection_type")
    private ConnectionType connectionType = ConnectionType.WEBSOCKET;

    @Column("status")
    private AgentStatus status = AgentStatus.OFFLINE;

    @Column("last_heartbeat")
    private Instant lastHeartbeat;

    @Column("last_seen")
    private Instant lastSeen;

    @Column("assigned_user_id")
    private UUID assignedUserId;

    @Column("configuration")
    private String configuration;

    @Size(max = 500)
    @Column("tags")
    private String tags;

    @CreatedBy
    @Size(max = 50)
    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_date")
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Size(max = 50)
    @Column("last_modified_by")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column("last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public enum ConnectionType {
        WEBSOCKET, LONG_POLLING
    }

    public enum AgentStatus {
        ONLINE, OFFLINE, CONNECTING, ERROR, MAINTENANCE
    }

    // Constructors
    public Agent() {
        this.id = UUID.randomUUID();
        this.apiKeyCreatedDate = Instant.now();
    }

    public Agent(String name, String hostname, String osType, String agentVersion, String apiKey) {
        this();
        this.name = name;
        this.hostname = hostname;
        this.osType = osType;
        this.agentVersion = agentVersion;
        this.apiKey = apiKey;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Instant getApiKeyCreatedDate() {
        return apiKeyCreatedDate;
    }

    public void setApiKeyCreatedDate(Instant apiKeyCreatedDate) {
        this.apiKeyCreatedDate = apiKeyCreatedDate;
    }

    public Instant getApiKeyExpiresDate() {
        return apiKeyExpiresDate;
    }

    public void setApiKeyExpiresDate(Instant apiKeyExpiresDate) {
        this.apiKeyExpiresDate = apiKeyExpiresDate;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    // Utility methods
    public boolean isApiKeyExpired() {
        return apiKeyExpiresDate != null && Instant.now().isAfter(apiKeyExpiresDate);
    }

    public boolean isOnline() {
        return status == AgentStatus.ONLINE;
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
        if (this.status == AgentStatus.OFFLINE) {
            this.status = AgentStatus.ONLINE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;
        return id != null && id.equals(((Agent) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Agent{" +
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
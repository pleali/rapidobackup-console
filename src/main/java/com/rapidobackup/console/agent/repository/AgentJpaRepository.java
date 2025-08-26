package com.rapidobackup.console.agent.repository;

import com.rapidobackup.console.agent.entity.Agent;
import com.rapidobackup.console.agent.entity.AgentJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Traditional JPA repository for Agent entities
 * Used for performance comparison with R2DBC AgentRepository
 */
@Repository
public interface AgentJpaRepository extends JpaRepository<AgentJpa, UUID> {

    // Basic blocking queries
    Optional<AgentJpa> findByApiKey(String apiKey);
    
    Optional<AgentJpa> findByHostname(String hostname);
    
    List<AgentJpa> findByStatus(Agent.AgentStatus status);
    
    List<AgentJpa> findByAssignedUserId(UUID assignedUserId);
    
    List<AgentJpa> findByConnectionType(Agent.ConnectionType connectionType);

    // Advanced blocking queries with custom JPQL
    @Query("SELECT a FROM AgentJpa a WHERE a.status = :status AND a.lastHeartbeat > :threshold ORDER BY a.lastHeartbeat DESC")
    List<AgentJpa> findActiveAgentsSince(@Param("status") Agent.AgentStatus status, @Param("threshold") Instant threshold);
    
    @Query("SELECT a FROM AgentJpa a WHERE a.assignedUserId = :userId AND a.status IN ('ONLINE', 'CONNECTING') ORDER BY a.lastHeartbeat DESC")
    List<AgentJpa> findOnlineAgentsByUser(@Param("userId") UUID userId);
    
    @Query("SELECT a FROM AgentJpa a WHERE a.osType = :osType AND a.agentVersion = :version")
    List<AgentJpa> findByOsTypeAndVersion(@Param("osType") String osType, @Param("version") String version);
    
    long countByStatus(Agent.AgentStatus status);
    
    long countByAssignedUserId(UUID userId);
    
    // Complex queries with pagination for large datasets
    @Query("""
        SELECT a FROM AgentJpa a 
        WHERE (a.lastHeartbeat IS NULL OR a.lastHeartbeat < :threshold)
        AND a.status != 'OFFLINE'
        ORDER BY a.lastSeen DESC NULLS LAST
        """)
    Page<AgentJpa> findStaleAgents(@Param("threshold") Instant threshold, Pageable pageable);
    
    @Query("""
        SELECT a FROM AgentJpa a 
        WHERE a.assignedUserId = :userId 
        AND (
            LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.hostname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY a.lastHeartbeat DESC NULLS LAST
        """)
    Page<AgentJpa> searchAgentsByUser(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Bulk operations
    @Modifying
    @Query("UPDATE AgentJpa a SET a.status = :newStatus WHERE a.status = :currentStatus")
    int updateStatusBulk(@Param("currentStatus") Agent.AgentStatus currentStatus, @Param("newStatus") Agent.AgentStatus newStatus);
    
    @Modifying
    @Query("UPDATE AgentJpa a SET a.lastHeartbeat = :timestamp WHERE a.apiKey = :apiKey")
    int updateHeartbeat(@Param("apiKey") String apiKey, @Param("timestamp") Instant timestamp);
    
    // API key management
    List<AgentJpa> findByApiKeyExpiresDateBefore(Instant expirationDate);
    
    @Query("SELECT a FROM AgentJpa a WHERE a.apiKeyExpiresDate IS NOT NULL AND a.apiKeyExpiresDate < :now")
    List<AgentJpa> findExpiredApiKeys(@Param("now") Instant now);
    
    // Cleanup operations  
    @Modifying
    @Query("DELETE FROM AgentJpa a WHERE a.status = 'OFFLINE' AND a.lastSeen < :threshold")
    int cleanupOldOfflineAgents(@Param("threshold") Instant threshold);

    // Statistics queries (blocking)
    @Query("""
        SELECT a.status, COUNT(a) 
        FROM AgentJpa a 
        GROUP BY a.status
        """)
    List<Object[]> getStatusStatistics();
}
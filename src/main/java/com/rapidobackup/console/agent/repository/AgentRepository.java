package com.rapidobackup.console.agent.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidobackup.console.agent.entity.Agent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive R2DBC repository for Agent entities
 * Demonstrates reactive database access patterns for high-concurrency scenarios
 */
@Repository
public interface AgentRepository extends R2dbcRepository<Agent, UUID> {

    // Basic reactive queries
    Mono<Agent> findByApiKey(String apiKey);
    
    Mono<Agent> findByHostname(String hostname);
    
    Flux<Agent> findByStatus(Agent.AgentStatus status);
    
    Flux<Agent> findByAssignedUserId(UUID assignedUserId);
    
    Flux<Agent> findByConnectionType(Agent.ConnectionType connectionType);

    // Advanced reactive queries with custom SQL
    @Query("SELECT * FROM agents WHERE status = :status AND last_heartbeat > :threshold ORDER BY last_heartbeat DESC")
    Flux<Agent> findActiveAgentsSince(@Param("status") String status, @Param("threshold") Instant threshold);
    
    @Query("SELECT * FROM agents WHERE assigned_user_id = :userId AND status IN ('ONLINE', 'CONNECTING') ORDER BY last_heartbeat DESC")
    Flux<Agent> findOnlineAgentsByUser(@Param("userId") UUID userId);
    
    @Query("SELECT * FROM agents WHERE os_type = :osType AND agent_version = :version")
    Flux<Agent> findByOsTypeAndVersion(@Param("osType") String osType, @Param("version") String version);
    
    @Query("SELECT COUNT(*) FROM agents WHERE status = :status")
    Mono<Long> countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(*) FROM agents WHERE assigned_user_id = :userId")
    Mono<Long> countByAssignedUserId(@Param("userId") UUID userId);
    
    // Complex queries for monitoring and dashboard
    @Query("""
        SELECT * FROM agents 
        WHERE (last_heartbeat IS NULL OR last_heartbeat < :threshold)
        AND status != 'OFFLINE'
        ORDER BY last_seen DESC NULLS LAST
        """)
    Flux<Agent> findStaleAgents(@Param("threshold") Instant threshold);
    
    @Query("""
        SELECT * FROM agents 
        WHERE assigned_user_id = :userId 
        AND (
            LOWER(name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(hostname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY last_heartbeat DESC NULLS LAST
        """)
    Flux<Agent> searchAgentsByUser(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);
    
    // Bulk operations for performance testing
    @Query("UPDATE agents SET status = :newStatus WHERE status = :currentStatus")
    Mono<Integer> updateStatusBulk(@Param("currentStatus") String currentStatus, @Param("newStatus") String newStatus);
    
    @Query("UPDATE agents SET last_heartbeat = :timestamp WHERE api_key = :apiKey")
    Mono<Integer> updateHeartbeat(@Param("apiKey") String apiKey, @Param("timestamp") Instant timestamp);
    
    // Statistics queries for performance comparison
    @Query("SELECT COUNT(*) FROM agents")
    Mono<Long> countAll();
    
    @Query("SELECT status FROM agents GROUP BY status")
    Flux<String> findAllStatuses();
    
    // API key management
    Flux<Agent> findByApiKeyExpiresDateBefore(Instant expirationDate);
    
    @Query("SELECT * FROM agents WHERE api_key_expires_date IS NOT NULL AND api_key_expires_date < :now")
    Flux<Agent> findExpiredApiKeys(@Param("now") Instant now);
    
    // Soft cleanup operations  
    @Query("DELETE FROM agents WHERE status = 'OFFLINE' AND last_seen < :threshold")
    Mono<Integer> cleanupOldOfflineAgents(@Param("threshold") Instant threshold);
}
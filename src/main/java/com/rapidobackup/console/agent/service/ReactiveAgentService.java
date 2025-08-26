package com.rapidobackup.console.agent.service;

import com.rapidobackup.console.agent.entity.Agent;
import com.rapidobackup.console.agent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Reactive service for Agent management demonstrating R2DBC benefits:
 * - Non-blocking I/O for high concurrency (1000+ agents)
 * - Reactive streams for real-time monitoring
 * - Efficient bulk operations
 * - WebSocket-compatible reactive patterns
 */
@Service
@Transactional("reactiveTransactionManager")
public class ReactiveAgentService {

    private final AgentRepository agentRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public ReactiveAgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    // Basic CRUD operations with reactive patterns
    public Mono<Agent> createAgent(Agent agent) {
        agent.setApiKey(generateSecureApiKey());
        agent.setApiKeyCreatedDate(Instant.now());
        agent.setCreatedDate(Instant.now());
        
        return agentRepository.save(agent)
                .doOnSuccess(savedAgent -> 
                    System.out.println("Agent created reactively: " + savedAgent.getName()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)));
    }

    public Mono<Agent> findById(UUID agentId) {
        return agentRepository.findById(agentId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Agent not found: " + agentId)));
    }

    public Mono<Agent> findByApiKey(String apiKey) {
        return agentRepository.findByApiKey(apiKey)
                .filter(agent -> !agent.isApiKeyExpired())
                .switchIfEmpty(Mono.error(new SecurityException("Invalid or expired API key")));
    }

    // Reactive streaming for real-time monitoring
    public Flux<Agent> streamOnlineAgents() {
        return agentRepository.findByStatus(Agent.AgentStatus.ONLINE)
                .delayElements(Duration.ofMillis(100)) // Simulate real-time streaming
                .filter(agent -> agent.hasRecentHeartbeat(60)); // Only agents with recent heartbeat
    }

    public Flux<Agent> streamAgentsByUser(UUID userId) {
        return agentRepository.findOnlineAgentsByUser(userId)
                .take(Duration.ofMinutes(5)); // Stream for 5 minutes
    }

    // High-performance bulk operations
    public Mono<Long> processHeartbeatBatch(Flux<String> apiKeyStream) {
        Instant now = Instant.now();
        
        return apiKeyStream
                .flatMap(apiKey -> 
                    agentRepository.updateHeartbeat(apiKey, now)
                        .onErrorResume(error -> {
                            System.err.println("Failed to update heartbeat for " + apiKey + ": " + error.getMessage());
                            return Mono.just(0);
                        })
                )
                .reduce(0, Integer::sum)
                .map(Integer::longValue);
    }

    // Reactive monitoring and health checks
    public Flux<Agent> monitorStaleAgents(Duration staleThreshold) {
        Instant threshold = Instant.now().minus(staleThreshold);
        
        return agentRepository.findStaleAgents(threshold)
                .doOnNext(agent -> {
                    System.out.println("Stale agent detected: " + agent.getName() + 
                                     " (last seen: " + agent.getLastSeen() + ")");
                })
                .flatMap(this::markAgentAsOffline);
    }

    private Mono<Agent> markAgentAsOffline(Agent agent) {
        agent.setStatus(Agent.AgentStatus.OFFLINE);
        agent.setLastModifiedDate(Instant.now());
        return agentRepository.save(agent);
    }

    // Advanced search with reactive filtering
    public Flux<Agent> searchAgentsReactively(UUID userId, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return agentRepository.findByAssignedUserId(userId);
        }
        
        return agentRepository.searchAgentsByUser(userId, searchTerm.trim())
                .filter(agent -> !agent.isApiKeyExpired())
                .sort((a1, a2) -> {
                    // Sort by status priority, then by last heartbeat
                    int statusCompare = getStatusPriority(a1.getStatus()) - getStatusPriority(a2.getStatus());
                    if (statusCompare != 0) return statusCompare;
                    
                    if (a1.getLastHeartbeat() == null && a2.getLastHeartbeat() == null) return 0;
                    if (a1.getLastHeartbeat() == null) return 1;
                    if (a2.getLastHeartbeat() == null) return -1;
                    return a2.getLastHeartbeat().compareTo(a1.getLastHeartbeat());
                });
    }

    private int getStatusPriority(Agent.AgentStatus status) {
        return switch (status) {
            case ONLINE -> 1;
            case CONNECTING -> 2;
            case ERROR -> 3;
            case MAINTENANCE -> 4;
            case OFFLINE -> 5;
        };
    }

    // Statistics and analytics
    public Mono<AgentStatistics> getStatistics() {
        Mono<Long> totalCount = agentRepository.countAll();
        Mono<Long> onlineCount = agentRepository.countByStatus(Agent.AgentStatus.ONLINE.name());
        Mono<Long> offlineCount = agentRepository.countByStatus(Agent.AgentStatus.OFFLINE.name());
        
        return Mono.zip(totalCount, onlineCount, offlineCount)
                .map(tuple -> new AgentStatistics(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    // Connection type management for WebSocket/Long Polling fallback
    public Mono<Agent> switchConnectionType(UUID agentId, Agent.ConnectionType newType) {
        return agentRepository.findById(agentId)
                .flatMap(agent -> {
                    agent.setConnectionType(newType);
                    agent.setLastModifiedDate(Instant.now());
                    return agentRepository.save(agent);
                })
                .doOnSuccess(agent -> 
                    System.out.println("Agent " + agent.getName() + 
                                     " switched to " + newType + " connection"));
    }

    // API key management with reactive security
    public Mono<Agent> rotateApiKey(UUID agentId) {
        return agentRepository.findById(agentId)
                .flatMap(agent -> {
                    agent.setApiKey(generateSecureApiKey());
                    agent.setApiKeyCreatedDate(Instant.now());
                    // Set expiration to 90 days from now
                    agent.setApiKeyExpiresDate(Instant.now().plus(Duration.ofDays(90)));
                    agent.setLastModifiedDate(Instant.now());
                    return agentRepository.save(agent);
                });
    }

    public Flux<Agent> cleanupExpiredApiKeys() {
        return agentRepository.findExpiredApiKeys(Instant.now())
                .flatMap(this::rotateApiKey);
    }

    // Performance testing helpers
    public Mono<Long> performanceBulkInsert(int agentCount) {
        return Flux.range(1, agentCount)
                .map(i -> createTestAgent("TestAgent" + i, "host" + i + ".test.com"))
                .flatMap(agentRepository::save)
                .count()
                .doOnSuccess(count -> 
                    System.out.println("Bulk inserted " + count + " agents reactively"));
    }

    private Agent createTestAgent(String name, String hostname) {
        Agent agent = new Agent();
        agent.setName(name);
        agent.setHostname(hostname);
        agent.setOsType("Linux");
        agent.setAgentVersion("1.0.0");
        agent.setApiKey(generateSecureApiKey());
        agent.setConnectionType(Agent.ConnectionType.WEBSOCKET);
        agent.setStatus(Agent.AgentStatus.OFFLINE);
        return agent;
    }

    private String generateSecureApiKey() {
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
    }

    // Statistics DTO
    public static class AgentStatistics {
        private final long totalAgents;
        private final long onlineAgents;
        private final long offlineAgents;

        public AgentStatistics(long totalAgents, long onlineAgents, long offlineAgents) {
            this.totalAgents = totalAgents;
            this.onlineAgents = onlineAgents;
            this.offlineAgents = offlineAgents;
        }

        public long getTotalAgents() { return totalAgents; }
        public long getOnlineAgents() { return onlineAgents; }
        public long getOfflineAgents() { return offlineAgents; }
        public double getOnlinePercentage() { 
            return totalAgents > 0 ? (double) onlineAgents / totalAgents * 100 : 0; 
        }
    }
}
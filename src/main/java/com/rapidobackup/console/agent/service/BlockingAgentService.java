package com.rapidobackup.console.agent.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.agent.entity.Agent;
import com.rapidobackup.console.agent.entity.AgentJpa;
import com.rapidobackup.console.agent.repository.AgentJpaRepository;

/**
 * Traditional blocking service for Agent management using JPA
 * Used for performance comparison with ReactiveAgentService
 * 
 * Demonstrates blocking I/O patterns and thread consumption
 */
@Service
@Transactional
public class BlockingAgentService {

    private final AgentJpaRepository agentJpaRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public BlockingAgentService(AgentJpaRepository agentJpaRepository) {
        this.agentJpaRepository = agentJpaRepository;
    }

    // Basic CRUD operations with blocking patterns
    public AgentJpa createAgent(AgentJpa agent) {
        agent.setApiKey(generateSecureApiKey());
        agent.setApiKeyCreatedDate(Instant.now());
        agent.setCreatedDate(Instant.now());
        
        try {
            return agentJpaRepository.save(agent);
        } catch (Exception e) {
            System.err.println("Failed to create agent: " + e.getMessage());
            throw new RuntimeException("Agent creation failed", e);
        }
    }

    public Optional<AgentJpa> findById(UUID agentId) {
        return agentJpaRepository.findById(agentId);
    }

    public Optional<AgentJpa> findByApiKey(String apiKey) {
        Optional<AgentJpa> agent = agentJpaRepository.findByApiKey(apiKey);
        return agent.filter(a -> !a.isApiKeyExpired());
    }

    // Blocking monitoring operations
    public List<AgentJpa> getOnlineAgents() {
        return agentJpaRepository.findByStatus(Agent.AgentStatus.ONLINE)
                .stream()
                .filter(agent -> agent.hasRecentHeartbeat(60))
                .toList();
    }

    public List<AgentJpa> getAgentsByUser(UUID userId) {
        return agentJpaRepository.findOnlineAgentsByUser(userId);
    }

    // Blocking bulk operations (thread-consuming)
    public long processHeartbeatBatch(List<String> apiKeys) {
        Instant now = Instant.now();
        long updatedCount = 0;
        
        for (String apiKey : apiKeys) {
            try {
                int updated = agentJpaRepository.updateHeartbeat(apiKey, now);
                updatedCount += updated;
            } catch (Exception e) {
                System.err.println("Failed to update heartbeat for " + apiKey + ": " + e.getMessage());
            }
        }
        
        return updatedCount;
    }

    // Blocking approach to handle stale agents
    public List<AgentJpa> processStaleAgents(Duration staleThreshold, int pageSize) {
        Instant threshold = Instant.now().minus(staleThreshold);
        Pageable pageable = PageRequest.of(0, pageSize);
        
        Page<AgentJpa> staleAgentsPage = agentJpaRepository.findStaleAgents(threshold, pageable);
        List<AgentJpa> staleAgents = staleAgentsPage.getContent();
        
        // Process each stale agent (blocking)
        staleAgents.forEach(agent -> {
            System.out.println("Processing stale agent: " + agent.getName() + 
                             " (last seen: " + agent.getLastSeen() + ")");
            markAgentAsOffline(agent);
        });
        
        return staleAgents;
    }

    private void markAgentAsOffline(AgentJpa agent) {
        agent.setStatus(Agent.AgentStatus.OFFLINE);
        agent.setLastModifiedDate(Instant.now());
        agentJpaRepository.save(agent);
    }

    // Blocking search with pagination
    public Page<AgentJpa> searchAgentsBlocking(UUID userId, String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
//            List<AgentJpa> agents = agentJpaRepository.findByAssignedUserId(userId);
            return Page.empty(pageable);
        }
        
        return agentJpaRepository.searchAgentsByUser(userId, searchTerm.trim(), pageable);
    }

    // Statistics with blocking queries
    public AgentStatistics getStatistics() {
        long totalCount = agentJpaRepository.count();
        long onlineCount = agentJpaRepository.countByStatus(Agent.AgentStatus.ONLINE);
        long offlineCount = agentJpaRepository.countByStatus(Agent.AgentStatus.OFFLINE);
        
        return new AgentStatistics(totalCount, onlineCount, offlineCount);
    }

    // Blocking connection type management
    public Optional<AgentJpa> switchConnectionType(UUID agentId, Agent.ConnectionType newType) {
        Optional<AgentJpa> agentOpt = agentJpaRepository.findById(agentId);
        
        if (agentOpt.isPresent()) {
            AgentJpa agent = agentOpt.get();
            agent.setConnectionType(newType);
            agent.setLastModifiedDate(Instant.now());
            AgentJpa saved = agentJpaRepository.save(agent);
            
            System.out.println("Agent " + agent.getName() + 
                             " switched to " + newType + " connection");
            return Optional.of(saved);
        }
        
        return Optional.empty();
    }

    // API key management with blocking operations
    public Optional<AgentJpa> rotateApiKey(UUID agentId) {
        Optional<AgentJpa> agentOpt = agentJpaRepository.findById(agentId);
        
        if (agentOpt.isPresent()) {
            AgentJpa agent = agentOpt.get();
            agent.setApiKey(generateSecureApiKey());
            agent.setApiKeyCreatedDate(Instant.now());
            agent.setApiKeyExpiresDate(Instant.now().plus(Duration.ofDays(90)));
            agent.setLastModifiedDate(Instant.now());
            return Optional.of(agentJpaRepository.save(agent));
        }
        
        return Optional.empty();
    }

    public List<AgentJpa> cleanupExpiredApiKeys() {
        List<AgentJpa> expiredAgents = agentJpaRepository.findExpiredApiKeys(Instant.now());
        
        return expiredAgents.stream()
                .map(agent -> {
                    rotateApiKey(agent.getId());
                    return agentJpaRepository.findById(agent.getId()).orElse(agent);
                })
                .toList();
    }

    // Performance testing - blocking bulk insert
    public long performanceBulkInsert(int agentCount) {
        long startTime = System.currentTimeMillis();
        
        List<AgentJpa> agents = IntStream.range(1, agentCount + 1)
                .mapToObj(i -> createTestAgent("BlockingTestAgent" + i, "host" + i + ".test.com"))
                .toList();
        
        List<AgentJpa> savedAgents = agentJpaRepository.saveAll(agents);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Blocking bulk insert of " + savedAgents.size() + 
                          " agents took " + (endTime - startTime) + "ms");
        
        return savedAgents.size();
    }

    // Async version for comparison (still blocking underneath)
    public CompletableFuture<Long> performanceBulkInsertAsync(int agentCount) {
        return CompletableFuture.supplyAsync(() -> performanceBulkInsert(agentCount));
    }

    // Simulate concurrent load - this will consume many threads
    public void simulateConcurrentLoad(int concurrentRequests) {
        List<CompletableFuture<Void>> futures = IntStream.range(0, concurrentRequests)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    // Each thread will block on database operations
                    getStatistics();
                    getOnlineAgents();
                    processHeartbeatBatch(List.of("dummy-key-" + i));
                }))
                .toList();
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("Completed " + concurrentRequests + " concurrent blocking operations");
    }

    private AgentJpa createTestAgent(String name, String hostname) {
        AgentJpa agent = new AgentJpa();
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

    // Statistics DTO (same as reactive version)
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
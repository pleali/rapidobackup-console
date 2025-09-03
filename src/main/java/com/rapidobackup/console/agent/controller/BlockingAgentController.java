package com.rapidobackup.console.agent.controller;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rapidobackup.console.agent.entity.Agent;
import com.rapidobackup.console.agent.entity.AgentJpa;
import com.rapidobackup.console.agent.service.BlockingAgentService;

/**
 * Traditional blocking REST controller using JPA
 * Used for performance comparison with ReactiveAgentController
 * 
 * Each request consumes a thread until completion
 */
@RestController
@RequestMapping("/api/blocking/agents")
public class BlockingAgentController {

    private final BlockingAgentService agentService;

    public BlockingAgentController(BlockingAgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentJpa> getAgent(@PathVariable UUID id) {
        return agentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api-key/{apiKey}")
    public ResponseEntity<AgentJpa> getAgentByApiKey(@PathVariable String apiKey) {
        return agentService.findByApiKey(apiKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgentJpa> createAgent(@RequestBody AgentJpa agent) {
        try {
            AgentJpa created = agentService.createAgent(agent);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Blocking list endpoints (no streaming)
    @GetMapping("/online")
    public ResponseEntity<List<AgentJpa>> getOnlineAgents() {
        List<AgentJpa> agents = agentService.getOnlineAgents();
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AgentJpa>> getAgentsByUser(@PathVariable UUID userId) {
        List<AgentJpa> agents = agentService.getAgentsByUser(userId);
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AgentJpa>> searchAgents(
            @RequestParam UUID userId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AgentJpa> agents = agentService.searchAgentsBlocking(userId, searchTerm, page, size);
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/statistics")
    public ResponseEntity<BlockingAgentService.AgentStatistics> getStatistics() {
        BlockingAgentService.AgentStatistics stats = agentService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}/connection-type")
    public ResponseEntity<AgentJpa> switchConnectionType(
            @PathVariable UUID id,
            @RequestParam Agent.ConnectionType connectionType) {
        return agentService.switchConnectionType(id, connectionType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/rotate-api-key")
    public ResponseEntity<AgentJpa> rotateApiKey(@PathVariable UUID id) {
        return agentService.rotateApiKey(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Performance testing endpoints
    @PostMapping("/performance/bulk-insert")
    public ResponseEntity<String> performanceBulkInsert(@RequestParam int count) {
        long insertedCount = agentService.performanceBulkInsert(count);
        return ResponseEntity.ok("Inserted " + insertedCount + " agents using blocking JPA");
    }

    @PostMapping("/performance/bulk-insert-async")
    public CompletableFuture<ResponseEntity<String>> performanceBulkInsertAsync(@RequestParam int count) {
        return agentService.performanceBulkInsertAsync(count)
                .thenApply(insertedCount -> 
                    ResponseEntity.ok("Inserted " + insertedCount + " agents using async blocking JPA"));
    }

    @PostMapping("/heartbeat/batch")
    public ResponseEntity<String> batchHeartbeat(@RequestBody List<String> apiKeys) {
        long updatedCount = agentService.processHeartbeatBatch(apiKeys);
        return ResponseEntity.ok("Updated " + updatedCount + " heartbeats using blocking approach");
    }

    @GetMapping("/monitor/stale")
    public ResponseEntity<List<AgentJpa>> getStaleAgents(
            @RequestParam(defaultValue = "60") long staleThresholdSeconds,
            @RequestParam(defaultValue = "100") int pageSize) {
        List<AgentJpa> staleAgents = agentService.processStaleAgents(
            Duration.ofSeconds(staleThresholdSeconds), pageSize);
        return ResponseEntity.ok(staleAgents);
    }

    @PostMapping("/cleanup/expired-keys")
    public ResponseEntity<List<AgentJpa>> cleanupExpiredApiKeys() {
        List<AgentJpa> updatedAgents = agentService.cleanupExpiredApiKeys();
        return ResponseEntity.ok(updatedAgents);
    }

    // Load testing endpoint
    @PostMapping("/performance/concurrent-load")
    public ResponseEntity<String> simulateConcurrentLoad(@RequestParam int concurrentRequests) {
        long startTime = System.currentTimeMillis();
        agentService.simulateConcurrentLoad(concurrentRequests);
        long endTime = System.currentTimeMillis();
        
        String message = String.format("Completed %d concurrent blocking requests in %dms", 
                                     concurrentRequests, (endTime - startTime));
        return ResponseEntity.ok(message);
    }
}
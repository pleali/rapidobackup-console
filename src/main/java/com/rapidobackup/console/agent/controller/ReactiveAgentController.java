package com.rapidobackup.console.agent.controller;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.MediaType;
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
import com.rapidobackup.console.agent.service.ReactiveAgentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST controller demonstrating R2DBC + WebFlux integration
 * Shows non-blocking HTTP endpoints for agent management
 */
@RestController
@RequestMapping("/api/reactive/agents")
public class ReactiveAgentController {

    private final ReactiveAgentService agentService;

    public ReactiveAgentController(ReactiveAgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Agent>> getAgent(@PathVariable UUID id) {
        return agentService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/api-key/{apiKey}")
    public Mono<ResponseEntity<Agent>> getAgentByApiKey(@PathVariable String apiKey) {
        return agentService.findByApiKey(apiKey)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Agent>> createAgent(@RequestBody Agent agent) {
        return agentService.createAgent(agent)
                .map(created -> ResponseEntity.status(201).body(created));
    }

    // Server-Sent Events endpoint for real-time monitoring
    @GetMapping(value = "/stream/online", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Agent> streamOnlineAgents() {
        return agentService.streamOnlineAgents();
    }

    @GetMapping(value = "/stream/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Agent> streamAgentsByUser(@PathVariable UUID userId) {
        return agentService.streamAgentsByUser(userId);
    }

    @GetMapping("/search")
    public Flux<Agent> searchAgents(
            @RequestParam UUID userId,
            @RequestParam(required = false) String searchTerm) {
        return agentService.searchAgentsReactively(userId, searchTerm);
    }

    @GetMapping("/statistics")
    public Mono<ReactiveAgentService.AgentStatistics> getStatistics() {
        return agentService.getStatistics();
    }

    @PutMapping("/{id}/connection-type")
    public Mono<ResponseEntity<Agent>> switchConnectionType(
            @PathVariable UUID id,
            @RequestParam Agent.ConnectionType connectionType) {
        return agentService.switchConnectionType(id, connectionType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/rotate-api-key")
    public Mono<ResponseEntity<Agent>> rotateApiKey(@PathVariable UUID id) {
        return agentService.rotateApiKey(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Performance testing endpoints
    @PostMapping("/performance/bulk-insert")
    public Mono<ResponseEntity<String>> performanceBulkInsert(@RequestParam int count) {
        return agentService.performanceBulkInsert(count)
                .map(insertedCount -> ResponseEntity.ok("Inserted " + insertedCount + " agents reactively"));
    }

    @PostMapping("/heartbeat/batch")
    public Mono<ResponseEntity<String>> batchHeartbeat(@RequestBody Flux<String> apiKeys) {
        return agentService.processHeartbeatBatch(apiKeys)
                .map(updatedCount -> ResponseEntity.ok("Updated " + updatedCount + " heartbeats"));
    }

    @GetMapping(value = "/monitor/stale", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Agent> monitorStaleAgents(@RequestParam(defaultValue = "60") long staleThresholdSeconds) {
        return agentService.monitorStaleAgents(Duration.ofSeconds(staleThresholdSeconds));
    }

    @PostMapping("/cleanup/expired-keys")
    public Flux<Agent> cleanupExpiredApiKeys() {
        return agentService.cleanupExpiredApiKeys();
    }
}
package com.rapidobackup.console.agent.performance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.rapidobackup.console.agent.entity.Agent;
import com.rapidobackup.console.agent.entity.AgentJpa;
import com.rapidobackup.console.agent.service.BlockingAgentService;
import com.rapidobackup.console.agent.service.ReactiveAgentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Comprehensive performance comparison between JPA and R2DBC for Agent operations
 * 
 * Tests cover:
 * - Bulk insert performance
 * - Concurrent read operations
 * - Memory usage patterns
 * - Thread consumption
 * - Scalability limits
 */
@SpringBootTest
@ActiveProfiles("test")
public class AgentPerformanceComparisonTest {

    @Autowired
    private ReactiveAgentService reactiveAgentService;
    
    @Autowired
    private BlockingAgentService blockingAgentService;

    private static final int SMALL_BATCH_SIZE = 100;
    private static final int MEDIUM_BATCH_SIZE = 1000;
    private static final int LARGE_BATCH_SIZE = 10000;
    private static final int CONCURRENT_REQUESTS = 100;

    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Starting Agent Performance Comparison Tests");
        System.out.println("=".repeat(80));
    }

    @Test
    void testBulkInsertPerformance() {
        System.out.println("\n🔬 BULK INSERT PERFORMANCE COMPARISON");
        System.out.println("-".repeat(50));

        // Test different batch sizes
        int[] batchSizes = {SMALL_BATCH_SIZE, MEDIUM_BATCH_SIZE};
        
        for (int batchSize : batchSizes) {
            System.out.println(String.format("\n📊 Testing bulk insert with %d agents:", batchSize));
            
            // R2DBC Test
            long reactiveStartTime = System.currentTimeMillis();
            StepVerifier.create(reactiveAgentService.performanceBulkInsert(batchSize))
                    .expectNext((long) batchSize)
                    .verifyComplete();
            long reactiveEndTime = System.currentTimeMillis();
            long reactiveTime = reactiveEndTime - reactiveStartTime;

            // JPA Test  
            long blockingStartTime = System.currentTimeMillis();
            long blockingResult = blockingAgentService.performanceBulkInsert(batchSize);
            long blockingEndTime = System.currentTimeMillis();
            long blockingTime = blockingEndTime - blockingStartTime;

            // Results
            System.out.println(String.format("  📈 R2DBC:  %dms (%d agents)", reactiveTime, batchSize));
            System.out.println(String.format("  📊 JPA:    %dms (%d agents)", blockingTime, blockingResult));
            System.out.println(String.format("  🚀 Winner: %s (%.1fx faster)", 
                reactiveTime < blockingTime ? "R2DBC" : "JPA",
                (double) Math.max(reactiveTime, blockingTime) / Math.min(reactiveTime, blockingTime)));
        }
    }

    @Test
    void testConcurrentReadPerformance() throws InterruptedException {
        System.out.println("\n🔬 CONCURRENT READ PERFORMANCE COMPARISON");
        System.out.println("-".repeat(50));

        // Create some test data first
        StepVerifier.create(reactiveAgentService.performanceBulkInsert(50))
                .expectNext(50L)
                .verifyComplete();

        // Test R2DBC concurrent reads
        System.out.println(String.format("\n📊 Testing %d concurrent statistics requests:", CONCURRENT_REQUESTS));
        
        long reactiveStartTime = System.currentTimeMillis();
        List<Mono<ReactiveAgentService.AgentStatistics>> reactiveRequests = IntStream.range(0, CONCURRENT_REQUESTS)
                .mapToObj(i -> reactiveAgentService.getStatistics())
                .toList();

        StepVerifier.create(Flux.merge(reactiveRequests))
                .expectNextCount(CONCURRENT_REQUESTS)
                .verifyComplete();
        long reactiveEndTime = System.currentTimeMillis();
        long reactiveTime = reactiveEndTime - reactiveStartTime;

        // Test JPA concurrent reads with thread pool
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_REQUESTS);
        
        long blockingStartTime = System.currentTimeMillis();
        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    blockingAgentService.getStatistics();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        long blockingEndTime = System.currentTimeMillis();
        long blockingTime = blockingEndTime - blockingStartTime;
        
        executor.shutdown();

        // Results
        System.out.println(String.format("  📈 R2DBC:  %dms (%d requests)", reactiveTime, CONCURRENT_REQUESTS));
        System.out.println(String.format("  📊 JPA:    %dms (%d requests)", blockingTime, CONCURRENT_REQUESTS));
        System.out.println(String.format("  🚀 Winner: %s (%.1fx faster)", 
            reactiveTime < blockingTime ? "R2DBC" : "JPA",
            (double) Math.max(reactiveTime, blockingTime) / Math.min(reactiveTime, blockingTime)));
    }

    @Test
    void testMemoryUsagePattern() {
        System.out.println("\n🔬 MEMORY USAGE PATTERN COMPARISON");
        System.out.println("-".repeat(50));

        Runtime runtime = Runtime.getRuntime();
        
        // Baseline memory
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("  📊 Baseline memory: %.2f MB", baselineMemory / 1024.0 / 1024.0));

        // R2DBC Memory Test
        System.gc();
        long beforeReactive = runtime.totalMemory() - runtime.freeMemory();
        
        StepVerifier.create(reactiveAgentService.performanceBulkInsert(1000))
                .expectNext(1000L)
                .verifyComplete();
        
        System.gc();
        long afterReactive = runtime.totalMemory() - runtime.freeMemory();
        long reactiveMemoryUsed = afterReactive - beforeReactive;

        // JPA Memory Test
        System.gc();
        long beforeBlocking = runtime.totalMemory() - runtime.freeMemory();
        
        blockingAgentService.performanceBulkInsert(1000);
        
        System.gc();
        long afterBlocking = runtime.totalMemory() - runtime.freeMemory();
        long blockingMemoryUsed = afterBlocking - beforeBlocking;

        // Results
        System.out.println(String.format("  📈 R2DBC memory:  %.2f MB", reactiveMemoryUsed / 1024.0 / 1024.0));
        System.out.println(String.format("  📊 JPA memory:    %.2f MB", blockingMemoryUsed / 1024.0 / 1024.0));
        System.out.println(String.format("  🚀 Winner: %s (%.1fx less memory)", 
            reactiveMemoryUsed < blockingMemoryUsed ? "R2DBC" : "JPA",
            (double) Math.max(reactiveMemoryUsed, blockingMemoryUsed) / Math.min(reactiveMemoryUsed, blockingMemoryUsed)));
    }

    @Test
    void testStreamingVsBatchPerformance() {
        System.out.println("\n🔬 STREAMING vs BATCH PERFORMANCE");
        System.out.println("-".repeat(50));

        // Create test data
        StepVerifier.create(reactiveAgentService.performanceBulkInsert(1000))
                .expectNext(1000L)
                .verifyComplete();

        // R2DBC Streaming Test
        long streamingStartTime = System.currentTimeMillis();
        StepVerifier.create(reactiveAgentService.streamOnlineAgents().take(100))
                .expectNextCount(100)
                .verifyComplete();
        long streamingEndTime = System.currentTimeMillis();
        long streamingTime = streamingEndTime - streamingStartTime;

        // JPA Batch Test (equivalent operation)
        long batchStartTime = System.currentTimeMillis();
        List<AgentJpa> onlineAgents = blockingAgentService.getOnlineAgents();
        long batchEndTime = System.currentTimeMillis();
        long batchTime = batchEndTime - batchStartTime;

        // Results
        System.out.println(String.format("  📈 R2DBC Streaming: %dms (first 100 agents)", streamingTime));
        System.out.println(String.format("  📊 JPA Batch:      %dms (%d agents)", batchTime, onlineAgents.size()));
        System.out.println(String.format("  💡 R2DBC advantage: Early results, backpressure support"));
    }

    @Test
    void testHeartbeatBatchPerformance() {
        System.out.println("\n🔬 HEARTBEAT BATCH PERFORMANCE");
        System.out.println("-".repeat(50));

        // Create test agents
        List<String> testApiKeys = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Agent agent = createTestAgent("HeartbeatTest" + i);
            StepVerifier.create(reactiveAgentService.createAgent(agent))
                    .assertNext(createdAgent -> testApiKeys.add(createdAgent.getApiKey()))
                    .verifyComplete();
        }

        // R2DBC Batch Test
        long reactiveStartTime = System.currentTimeMillis();
        StepVerifier.create(reactiveAgentService.processHeartbeatBatch(Flux.fromIterable(testApiKeys)))
                .expectNextCount(1)
                .verifyComplete();
        long reactiveEndTime = System.currentTimeMillis();
        long reactiveTime = reactiveEndTime - reactiveStartTime;

        // JPA Batch Test
        long blockingStartTime = System.currentTimeMillis();
        long updatedCount = blockingAgentService.processHeartbeatBatch(testApiKeys);
        long blockingEndTime = System.currentTimeMillis();
        long blockingTime = blockingEndTime - blockingStartTime;

        // Results
        System.out.println(String.format("  📈 R2DBC:  %dms (%d heartbeats)", reactiveTime, testApiKeys.size()));
        System.out.println(String.format("  📊 JPA:    %dms (%d heartbeats)", blockingTime, updatedCount));
        System.out.println(String.format("  🚀 Winner: %s (%.1fx faster)", 
            reactiveTime < blockingTime ? "R2DBC" : "JPA",
            (double) Math.max(reactiveTime, blockingTime) / Math.min(reactiveTime, blockingTime)));
    }

    @Test
    void testScalabilityLimits() {
        System.out.println("\n🔬 SCALABILITY LIMITS COMPARISON");
        System.out.println("-".repeat(50));

        // Test different load levels
        int[] loadLevels = {10, 50, 100};
        
        for (int loadLevel : loadLevels) {
            System.out.println(String.format("\n📊 Testing %d concurrent operations:", loadLevel));
            
            // R2DBC Scalability Test
            long reactiveStartTime = System.currentTimeMillis();
            List<Mono<ReactiveAgentService.AgentStatistics>> reactiveOps = IntStream.range(0, loadLevel)
                    .mapToObj(i -> reactiveAgentService.getStatistics())
                    .toList();
            
            StepVerifier.create(Flux.merge(reactiveOps).limitRate(10)) // Concurrency of 10
                    .expectNextCount(loadLevel)
                    .verifyComplete();
            long reactiveEndTime = System.currentTimeMillis();
            long reactiveTime = reactiveEndTime - reactiveStartTime;

            // JPA Scalability Test (with limited thread pool to simulate real conditions)
            ExecutorService limitedExecutor = Executors.newFixedThreadPool(10);
            CountDownLatch latch = new CountDownLatch(loadLevel);
            
            long blockingStartTime = System.currentTimeMillis();
            for (int i = 0; i < loadLevel; i++) {
                limitedExecutor.submit(() -> {
                    try {
                        blockingAgentService.getStatistics();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            long blockingEndTime = System.currentTimeMillis();
            long blockingTime = blockingEndTime - blockingStartTime;
            limitedExecutor.shutdown();

            System.out.println(String.format("  📈 R2DBC:  %dms", reactiveTime));
            System.out.println(String.format("  📊 JPA:    %dms", blockingTime));
            System.out.println(String.format("  🚀 Winner: %s (%.1fx faster)", 
                reactiveTime < blockingTime ? "R2DBC" : "JPA",
                (double) Math.max(reactiveTime, blockingTime) / Math.min(reactiveTime, blockingTime)));
        }
    }

    private Agent createTestAgent(String name) {
        Agent agent = new Agent();
        agent.setName(name);
        agent.setHostname(name.toLowerCase() + ".test.com");
        agent.setOsType("Linux");
        agent.setAgentVersion("1.0.0");
        agent.setConnectionType(Agent.ConnectionType.WEBSOCKET);
        agent.setStatus(Agent.AgentStatus.ONLINE);
        agent.setLastHeartbeat(Instant.now());
        return agent;
    }
}
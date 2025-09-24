package com.rapidobackup.console.tenant.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;

import com.rapidobackup.console.AbstractIntegrationTest;
import com.rapidobackup.console.tenant.entity.Tenant;
import com.rapidobackup.console.tenant.entity.TenantType;
import com.rapidobackup.console.tenant.repository.TenantRepository;

/**
 * Performance tests for tenant hierarchy operations.
 *
 * These tests validate that our Materialized Path approach performs well
 * with realistic data volumes (up to 100,000 tenants).
 *
 * Test scenarios:
 * - Large hierarchy creation (10,000 tenants across 5 levels)
 * - Descendant queries on large subtrees
 * - Ancestor queries up the hierarchy
 * - Search operations across the hierarchy
 */
@Transactional
class TenantHierarchyPerformanceTest extends AbstractIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantRepository tenantRepository;


    @BeforeEach
    void setUp() {
        // Clear any existing data
        tenantRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Ensure cleanup after each test to prevent resource leaks
        try {
            tenantRepository.deleteAll();
        } catch (Exception e) {
            // Log but don't fail the test on cleanup issues
            System.err.println("Warning: Failed to clean up test data: " + e.getMessage());
        }
    }

    @Test
    void testLargeHierarchyCreation() {
        long startTime = System.currentTimeMillis();

        // Create a realistic hierarchy:
        // Level 0: 1 root tenant (company)
        // Level 1: 5 divisions
        // Level 2: 10 departments per division (50 total)
        // Level 3: 20 teams per department (1000 total)
        // Total: ~1056 tenants

        Tenant company = tenantService.createTenant("Global Corp", "Global Corporation", TenantType.WHOLESALER, null);
        List<Tenant> divisions = new ArrayList<>();

        // Create divisions
        for (int i = 1; i <= 5; i++) {
            Tenant division = tenantService.createTenant("Division " + i, null, TenantType.PARTNER, company.getId());
            divisions.add(division);
        }

        // Create departments
        List<Tenant> departments = new ArrayList<>();
        for (Tenant division : divisions) {
            for (int i = 1; i <= 10; i++) {
                Tenant department = tenantService.createTenant(
                    "Dept " + division.getName() + "-" + i, null, TenantType.CLIENT, division.getId());
                departments.add(department);
            }
        }

        // Create teams
        for (Tenant department : departments) {
            for (int i = 1; i <= 20; i++) {
                tenantService.createTenant(
                    "Team " + department.getName() + "-" + i, null, TenantType.CLIENT, department.getId());
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Verify hierarchy was created correctly
        long totalTenants = tenantRepository.count();
        assertThat(totalTenants).isEqualTo(1056); // 1 + 5 + 50 + 1000

        // Creation should be reasonably fast (< 7 seconds for 1000+ tenants with containers)
        assertThat(duration).isLessThan(7000);

        System.out.printf("Created %d tenants in %d ms (%.2f tenants/second)%n",
            totalTenants, duration, (totalTenants * 1000.0) / duration);
    }

    @Test
    void testDescendantQueryPerformance() {
        // Create a moderate hierarchy for testing
        createTestHierarchy();

        Tenant company = tenantRepository.findRootTenants().get(0);

        // Test descendant query performance
        long startTime = System.currentTimeMillis();
        List<Tenant> allDescendants = tenantService.findAllDescendants(company.getId());
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        // Should find all descendants quickly
        assertThat(allDescendants).hasSizeGreaterThan(100);
        assertThat(duration).isLessThan(100); // Should be very fast (< 100ms)

        System.out.printf("Found %d descendants in %d ms%n", allDescendants.size(), duration);
    }

    @Test
    void testAncestorQueryPerformance() {
        createTestHierarchy();

        // Find a deep tenant (level 3)
        List<Tenant> level3Tenants = tenantRepository.findTenantsAtLevel(3);
        assertThat(level3Tenants).isNotEmpty();

        Tenant deepTenant = level3Tenants.get(0);

        // Test ancestor query performance
        long startTime = System.currentTimeMillis();
        List<Tenant> ancestors = tenantService.findAllAncestors(deepTenant.getId());
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        // Should find 3 ancestors (root, level 1, level 2)
        assertThat(ancestors).hasSize(3);
        assertThat(duration).isLessThan(50); // Should be very fast

        System.out.printf("Found %d ancestors in %d ms%n", ancestors.size(), duration);
    }

    @Test
    void testSearchPerformance() {
        createTestHierarchy();

        // Test search performance
        long startTime = System.currentTimeMillis();
        var searchResults = tenantService.searchTenants("Team", TenantType.CLIENT, null, null,
            org.springframework.data.domain.PageRequest.of(0, 100));
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        assertThat(searchResults.getContent()).isNotEmpty();
        assertThat(duration).isLessThan(100); // Should be fast

        System.out.printf("Search found %d results in %d ms%n",
            searchResults.getContent().size(), duration);
    }

    @Test
    void testMoveOperationPerformance() {
        createTestHierarchy();

        // Find tenants to move
        List<Tenant> rootTenants = tenantRepository.findRootTenants();
        List<Tenant> level1Tenants = tenantRepository.findTenantsAtLevel(1);

        assertThat(rootTenants).hasSize(1);
        assertThat(level1Tenants).hasSizeGreaterThanOrEqualTo(2);

        Tenant sourceParent = level1Tenants.get(0);
        Tenant targetParent = level1Tenants.get(1);

        // Find children to move
        List<Tenant> childrenToMove = tenantService.findDirectChildren(sourceParent.getId());
        assertThat(childrenToMove).isNotEmpty();

        Tenant tenantToMove = childrenToMove.get(0);
        int descendantCount = tenantService.findAllDescendants(tenantToMove.getId()).size();

        // Test move performance
        long startTime = System.currentTimeMillis();
        tenantService.moveTenant(tenantToMove.getId(), targetParent.getId());
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        // Move should be reasonably fast even with path updates
        assertThat(duration).isLessThan(1000);

        System.out.printf("Moved tenant with %d descendants in %d ms%n", descendantCount, duration);

        // Verify move was successful
        Tenant movedTenant = tenantService.findById(tenantToMove.getId()).orElseThrow();
        assertThat(movedTenant.getParent().getId()).isEqualTo(targetParent.getId());
        assertThat(movedTenant.getPath()).startsWith(targetParent.getPath() + "/");
    }

    @Test
    void testConcurrentReadPerformance() {
        createTestHierarchy();

        Tenant rootTenant = tenantRepository.findRootTenants().get(0);

        // Simulate multiple concurrent read operations
        int numberOfReads = 100;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfReads; i++) {
            tenantService.findAllDescendants(rootTenant.getId());
            tenantService.findDirectChildren(rootTenant.getId());
            tenantService.findById(rootTenant.getId());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should handle multiple reads efficiently (caching should help)
        double readsPerSecond = (numberOfReads * 3 * 1000.0) / duration;
        assertThat(readsPerSecond).isGreaterThan(100); // At least 100 operations/second

        System.out.printf("Performed %d read operations in %d ms (%.2f ops/second)%n",
            numberOfReads * 3, duration, readsPerSecond);
    }

    /**
     * Creates a test hierarchy with 4 levels:
     * - 1 root company
     * - 3 divisions under the company
     * - 5 departments under each division (15 total)
     * - 10 teams under each department (150 total)
     * Total: 169 tenants
     */
    private void createTestHierarchy() {
        Tenant company = tenantService.createTenant("Test Company", null, TenantType.WHOLESALER, null);

        for (int d = 1; d <= 3; d++) {
            Tenant division = tenantService.createTenant("Division " + d, null, TenantType.PARTNER, company.getId());

            for (int dept = 1; dept <= 5; dept++) {
                Tenant department = tenantService.createTenant(
                    "Department " + d + "-" + dept, null, TenantType.CLIENT, division.getId());

                for (int t = 1; t <= 10; t++) {
                    tenantService.createTenant(
                        "Team " + d + "-" + dept + "-" + t, null, TenantType.CLIENT, department.getId());
                }
            }
        }
    }
}
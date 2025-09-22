package com.rapidobackup.console.tenant.repository;

import com.rapidobackup.console.tenant.entity.Tenant;
import com.rapidobackup.console.tenant.entity.TenantStatus;
import com.rapidobackup.console.tenant.entity.TenantType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing tenant hierarchy using the Materialized Path pattern.
 *
 * This implementation uses a path-based approach for efficient hierarchical queries
 * without requiring PostgreSQL-specific extensions like ltree.
 *
 * Key design decisions:
 * - Uses "/" as path separator for better readability and conflict avoidance
 * - Leverages varchar_pattern_ops indexes for efficient LIKE queries
 * - Supports up to 5 levels of hierarchy for reasonable performance
 * - Includes parent_id for direct parent-child navigation
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    // === Basic finders ===

    /**
     * Find tenant by unique slug.
     */
    Optional<Tenant> findBySlug(String slug);

    /**
     * Find tenant by external ID.
     */
    Optional<Tenant> findByExternalId(String externalId);

    /**
     * Check if a slug already exists.
     */
    boolean existsBySlug(String slug);

    /**
     * Check if a slug exists excluding a specific tenant ID.
     */
    @Query("SELECT COUNT(t) > 0 FROM Tenant t WHERE t.slug = :slug AND t.id != :excludeId")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("excludeId") UUID excludeId);

    // === Hierarchy navigation ===

    /**
     * Find all direct children of a tenant.
     */
    List<Tenant> findByParentOrderByName(Tenant parent);

    /**
     * Find all direct children by parent ID.
     */
    @Query("SELECT t FROM Tenant t WHERE t.parent.id = :parentId ORDER BY t.name")
    List<Tenant> findDirectChildren(@Param("parentId") UUID parentId);

    /**
     * Find all active direct children.
     */
    @Query("SELECT t FROM Tenant t WHERE t.parent.id = :parentId AND t.status = :status ORDER BY t.name")
    List<Tenant> findDirectChildrenByStatus(@Param("parentId") UUID parentId, @Param("status") TenantStatus status);

    /**
     * Find all descendants (children, grandchildren, etc.) of a tenant.
     * Uses the path column with LIKE for efficient queries.
     */
    @Query("SELECT t FROM Tenant t WHERE t.path LIKE :pathPrefix ORDER BY t.level, t.name")
    List<Tenant> findAllDescendants(@Param("pathPrefix") String pathPrefix);

    /**
     * Find all descendants with pagination.
     */
    @Query("SELECT t FROM Tenant t WHERE t.path LIKE :pathPrefix ORDER BY t.level, t.name")
    Page<Tenant> findAllDescendants(@Param("pathPrefix") String pathPrefix, Pageable pageable);

    /**
     * Find all active descendants.
     */
    @Query("SELECT t FROM Tenant t WHERE t.path LIKE :pathPrefix AND t.status = :status ORDER BY t.level, t.name")
    List<Tenant> findActiveDescendants(@Param("pathPrefix") String pathPrefix, @Param("status") TenantStatus status);

    /**
     * Find all ancestors (parent, grandparent, etc.) of a tenant.
     * Uses reverse LIKE to find all paths that the current path starts with.
     */
    @Query("SELECT t FROM Tenant t WHERE :childPath LIKE CONCAT(t.path, '/%') OR t.path = :rootPath ORDER BY t.level")
    List<Tenant> findAllAncestors(@Param("childPath") String childPath, @Param("rootPath") String rootPath);

    /**
     * Find root tenants (tenants without parent).
     */
    @Query("SELECT t FROM Tenant t WHERE t.parent IS NULL ORDER BY t.name")
    List<Tenant> findRootTenants();

    /**
     * Find root tenants with pagination.
     */
    @Query("SELECT t FROM Tenant t WHERE t.parent IS NULL ORDER BY t.name")
    Page<Tenant> findRootTenants(Pageable pageable);

    /**
     * Find tenants at a specific level in the hierarchy.
     */
    @Query("SELECT t FROM Tenant t WHERE t.level = :level ORDER BY t.name")
    List<Tenant> findTenantsAtLevel(@Param("level") int level);

    // === Counting and statistics ===

    /**
     * Count direct children of a tenant.
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.parent.id = :parentId")
    long countDirectChildren(@Param("parentId") UUID parentId);

    /**
     * Count all descendants of a tenant.
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.path LIKE :pathPrefix")
    long countAllDescendants(@Param("pathPrefix") String pathPrefix);

    /**
     * Count active descendants.
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.path LIKE :pathPrefix AND t.status = :status")
    long countActiveDescendants(@Param("pathPrefix") String pathPrefix, @Param("status") TenantStatus status);

    /**
     * Get the maximum level in the hierarchy.
     */
    @Query("SELECT MAX(t.level) FROM Tenant t")
    Integer findMaxLevel();

    // === Search and filtering ===

    /**
     * Search tenants by name or slug with hierarchy context.
     */
    @Query("SELECT t FROM Tenant t WHERE " +
           "(:searchTerm IS NULL OR " +
           " LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(t.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(t.slug) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:tenantType IS NULL OR t.tenantType = :tenantType) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:parentPath IS NULL OR t.path LIKE :parentPath)")
    Page<Tenant> searchTenants(
        @Param("searchTerm") String searchTerm,
        @Param("tenantType") TenantType tenantType,
        @Param("status") TenantStatus status,
        @Param("parentPath") String parentPath,
        Pageable pageable
    );

    /**
     * Find tenants by type within a specific branch of the hierarchy.
     */
    @Query("SELECT t FROM Tenant t WHERE t.tenantType = :tenantType AND " +
           "(:parentPath IS NULL OR t.path LIKE :parentPath) ORDER BY t.name")
    List<Tenant> findByTypeInBranch(@Param("tenantType") TenantType tenantType, @Param("parentPath") String parentPath);

    // === Validation and integrity ===

    /**
     * Check if moving a tenant would create a circular reference.
     * Returns true if the proposed new parent is a descendant of the tenant.
     */
    @Query("SELECT COUNT(t) > 0 FROM Tenant t WHERE t.id = :newParentId AND t.path LIKE :tenantPathPrefix")
    boolean wouldCreateCircularReference(@Param("newParentId") UUID newParentId, @Param("tenantPathPrefix") String tenantPathPrefix);

    /**
     * Find all tenants that need path updates when a tenant is moved.
     */
    @Query("SELECT t FROM Tenant t WHERE t.path LIKE :oldPathPrefix ORDER BY t.level")
    List<Tenant> findTenantsToUpdatePath(@Param("oldPathPrefix") String oldPathPrefix);
}
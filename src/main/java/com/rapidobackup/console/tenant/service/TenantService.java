package com.rapidobackup.console.tenant.service;

import com.rapidobackup.console.common.util.SlugUtils;
import com.rapidobackup.console.tenant.entity.Tenant;
import com.rapidobackup.console.tenant.entity.TenantStatus;
import com.rapidobackup.console.tenant.entity.TenantType;
import com.rapidobackup.console.tenant.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing tenant hierarchy using the Materialized Path pattern.
 *
 * This service provides high-level operations for tenant management including:
 * - CRUD operations with automatic slug generation
 * - Hierarchical navigation (children, descendants, ancestors)
 * - Tenant movement with path recalculation
 * - Caching for frequently accessed data
 * - Business rule validation
 *
 * Design considerations:
 * - Uses "/" as path separator (more readable than ".")
 * - Limits hierarchy depth to 5 levels for performance
 * - Implements caching for read-heavy operations
 * - Ensures data integrity during hierarchy modifications
 */
@Service
@Transactional
public class TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantService.class);
    private static final int MAX_HIERARCHY_DEPTH = 5;

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // === CRUD Operations ===

    /**
     * Create a new tenant with automatic slug generation and path calculation.
     */
    public Tenant createTenant(String name, String displayName, TenantType tenantType, UUID parentId) {
        log.debug("Creating tenant: name={}, type={}, parentId={}", name, tenantType, parentId);

        // Generate unique slug
        String slug = SlugUtils.generateUniqueSlug(name, tenantRepository::existsBySlug);

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setDisplayName(displayName != null ? displayName : name);
        tenant.setSlug(slug);
        tenant.setTenantType(tenantType);
        tenant.setStatus(TenantStatus.ACTIVE);

        // Set parent and validate hierarchy depth
        if (parentId != null) {
            Tenant parent = findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent tenant not found: " + parentId));

            if (parent.getLevel() >= MAX_HIERARCHY_DEPTH - 1) {
                throw new IllegalArgumentException("Maximum hierarchy depth exceeded. Current max level: " + MAX_HIERARCHY_DEPTH);
            }

            tenant.setParent(parent);
        }

        // Save and clear cache
        tenant = tenantRepository.save(tenant);
        clearHierarchyCache();

        log.info("Created tenant: id={}, slug={}, path={}", tenant.getId(), tenant.getSlug(), tenant.getPath());
        return tenant;
    }

    /**
     * Update tenant with slug regeneration if name changed.
     */
    public Tenant updateTenant(UUID tenantId, String name, String displayName, TenantType tenantType) {
        log.debug("Updating tenant: id={}, name={}, type={}", tenantId, name, tenantType);

        Tenant tenant = findById(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        boolean nameChanged = !tenant.getName().equals(name);
        tenant.setName(name);
        tenant.setDisplayName(displayName != null ? displayName : name);
        tenant.setTenantType(tenantType);

        // Regenerate slug if name changed
        if (nameChanged) {
            String newSlug = SlugUtils.generateUniqueSlug(name,
                slug -> tenantRepository.existsBySlugAndIdNot(slug, tenantId));
            tenant.setSlug(newSlug);
        }

        tenant = tenantRepository.save(tenant);
        clearHierarchyCache();

        log.info("Updated tenant: id={}, slug={}", tenant.getId(), tenant.getSlug());
        return tenant;
    }

    /**
     * Move tenant to a new parent, recalculating paths for all descendants.
     */
    public Tenant moveTenant(UUID tenantId, UUID newParentId) {
        log.debug("Moving tenant: id={}, newParentId={}", tenantId, newParentId);

        Tenant tenant = findById(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        Tenant newParent = null;
        if (newParentId != null) {
            newParent = findById(newParentId)
                .orElseThrow(() -> new IllegalArgumentException("New parent tenant not found: " + newParentId));

            // Validate hierarchy depth
            if (newParent.getLevel() >= MAX_HIERARCHY_DEPTH - 1) {
                throw new IllegalArgumentException("Moving tenant would exceed maximum hierarchy depth");
            }

            // Check for circular reference
            if (tenantRepository.wouldCreateCircularReference(newParentId, tenant.getPath() + Tenant.PATH_SEPARATOR + "%")) {
                throw new IllegalArgumentException("Cannot move tenant: would create circular reference");
            }
        }

        // Store old path for updating descendants
        String oldPath = tenant.getPath();
    String oldPathPrefix = oldPath + Tenant.PATH_SEPARATOR + "%";

        // Update tenant's parent
        tenant.setParent(newParent);
        tenant = tenantRepository.save(tenant); // This triggers path recalculation

        // Update paths for all descendants
        List<Tenant> descendantsToUpdate = tenantRepository.findTenantsToUpdatePath(oldPathPrefix);
        for (Tenant descendant : descendantsToUpdate) {
            // Replace old path prefix with new path
            String newDescendantPath = descendant.getPath().replace(oldPath, tenant.getPath());
            descendant.setPath(newDescendantPath);
            descendant.setLevel(countPathSegments(newDescendantPath));
        }

        if (!descendantsToUpdate.isEmpty()) {
            tenantRepository.saveAll(descendantsToUpdate);
            log.info("Updated paths for {} descendants", descendantsToUpdate.size());
        }

        clearHierarchyCache();
        log.info("Moved tenant: id={}, oldPath={}, newPath={}", tenantId, oldPath, tenant.getPath());
        return tenant;
    }

    /**
     * Soft delete tenant and all its descendants.
     */
    @CacheEvict(value = {"tenants", "tenantHierarchy"}, allEntries = true)
    public void deleteTenant(UUID tenantId, boolean cascade) {
        log.debug("Deleting tenant: id={}, cascade={}", tenantId, cascade);

        Tenant tenant = findById(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        if (cascade) {
            // Soft delete all descendants
            List<Tenant> descendants = findAllDescendants(tenantId);
            for (Tenant descendant : descendants) {
                descendant.setStatus(TenantStatus.CLOSED);
                descendant.setDeletedAt(Instant.now());
            }
            tenantRepository.saveAll(descendants);
            log.info("Soft deleted {} descendants", descendants.size());
        } else {
            // Check if tenant has children
            long childrenCount = tenantRepository.countDirectChildren(tenantId);
            if (childrenCount > 0) {
                throw new IllegalArgumentException("Cannot delete tenant with children. Use cascade=true or move children first.");
            }
        }

        // Soft delete the tenant itself
        tenant.setStatus(TenantStatus.CLOSED);
        tenant.setDeletedAt(Instant.now());
        tenantRepository.save(tenant);

        log.info("Soft deleted tenant: id={}", tenantId);
    }

    // === Hierarchy Navigation ===

    @Cacheable(value = "tenants", key = "#tenantId")
    public Optional<Tenant> findById(UUID tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Cacheable(value = "tenants", key = "#slug")
    public Optional<Tenant> findBySlug(String slug) {
        return tenantRepository.findBySlug(slug);
    }

    @Cacheable(value = "tenantHierarchy", key = "'roots'")
    public List<Tenant> findRootTenants() {
        return tenantRepository.findRootTenants();
    }

    @Cacheable(value = "tenantHierarchy", key = "'children:' + #parentId")
    public List<Tenant> findDirectChildren(UUID parentId) {
        return tenantRepository.findDirectChildren(parentId);
    }

    @Cacheable(value = "tenantHierarchy", key = "'descendants:' + #parentId")
    public List<Tenant> findAllDescendants(UUID parentId) {
        Tenant parent = findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent tenant not found: " + parentId));
    String pathPrefix = parent.getPath() + Tenant.PATH_SEPARATOR + "%";
        return tenantRepository.findAllDescendants(pathPrefix);
    }

    @Cacheable(value = "tenantHierarchy", key = "'ancestors:' + #tenantId")
    public List<Tenant> findAllAncestors(UUID tenantId) {
        Tenant tenant = findById(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        // Extract root path (first segment)
    String[] pathSegments = tenant.getPath().split(Tenant.PATH_SEPARATOR);
        String rootPath = pathSegments[0];

        return tenantRepository.findAllAncestors(tenant.getPath(), rootPath);
    }

    public List<Tenant> findActiveDescendants(UUID parentId) {
        Tenant parent = findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent tenant not found: " + parentId));
    String pathPrefix = parent.getPath() + Tenant.PATH_SEPARATOR + "%";
        return tenantRepository.findActiveDescendants(pathPrefix, TenantStatus.ACTIVE);
    }

    // === Search and Statistics ===

    public Page<Tenant> searchTenants(String searchTerm, TenantType tenantType, TenantStatus status,
                                     UUID parentId, Pageable pageable) {
        String parentPath = null;
        if (parentId != null) {
            Tenant parent = findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent tenant not found: " + parentId));
            parentPath = parent.getPath() + Tenant.PATH_SEPARATOR + "%";
        }

        return tenantRepository.searchTenants(searchTerm, tenantType, status, parentPath, pageable);
    }

    public long countDirectChildren(UUID parentId) {
        return tenantRepository.countDirectChildren(parentId);
    }

    public long countAllDescendants(UUID parentId) {
        Tenant parent = findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent tenant not found: " + parentId));
    String pathPrefix = parent.getPath() + Tenant.PATH_SEPARATOR + "%";
        return tenantRepository.countAllDescendants(pathPrefix);
    }

    // === Utility Methods ===

    /**
     * Generate a unique slug for a tenant name.
     */
    public String generateUniqueSlug(String name) {
        return SlugUtils.generateUniqueSlug(name, tenantRepository::existsBySlug);
    }

    /**
     * Validate if a slug is available.
     */
    public boolean isSlugAvailable(String slug, UUID excludeTenantId) {
        if (excludeTenantId != null) {
            return !tenantRepository.existsBySlugAndIdNot(slug, excludeTenantId);
        }
        return !tenantRepository.existsBySlug(slug);
    }

    /**
     * Get the current maximum hierarchy depth.
     */
    public int getCurrentMaxLevel() {
        Integer maxLevel = tenantRepository.findMaxLevel();
        return maxLevel != null ? maxLevel : 0;
    }

    /**
     * Check if a tenant can be moved to a new parent without violating business rules.
     */
    public boolean canMoveTenant(UUID tenantId, UUID newParentId) {
        if (newParentId == null) {
            return true; // Can always move to root
        }

        Optional<Tenant> newParentOpt = findById(newParentId);
        if (newParentOpt.isEmpty()) {
            return false; // Parent doesn't exist
        }

        Tenant newParent = newParentOpt.get();
        if (newParent.getLevel() >= MAX_HIERARCHY_DEPTH - 1) {
            return false; // Would exceed max depth
        }

        Optional<Tenant> tenantOpt = findById(tenantId);
        if (tenantOpt.isEmpty()) {
            return false; // Tenant doesn't exist
        }

        Tenant tenant = tenantOpt.get();
    String tenantPathPrefix = tenant.getPath() + Tenant.PATH_SEPARATOR + "%";

        return !tenantRepository.wouldCreateCircularReference(newParentId, tenantPathPrefix);
    }

    // === Private Methods ===

    @CacheEvict(value = {"tenants", "tenantHierarchy"}, allEntries = true)
    private void clearHierarchyCache() {
        log.debug("Cleared tenant hierarchy cache");
    }

    private int countPathSegments(String path) {
        if (path == null || path.isEmpty()) {
            return 0;
        }
    return path.split(Tenant.PATH_SEPARATOR).length - 1;
    }
}
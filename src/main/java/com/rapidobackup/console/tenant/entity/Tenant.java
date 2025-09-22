package com.rapidobackup.console.tenant.entity;

import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tenants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"slug"}),
    @UniqueConstraint(columnNames = {"external_id"})
})
@SQLDelete(sql = "UPDATE tenants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Tenant {

    public static final String PATH_SEPARATOR = "/";

    @Id
    @GeneratedValue
    private UUID id;

    // Identification
    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Size(max = 255)
    @Column(name = "external_id", unique = true)
    private String externalId;

    // Hiérarchie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Tenant parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Tenant> children = new HashSet<>();

    @Column(name = "level", nullable = false)
    private Integer level = 0;

    @Column(name = "path")
    private String path;

    // Classification
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_type", nullable = false)
    private TenantType tenantType;

    @Size(max = 100)
    @Column(name = "industry")
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "size_category")
    private TenantSizeCategory sizeCategory;

    // Configuration
    @Size(max = 50)
    @Column(name = "timezone")
    private String timezone = "UTC";

    @Size(max = 10)
    @Column(name = "locale")
    private String locale = "en-US";

    @Size(max = 3)
    @Column(name = "currency")
    private String currency = "EUR";

    @Size(max = 2)
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be ISO 3166-1 alpha-2")
    @Column(name = "country")
    private String country;

    // Limites et Quotas
    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_agents")
    private Integer maxAgents;

    @Column(name = "max_storage_gb")
    private BigInteger maxStorageGb;

    @Column(name = "used_storage_gb")
    private BigInteger usedStorageGb = BigInteger.ZERO;

    // Billing
    @Email
    @Size(max = 255)
    @Column(name = "billing_contact_email")
    private String billingContactEmail;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "billing_address", columnDefinition = "jsonb")
    private Map<String, Object> billingAddress = new HashMap<>();

    @Size(max = 100)
    @Column(name = "contract_number")
    private String contractNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "subscription_expires_at")
    private Instant subscriptionExpiresAt;

    // État et Audit
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "suspension_reason", columnDefinition = "text")
    private String suspensionReason;

    // Metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "jsonb")
    private Map<String, Object> settings = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_attributes", columnDefinition = "jsonb")
    private Map<String, Object> customAttributes = new HashMap<>();

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "suspended_at")
    private Instant suspendedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Tenant() {}
    public Tenant(String name, String slug, TenantType tenantType) {
        this.name = name;
        this.displayName = name;
        this.slug = slug;
        this.tenantType = tenantType;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == TenantStatus.ACTIVE && activatedAt == null) {
            activatedAt = Instant.now();
        }
        updatePath();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        if (status == TenantStatus.ACTIVE && activatedAt == null) {
            activatedAt = Instant.now();
        }
        if (status == TenantStatus.SUSPENDED && suspendedAt == null) {
            suspendedAt = Instant.now();
        }
        updatePath();
    }

    private void updatePath() {
        if (parent == null) {
            this.path = slug;
            this.level = 0;
        } else {
            this.path = parent.getPath() + PATH_SEPARATOR + slug;
            this.level = parent.getLevel() + 1;
        }
    }

    // Helper methods
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isChild() {
        return parent != null;
    }

    public String getFullName() {
        if (displayName != null && !displayName.equals(name)) {
            return displayName;
        }
        return name;
    }

    public boolean isActive() {
        return status == TenantStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return status == TenantStatus.SUSPENDED;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Tenant getParent() {
        return parent;
    }

    public void setParent(Tenant parent) {
        this.parent = parent;
    }

    public Set<Tenant> getChildren() {
        return children;
    }

    public void setChildren(Set<Tenant> children) {
        this.children = children;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TenantType getTenantType() {
        return tenantType;
    }

    public void setTenantType(TenantType tenantType) {
        this.tenantType = tenantType;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public TenantSizeCategory getSizeCategory() {
        return sizeCategory;
    }

    public void setSizeCategory(TenantSizeCategory sizeCategory) {
        this.sizeCategory = sizeCategory;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getMaxAgents() {
        return maxAgents;
    }

    public void setMaxAgents(Integer maxAgents) {
        this.maxAgents = maxAgents;
    }

    public BigInteger getMaxStorageGb() {
        return maxStorageGb;
    }

    public void setMaxStorageGb(BigInteger maxStorageGb) {
        this.maxStorageGb = maxStorageGb;
    }

    public BigInteger getUsedStorageGb() {
        return usedStorageGb;
    }

    public void setUsedStorageGb(BigInteger usedStorageGb) {
        this.usedStorageGb = usedStorageGb;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

    public void setBillingContactEmail(String billingContactEmail) {
        this.billingContactEmail = billingContactEmail;
    }

    public Map<String, Object> getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Map<String, Object> billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public Instant getSubscriptionExpiresAt() {
        return subscriptionExpiresAt;
    }

    public void setSubscriptionExpiresAt(Instant subscriptionExpiresAt) {
        this.subscriptionExpiresAt = subscriptionExpiresAt;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public String getSuspensionReason() {
        return suspensionReason;
    }

    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Instant getSuspendedAt() {
        return suspendedAt;
    }

    public void setSuspendedAt(Instant suspendedAt) {
        this.suspendedAt = suspendedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tenant)) return false;
        return id != null && id.equals(((Tenant) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", slug='" + slug + '\'' +
            ", tenantType=" + tenantType +
            ", status=" + status +
            '}';
    }
}
package com.rapidobackup.console.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidobackup.console.contact.entity.Contact;
import com.rapidobackup.console.tenant.entity.Tenant;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"}),
    @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant tenant;

    // Relation optionnelle vers contact (0-1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    @JsonIgnore
    private Contact contact;

    // Identification
    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Size(max = 100)
    @Column(name = "employee_id")
    private String employeeId;

    @Size(max = 255)
    @Column(name = "external_id")
    private String externalId;

    // Personal Profile
    @NotBlank
    @Size(max = 255)
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Size(max = 100)
    @Column(name = "nickname")
    private String nickname;

    @Size(max = 100)
    @Column(name = "preferred_name")
    private String preferredName;

    // Professional Profile
    @Size(max = 255)
    @Column(name = "division")
    private String division;

    // Préférences

    @Size(max = 10)
    @Column(name = "locale")
    private String locale;


    // Sécurité et Accès
    @JsonIgnore
    @Size(max = 100)
    @Column(name = "password_hash")
    private String passwordHash;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @NotNull
    private Set<UserRole> roles = new HashSet<>();

    @Size(max = 50)
    @Column(name = "profile_type")
    private String profileType;

    @Column(name = "is_system_admin")
    private Boolean isSystemAdmin = false;

    @Column(name = "requires_mfa")
    private Boolean requiresMfa = false;

    @Column(name = "password_expires_at")
    private Instant passwordExpiresAt;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword = false;

    // État du compte
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Size(max = 255)
    @Column(name = "activation_token")
    @JsonIgnore
    private String activationToken;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @Column(name = "suspension_reason", columnDefinition = "text")
    private String suspensionReason;

    // Reset password
    @Size(max = 20)
    @Column(name = "reset_key")
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private Instant resetDate;

    // Métadonnées
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_metadata", columnDefinition = "jsonb")
    private Map<String, Object> userMetadata = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "app_metadata", columnDefinition = "jsonb")
    private Map<String, Object> appMetadata = new HashMap<>();

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Audit fields
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "last_modified_by")
    private UUID lastModifiedBy;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private Instant accountLockedUntil;

    public User() {}

    public User(String username, String email, String displayName, Tenant tenant) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.tenant = tenant;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == UserStatus.ACTIVE && activatedAt == null) {
            activatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        if (status == UserStatus.ACTIVE && activatedAt == null) {
            activatedAt = Instant.now();
        }
    }

    // Helper methods
    public String getFullName() {
        return displayName != null ? displayName : username;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isAccountLocked() {
        return accountLockedUntil != null && Instant.now().isBefore(accountLockedUntil);
    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN) || Boolean.TRUE.equals(isSystemAdmin);
    }

    public boolean isManager() {
        return roles.contains(UserRole.MANAGER);
    }

    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(UserRole... rolesToCheck) {
        for (UserRole role : rolesToCheck) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }


    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }


    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public void addRole(UserRole role) {
        if (role != null) {
            this.roles.add(role);
        }
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    public void clearRoles() {
        this.roles.clear();
    }

    // Méthode de compatibilité pour obtenir le rôle principal (le plus élevé)
    public UserRole getPrimaryRole() {
        if (roles.contains(UserRole.ADMIN)) return UserRole.ADMIN;
        if (roles.contains(UserRole.MANAGER)) return UserRole.MANAGER;
        if (roles.contains(UserRole.USER)) return UserRole.USER;
        return UserRole.USER; // Défaut
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public Boolean getIsSystemAdmin() {
        return isSystemAdmin;
    }

    public void setIsSystemAdmin(Boolean isSystemAdmin) {
        this.isSystemAdmin = isSystemAdmin;
    }

    public Boolean getRequiresMfa() {
        return requiresMfa;
    }

    public void setRequiresMfa(Boolean requiresMfa) {
        this.requiresMfa = requiresMfa;
    }

    public Instant getPasswordExpiresAt() {
        return passwordExpiresAt;
    }

    public void setPasswordExpiresAt(Instant passwordExpiresAt) {
        this.passwordExpiresAt = passwordExpiresAt;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getSuspensionReason() {
        return suspensionReason;
    }

    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    public Map<String, Object> getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(Map<String, Object> userMetadata) {
        this.userMetadata = userMetadata;
    }

    public Map<String, Object> getAppMetadata() {
        return appMetadata;
    }

    public void setAppMetadata(Map<String, Object> appMetadata) {
        this.appMetadata = appMetadata;
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UUID lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Instant getAccountLockedUntil() {
        return accountLockedUntil;
    }

    public void setAccountLockedUntil(Instant accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", displayName='" + displayName + '\'' +
            ", roles=" + roles +
            ", status=" + status +
            '}';
    }
}
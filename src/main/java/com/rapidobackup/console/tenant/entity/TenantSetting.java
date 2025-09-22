package com.rapidobackup.console.tenant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tenant_settings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "category", "key"})
})
public class TenantSetting {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant tenant;

    // Catégorie de paramètres
    @NotBlank
    @Size(max = 50)
    @Column(name = "category", nullable = false)
    private String category;

    @NotBlank
    @Size(max = 100)
    @Column(name = "key", nullable = false)
    private String key;

    // Valeur polymorphe
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private SettingValueType valueType;

    @Column(name = "string_value", columnDefinition = "text")
    private String stringValue;

    @Column(name = "number_value", precision = 19, scale = 4)
    private BigDecimal numberValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_value", columnDefinition = "jsonb")
    private Map<String, Object> jsonValue = new HashMap<>();

    // Métadonnées
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted = false;

    @Column(name = "is_inherited")
    private Boolean isInherited = false;

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TenantSetting() {}

    public TenantSetting(Tenant tenant, String category, String key, SettingValueType valueType) {
        this.tenant = tenant;
        this.category = category;
        this.key = key;
        this.valueType = valueType;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Helper methods for type-safe value setting
    public void setStringValue(String value) {
        this.valueType = SettingValueType.STRING;
        this.stringValue = value;
        this.numberValue = null;
        this.booleanValue = null;
        this.jsonValue = new HashMap<>();
    }

    public void setNumberValue(BigDecimal value) {
        this.valueType = SettingValueType.NUMBER;
        this.numberValue = value;
        this.stringValue = null;
        this.booleanValue = null;
        this.jsonValue = new HashMap<>();
    }

    public void setNumberValue(Integer value) {
        setNumberValue(value != null ? BigDecimal.valueOf(value) : null);
    }

    public void setNumberValue(Long value) {
        setNumberValue(value != null ? BigDecimal.valueOf(value) : null);
    }

    public void setNumberValue(Double value) {
        setNumberValue(value != null ? BigDecimal.valueOf(value) : null);
    }

    public void setBooleanValue(Boolean value) {
        this.valueType = SettingValueType.BOOLEAN;
        this.booleanValue = value;
        this.stringValue = null;
        this.numberValue = null;
        this.jsonValue = new HashMap<>();
    }

    public void setJsonValue(Map<String, Object> value) {
        this.valueType = SettingValueType.JSON;
        this.jsonValue = value != null ? value : new HashMap<>();
        this.stringValue = null;
        this.numberValue = null;
        this.booleanValue = null;
    }

    // Helper methods for type-safe value getting
    public Object getValue() {
        return switch (valueType) {
            case STRING -> stringValue;
            case NUMBER -> numberValue;
            case BOOLEAN -> booleanValue;
            case JSON -> jsonValue;
        };
    }

    public String getStringValue() {
        return valueType == SettingValueType.STRING ? stringValue : null;
    }

    public BigDecimal getNumberValue() {
        return valueType == SettingValueType.NUMBER ? numberValue : null;
    }

    public Integer getIntegerValue() {
        return numberValue != null ? numberValue.intValue() : null;
    }

    public Long getLongValue() {
        return numberValue != null ? numberValue.longValue() : null;
    }

    public Double getDoubleValue() {
        return numberValue != null ? numberValue.doubleValue() : null;
    }

    public Boolean getBooleanValue() {
        return valueType == SettingValueType.BOOLEAN ? booleanValue : null;
    }

    public Map<String, Object> getJsonValue() {
        return valueType == SettingValueType.JSON ? jsonValue : null;
    }

    public String getFullKey() {
        return category + "." + key;
    }

    public boolean isSecure() {
        return Boolean.TRUE.equals(isEncrypted);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SettingValueType getValueType() {
        return valueType;
    }

    public void setValueType(SettingValueType valueType) {
        this.valueType = valueType;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public Boolean getIsInherited() {
        return isInherited;
    }

    public void setIsInherited(Boolean isInherited) {
        this.isInherited = isInherited;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantSetting)) return false;
        return id != null && id.equals(((TenantSetting) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TenantSetting{" +
            "id=" + id +
            ", category='" + category + '\'' +
            ", key='" + key + '\'' +
            ", valueType=" + valueType +
            ", value=" + getValue() +
            '}';
    }
}
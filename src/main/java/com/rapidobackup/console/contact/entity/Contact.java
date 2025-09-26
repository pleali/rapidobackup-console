package com.rapidobackup.console.contact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidobackup.console.tenant.entity.Tenant;
import com.rapidobackup.console.user.entity.User;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue
    private UUID id;

    // Relation vers tenant (obligatoire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant tenant;

    // Type de contact
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false)
    private ContactType contactType;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    // Informations personnelles
    @Size(max = 20)
    @Column(name = "salutation")
    private String salutation;

    @Size(max = 100)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 100)
    @Column(name = "middle_name")
    private String middleName;

    @Size(max = 100)
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 20)
    @Column(name = "suffix")
    private String suffix;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    // Contact professionnel
    @Size(max = 255)
    @Column(name = "company_name")
    private String companyName;

    @Size(max = 255)
    @Column(name = "job_title")
    private String jobTitle;

    @Size(max = 255)
    @Column(name = "department")
    private String department;

    // Communications
    @Email
    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 50)
    @Column(name = "phone_primary")
    private String phonePrimary;

    @Size(max = 50)
    @Column(name = "phone_secondary")
    private String phoneSecondary;

    @Size(max = 50)
    @Column(name = "phone_mobile")
    private String phoneMobile;

    @Size(max = 50)
    @Column(name = "fax")
    private String fax;

    // Adresse
    @Size(max = 255)
    @Column(name = "address_line1")
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 100)
    @Column(name = "city")
    private String city;

    @Size(max = 100)
    @Column(name = "state_province")
    private String stateProvince;

    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;

    @Size(max = 2)
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be ISO 3166-1 alpha-2")
    @Column(name = "country")
    private String country;

    // Préférences
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_contact_method")
    private ContactMethod preferredContactMethod;

    @Size(max = 10)
    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Size(max = 50)
    @Column(name = "timezone")
    private String timezone;

    // Consentements GDPR
    @Column(name = "marketing_consent")
    private Boolean marketingConsent = false;

    @Column(name = "newsletter_consent")
    private Boolean newsletterConsent = false;

    @Column(name = "consent_date")
    private Instant consentDate;

    // Métadonnées
    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Size(max = 255)
    @Column(name = "tags")
    private String tags; // Comma-separated tags

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields = new HashMap<>();

    // Validation
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Contact() {}

    public Contact(ContactType contactType) {
        this.contactType = contactType;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        updateFullName();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updateFullName();
    }

    private void updateFullName() {
        StringBuilder sb = new StringBuilder();
        if (salutation != null) {
            sb.append(salutation).append(" ");
        }
        if (firstName != null) {
            sb.append(firstName).append(" ");
        }
        if (middleName != null) {
            sb.append(middleName).append(" ");
        }
        if (lastName != null) {
            sb.append(lastName);
        }
        if (suffix != null) {
            sb.append(" ").append(suffix);
        }
        this.fullName = sb.toString().trim();
    }

    // Helper methods
    public String getDisplayName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        if (firstName != null || lastName != null) {
            return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
        }
        return email != null ? email : "Unknown Contact";
    }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null) {
            sb.append(addressLine1);
        }
        if (addressLine2 != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (stateProvince != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(stateProvince);
        }
        if (postalCode != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }
        if (country != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }

    public boolean isVerified() {
        return Boolean.TRUE.equals(emailVerified) || Boolean.TRUE.equals(phoneVerified);
    }

    public boolean belongsToTenant() {
        return tenant != null;
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

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ContactMethod getPreferredContactMethod() {
        return preferredContactMethod;
    }

    public void setPreferredContactMethod(ContactMethod preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public Boolean getNewsletterConsent() {
        return newsletterConsent;
    }

    public void setNewsletterConsent(Boolean newsletterConsent) {
        this.newsletterConsent = newsletterConsent;
    }

    public Instant getConsentDate() {
        return consentDate;
    }

    public void setConsentDate(Instant consentDate) {
        this.consentDate = consentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
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
        if (!(o instanceof Contact)) return false;
        return id != null && id.equals(((Contact) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Contact{" +
            "id=" + id +
            ", contactType=" + contactType +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", isPrimary=" + isPrimary +
            '}';
    }
}
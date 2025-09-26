package com.rapidobackup.console.contact.dto;

import com.rapidobackup.console.contact.entity.ContactMethod;
import com.rapidobackup.console.contact.entity.ContactType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ContactDto {

  private UUID id;
  private ContactType contactType;
  private Boolean isPrimary;

  // Personal information
  private String salutation;
  private String firstName;
  private String middleName;
  private String lastName;
  private String suffix;
  private String fullName;
  private String nickname;
  private String preferredName;

  // Professional information
  private String companyName;
  private String jobTitle;
  private String department;
  private String employeeId;
  private String division;

  // Communication
  private String email;
  private String phonePrimary;
  private String phoneSecondary;
  private String phoneMobile;
  private String fax;

  // Address
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String stateProvince;
  private String postalCode;
  private String country;

  // Preferences
  private ContactMethod preferredContactMethod;
  private String preferredLanguage;
  private String timezone;

  // GDPR consents
  private Boolean marketingConsent;
  private Boolean newsletterConsent;
  private Instant consentDate;

  // Metadata
  private String notes;
  private String tags;
  private Map<String, Object> customFields;

  // Validation
  private Boolean emailVerified;
  private Boolean phoneVerified;
  private Instant verifiedAt;

  // Timestamps
  private Instant createdAt;
  private Instant updatedAt;

  public ContactDto() {}

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

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getDivision() {
    return division;
  }

  public void setDivision(String division) {
    this.division = division;
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
}
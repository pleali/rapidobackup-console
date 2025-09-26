# RapidoBackup Console - Database Model

## Overview

This document describes the database model for RapidoBackup Console, a multi-tenant administration console inspired by best practices from AWS, Azure, Okta, Google Workspace, Salesforce, and Auth0.

## Multi-Tenant Architecture

The system supports a hierarchical multi-tenant architecture with the following characteristics:

- **Unlimited hierarchy**: Support for unlimited tenant levels
- **Data isolation**: Each tenant has its own data
- **Configuration inheritance**: Sub-tenants can inherit parent settings
- **Quota management**: Configurable limits per tenant

## Core Tables

### 1. Tenants (`tenants`)

Central table for hierarchical organization management.

**Key fields:**

- `id` (UUID, PK) - Unique identifier
- `name` (VARCHAR) - Organization name
- `display_name` (VARCHAR) - Display name
- `slug` (VARCHAR, UNIQUE) - URL-safe identifier
- `parent_id` (UUID, FK) - Reference to parent tenant
- `level` (INTEGER) - Hierarchy level (0 = root)
- `path` (VARCHAR) - Complete hierarchical path
- `tenant_type` (ENUM) - Organization type (ADMIN, WHOLESALER, PARTNER, CLIENT)

**Features:**

- **Soft delete** with `deleted_at`
- **Extensible JSON metadata** (`settings`, `custom_attributes`)
- **Quota management** (max_users, max_agents, max_storage_gb)
- **Complete billing configuration**
- **Multi-language and multi-currency support**

**Relationships:**

- Self-reference for parent-child hierarchy
- One-to-many with `users`
- One-to-many with `contacts` (tenant contacts)
- One-to-many with `tenant_settings`

### 2. Users (`users`)

Comprehensive user management with rich profiles.

**Key fields:**

- `id` (UUID, PK) - Unique identifier
- `tenant_id` (UUID, FK) - Owner tenant
- `contact_id` (UUID, FK) - Optional personal contact
- `email` (VARCHAR, NOT NULL) - Email unique per tenant
- `username` (VARCHAR, UNIQUE) - Global unique username
- `display_name` (VARCHAR) - Display name

**Professional profile:**

- `job_title`, `division` - Organizational information
- `employee_id` - Internal employee identifier
- `external_id` - Identifier for SSO/SAML integrations

**Security:**

- `password_hash` - bcrypt password hash
- `status` (ENUM) - Account status (PENDING, ACTIVE, SUSPENDED, LOCKED, DELETED)
- `requires_mfa` - Multi-factor authentication
- `failed_login_attempts` - Failed attempts counter

**Metadata:**

- `user_metadata` (JSONB) - User-modifiable data
- `app_metadata` (JSONB) - Admin-only data

**Relationships:**

- One-to-one with `contacts` (optional personal contact)
- Many-to-one with `tenants` (owner tenant)

### 3. Contacts (`contacts`) - Direct Relationship Architecture

Contact entity with direct relationship to tenants.

**Design principles:**

- **Tenant-owned**: Each contact belongs to a specific tenant
- **Optional user association**: Users can optionally reference a contact
- **Comprehensive data**: Complete contact information and preferences

**Contact types:**
- `PRIMARY` - Primary contact
- `BILLING` - Billing contact
- `TECHNICAL` - Technical support
- `MANAGEMENT` - Management contact

**Key fields:**
- `id` (UUID, PK) - Unique identifier
- `tenant_id` (UUID, FK, NOT NULL) - Owner tenant
- `contact_type` (ENUM, NOT NULL) - Contact type
- `is_primary` (BOOLEAN) - Primary contact flag

**Complete information:**
- Personal data (salutation, first_name, middle_name, last_name, suffix)
- Professional data (company_name, job_title, department)
- Communications (email, phone_primary, phone_secondary, phone_mobile, fax)
- Address (complete postal address with country code)
- Preferences (preferred_contact_method, preferred_language, timezone)
- GDPR consents (marketing_consent, newsletter_consent, consent_date)
- Verification (email_verified, phone_verified, verified_at)
- Extensible metadata (notes, tags, custom_fields as JSONB)

**Relationships:**
- Many-to-one with `tenants` (owner tenant - required)
- One-to-one with `users` (optional user reference)

### 4. Contact Relationships - Direct Architecture

The system uses direct relationships for simplicity:

#### Tenant → Contacts (1-to-Many)
```
Tenant ←→ Contacts (tenant_id FK)
```
- A tenant can have **multiple contacts**
- Each contact belongs to exactly one tenant
- Contact types: PRIMARY, BILLING, TECHNICAL, MANAGEMENT

#### User → Contact (0-to-1)
```
User ←→ Contact (contact_id FK)
```
- A user can optionally reference **one contact**
- Direct foreign key relationship for simplicity
- Contact must belong to user's tenant

**Model advantages:**
- **Simple architecture**: No liaison tables needed
- **Clear ownership**: Contacts belong to tenants
- **Flexible user association**: Optional personal contact reference
- **Data integrity**: Direct foreign key constraints

### 5. Tenant Settings (`tenant_settings`)

Flexible per-tenant configuration with typed values.

**Supported value types:**

- `STRING` - Text values
- `NUMBER` - Numeric values (BigDecimal)
- `BOOLEAN` - Boolean values
- `JSON` - Complex JSON objects

**Organization:**

- `category` - Category (SECURITY, BILLING, FEATURES, BRANDING)
- `key` - Configuration key
- Unique constraint: `(tenant_id, category, key)`

**Features:**

- **Encryption**: Support for encrypted values
- **Inheritance**: Configuration inherited from parent

### 6. Audit Logs (`audit_logs`)

Complete traceability of system actions.

**Tracked events:**

- User connections
- Data modifications
- Administrative actions
- System errors

**Contextual metadata:**

- `ip_address` - IP address (IPv6 compatible)
- `user_agent` - User agent
- `session_id` - Session identifier
- `request_id` - Request identifier

**Change data:**

- `old_values` (JSONB) - Old values
- `new_values` (JSONB) - New values
- `metadata` (JSONB) - Additional metadata

## Materialized Views

### 1. `v_tenant_hierarchy`

Recursive view for hierarchical tenant navigation.

### 2. `v_active_users`

Optimized view of active users with tenant information.

### 3. `v_tenant_contacts`

Enriched view of tenant contacts with complete information.

### 4. `v_user_contacts`

View of users with their associated contact information.

### 5. `v_tenant_stats`

Aggregated statistics per tenant (users, quotas, etc.).

## Enums and Types

### Tenant

- `TenantType` : ADMIN, WHOLESALER, PARTNER, CLIENT
- `TenantStatus` : ACTIVE, SUSPENDED, PENDING_CLOSURE, CLOSED
- `TenantSizeCategory` : SMALL, MEDIUM, LARGE, ENTERPRISE
- `SubscriptionPlan` : STARTER, PROFESSIONAL, ENTERPRISE, CUSTOM

### User

- `UserStatus` : PENDING, ACTIVE, SUSPENDED, LOCKED, DELETED

### Contact

- `ContactType` : PRIMARY, BILLING, TECHNICAL, MANAGEMENT
- `ContactMethod` : EMAIL, PHONE, SMS

### Settings

- `SettingValueType` : STRING, NUMBER, BOOLEAN, JSON

### Audit

- `AuditSeverity` : INFO, WARNING, ERROR, CRITICAL
- `AuditResult` : SUCCESS, FAILURE, PARTIAL

## Performance Indexes

Key indexes for optimal performance:

- **Hierarchical queries**: `idx_tenants_path` (GIST) for LTREE path queries
- **JSON metadata**: `idx_users_metadata` (GIN) for JSON search
- **Contact relationships**: Indexes on tenant_id and contact_id foreign keys for efficient queries

## Advanced Features

### 1. Soft Delete

All main entities support logical deletion:

- `@SQLDelete` annotation for JPA entities
- `@Where` clause for automatic filtering
- `deleted_at` field for traceability

### 2. Automatic Auditing

Builder pattern for easy log creation with complete context tracking.

### 3. JSON Metadata

Native PostgreSQL JSONB support:

- GIN indexes for high-performance search
- Application-side validation
- Extensibility without migration

### 4. Multi-Level Validation

- Database constraints
- JPA validation with Bean Validation
- Business rules in services

## Security

### 1. Data Isolation

- Automatic tenant-based filtering
- Foreign keys with integrity constraints
- Service-level verifications

### 2. Encryption

- Password hashing with bcrypt
- Encryption support for sensitive settings
- Secure tokens for activation/reset

### 3. Complete Auditing

- Traceability of all actions
- Security context (IP, session)
- Configurable retention

## Migration and Evolution

### 1. Liquibase

Complete structure with:

- Versioned schema migrations
- Reference data
- Indexes and constraints
- Views and functions

### 2. Extensibility

- JSON metadata for new fields
- Flexible configuration pattern
- Multi-database support (PostgreSQL/H2)

## Relationship Diagram

```
┌─────────────┐    1-to-many    ┌─────────────┐
│   Tenants   │◄────────────────│  Contacts   │
│             │                 │             │
│ - hierarchy │                 │ - tenant_id │
│ - settings  │                 │ - complete  │
│ - quotas    │                 │   data      │
└─────┬───────┘                 └──────┬──────┘
      │                                │
      │ 1-to-many                      │ 0-to-1
      ▼                                ▼
┌─────────────┐    contact_id    ┌─────────────┐
│    Users    │─────────────────►│   Contact   │
│             │ (optional FK)    │ (optional)  │
│ - profiles  │                 │ - personal  │
│ - security  │                 │   contact   │
│ - metadata  │                 │             │
└─────────────┘                 └─────────────┘
```

## Typical Use Cases

### Tenant Contact Management

**Example: Acme Corp Company**
- **Primary Contact**: CEO for strategic decisions
- **Billing Contact**: Accountant for invoicing
- **Technical Contact**: CTO for technical support
- **Management Contact**: HR for user management

### Personal User Contact

**Example: Employee John Doe**
- **Single contact**: His personal coordinates
- Can differ from tenant's professional contact

### Model Flexibility

- **Tenant ownership**: Each contact belongs to exactly one tenant
- **Multiple contacts per tenant**: A tenant can have multiple contacts of different types
- **Optional user contact**: Users can optionally reference a personal contact
- **Easy evolution**: Simple architecture allows for future extensions

This architecture provides a solid, extensible, and high-performance foundation for RapidoBackup Console, inspired by industry best practices.

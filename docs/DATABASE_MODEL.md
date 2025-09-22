# RapidoBackup Console - Modèle de Base de Données

## Vue d'ensemble

Ce document décrit le modèle de base de données pour RapidoBackup Console, une console d'administration multi-tenant inspirée des meilleures pratiques d'AWS, Azure, Okta, Google Workspace, Salesforce et Auth0.

## Architecture Multi-Tenant

Le système supporte une architecture multi-tenant hiérarchique avec les caractéristiques suivantes :

- **Hiérarchie illimitée** : Support de niveaux de tenants illimités
- **Isolation des données** : Chaque tenant a ses propres données
- **Héritage de configuration** : Les sous-tenants peuvent hériter des paramètres parents
- **Gestion des quotas** : Limites configurables par tenant

## Tables Principales

### 1. Tenants (`tenants`)

Table centrale pour la gestion des organisations hiérarchiques.

**Champs principaux :**
- `id` (UUID, PK) - Identifiant unique
- `name` (VARCHAR) - Nom de l'organisation
- `display_name` (VARCHAR) - Nom d'affichage
- `slug` (VARCHAR, UNIQUE) - Identifiant URL-safe
- `parent_tenant_id` (UUID, FK) - Référence au tenant parent
- `level` (INTEGER) - Niveau dans la hiérarchie (0 = root)
- `path` (LTREE/VARCHAR) - Chemin hiérarchique complet
- `tenant_type` (ENUM) - Type d'organisation (ADMIN, GROSSISTE, PARTENAIRE, CLIENT)

**Fonctionnalités :**
- **Soft delete** avec `deleted_at`
- **Métadonnées JSON** extensibles (`settings`, `custom_attributes`)
- **Gestion des quotas** (max_users, max_agents, max_storage_gb)
- **Configuration de facturation** complète
- **Support multi-langue** et multi-devise

**Relations :**
- Auto-référence pour la hiérarchie parent-enfant
- Un-vers-plusieurs avec `users`
- Un-vers-plusieurs avec `contacts`
- Un-vers-plusieurs avec `tenant_settings`

### 2. Utilisateurs (`users`)

Gestion complète des utilisateurs avec profils riches.

**Champs principaux :**
- `id` (UUID, PK) - Identifiant unique
- `tenant_id` (UUID, FK) - Tenant propriétaire
- `email` (VARCHAR, NOT NULL) - Email unique par tenant
- `username` (VARCHAR, UNIQUE) - Nom d'utilisateur global unique
- `display_name` (VARCHAR) - Nom d'affichage

**Profil professionnel :**
- `job_title`, `department`, `division` - Informations organisationnelles
- `employee_id` - Identifiant employé interne
- `external_id` - Identifiant pour intégrations SSO/SAML

**Sécurité :**
- `password_hash` - Hash bcrypt du mot de passe
- `role` (ENUM) - Rôle utilisateur (ADMIN, MANAGER, USER)
- `status` (ENUM) - État du compte (PENDING, ACTIVE, SUSPENDED, LOCKED, DELETED)
- `requires_mfa` - Authentification multi-facteurs
- `failed_login_attempts` - Compteur de tentatives échouées

**Métadonnées :**
- `user_metadata` (JSONB) - Données modifiables par l'utilisateur
- `app_metadata` (JSONB) - Données admin uniquement

### 3. Contacts (`contacts`)

Système de contacts flexible et réutilisable.

**Caractéristiques :**
- **Polyvalent** : Peut être lié à un utilisateur OU à un tenant
- **Types multiples** : PRIMARY, BILLING, TECHNICAL, EMERGENCY
- **Adresse complète** : Support international complet
- **Consentements GDPR** : Tracking des consentements marketing
- **Vérification** : Email et téléphone vérifiables

**Contrainte d'intégrité :**
```sql
CHECK (
    (user_id IS NOT NULL AND tenant_id IS NULL) OR
    (user_id IS NULL AND tenant_id IS NOT NULL)
)
```

### 4. Paramètres Tenant (`tenant_settings`)

Configuration flexible par tenant avec valeurs typées.

**Types de valeurs supportés :**
- `STRING` - Valeurs textuelles
- `NUMBER` - Valeurs numériques (BigDecimal)
- `BOOLEAN` - Valeurs booléennes
- `JSON` - Objets JSON complexes

**Organisation :**
- `category` - Catégorie (SECURITY, BILLING, FEATURES, BRANDING)
- `key` - Clé de configuration
- Contrainte unique : `(tenant_id, category, key)`

**Fonctionnalités :**
- **Chiffrement** : Support des valeurs chiffrées
- **Héritage** : Configuration héritée du parent

### 5. Logs d'Audit (`audit_logs`)

Traçabilité complète des actions système.

**Événements trackés :**
- Connexions utilisateur
- Modifications de données
- Actions administratives
- Erreurs système

**Métadonnées contextuelles :**
- `ip_address` - Adresse IP (IPv6 compatible)
- `user_agent` - Agent utilisateur
- `session_id` - Identifiant de session
- `request_id` - Identifiant de requête

**Données de changement :**
- `old_values` (JSONB) - Anciennes valeurs
- `new_values` (JSONB) - Nouvelles valeurs
- `metadata` (JSONB) - Métadonnées additionnelles

## Vues Matérialisées

### 1. `v_tenant_hierarchy`

Vue récursive pour navigation hiérarchique des tenants.

```sql
WITH RECURSIVE tenant_tree AS (
    SELECT *, 0 as depth, ARRAY[id] as path_array
    FROM tenants WHERE parent_tenant_id IS NULL
    UNION ALL
    SELECT t.*, tt.depth + 1, tt.path_array || t.id
    FROM tenants t
    JOIN tenant_tree tt ON t.parent_tenant_id = tt.id
)
SELECT * FROM tenant_tree;
```

### 2. `v_active_users`

Vue optimisée des utilisateurs actifs avec informations tenant.

### 3. `v_contacts_with_details`

Vue enrichie des contacts avec détails utilisateur/tenant.

### 4. `v_tenant_stats`

Statistiques agrégées par tenant (utilisateurs, quotas, etc.).

## Enums et Types

### Tenant
- `TenantType` : ADMIN, GROSSISTE, PARTENAIRE, CLIENT
- `TenantStatus` : ACTIVE, SUSPENDED, PENDING_CLOSURE, CLOSED
- `TenantSizeCategory` : SMALL, MEDIUM, LARGE, ENTERPRISE
- `SubscriptionPlan` : STARTER, PROFESSIONAL, ENTERPRISE, CUSTOM

### User
- `UserRole` : ADMIN, MANAGER, USER
- `UserStatus` : PENDING, ACTIVE, SUSPENDED, LOCKED, DELETED

### Contact
- `ContactType` : PRIMARY, BILLING, TECHNICAL, EMERGENCY
- `ContactMethod` : EMAIL, PHONE, SMS

### Settings
- `SettingValueType` : STRING, NUMBER, BOOLEAN, JSON

### Audit
- `AuditSeverity` : INFO, WARNING, ERROR, CRITICAL
- `AuditResult` : SUCCESS, FAILURE, PARTIAL

## Index de Performance

### Tenants
- `idx_tenants_parent` - Requêtes hiérarchiques
- `idx_tenants_slug` - Recherche par slug
- `idx_tenants_path` (GIST) - Requêtes sur le chemin LTREE

### Users
- `idx_users_tenant` - Filtrage par tenant
- `idx_users_email` - Recherche par email
- `idx_users_username` - Recherche par username
- `idx_users_metadata` (GIN) - Recherche dans les métadonnées JSON

### Audit Logs
- `idx_audit_tenant_date` - Logs par tenant et date
- `idx_audit_user_date` - Logs par utilisateur et date
- `idx_audit_event` - Logs par type d'événement

## Fonctionnalités Avancées

### 1. Soft Delete
Toutes les entités principales supportent la suppression logique :
- Annotation `@SQLDelete` pour les entités JPA
- Clause `@Where` pour filtrer automatiquement
- Champ `deleted_at` pour traçabilité

### 2. Audit Automatique
Pattern Builder pour création facile de logs :

```java
AuditLog.builder()
    .tenant(tenant)
    .user(user)
    .eventType("USER_CREATED")
    .action("CREATE")
    .target("USER", userId, username)
    .newValues(userData)
    .build();
```

### 3. Métadonnées JSON
Support natif PostgreSQL JSONB :
- Index GIN pour recherche performante
- Validation côté application
- Extensibilité sans migration

### 4. Validation Multi-Niveau
- Contraintes base de données
- Validation JPA avec Bean Validation
- Règles métier dans les services

## Sécurité

### 1. Isolation des Données
- Filtrage automatique par tenant
- Foreign keys avec contraintes d'intégrité
- Vérifications au niveau service

### 2. Chiffrement
- Mots de passe avec bcrypt
- Support chiffrement pour paramètres sensibles
- Tokens sécurisés pour activation/reset

### 3. Audit Complet
- Traçabilité de toutes les actions
- Contexte de sécurité (IP, session)
- Rétention configurable

## Migration et Évolution

### 1. Liquibase
Structure complète avec :
- Migrations de schéma versionnées
- Données de référence
- Index et contraintes
- Vues et fonctions

### 2. Extensibilité
- Métadonnées JSON pour nouveaux champs
- Pattern de configuration flexible
- Support multi-base de données (PostgreSQL/H2)

## Exemples d'Utilisation

### Création d'un Tenant
```java
Tenant tenant = new Tenant("Acme Corp", "acme-corp", TenantType.CLIENT);
tenant.setDisplayName("Acme Corporation");
tenant.setParent(parentTenant);
tenant.setMaxUsers(100);
tenant.setSubscriptionPlan(SubscriptionPlan.PROFESSIONAL);
```

### Création d'un Utilisateur
```java
User user = new User("john.doe", "john@acme.com", "John Doe", tenant);
user.setFirstName("John");
user.setLastName("Doe");
user.setRole(UserRole.MANAGER);
user.setJobTitle("IT Manager");
```

### Configuration de Paramètres
```java
TenantSetting setting = new TenantSetting(tenant, "SECURITY", "session_timeout");
setting.setNumberValue(480); // 8 heures
setting.setDescription("Session timeout in minutes");
```

Cette architecture fournit une base solide, extensible et performante pour RapidoBackup Console, inspirée des meilleures pratiques de l'industrie.
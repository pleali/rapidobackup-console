# RapidoBackup Console - Profile Configuration Documentation

## Overview

The RapidoBackup Console uses both Maven profiles and Spring profiles to manage different deployment configurations. This document outlines all available profiles and their purposes.

## Maven Profiles

### Core Profiles

#### `dev` (Default Active)
- **Purpose**: Development environment configuration
- **Database**: PostgreSQL (localhost:5432)
- **Features**:
  - Spring Boot DevTools enabled
  - Default active Spring profiles: `dev` with optional TLS and no-liquibase modifiers
  - Liquibase plugin configuration for local database

#### `prod`
- **Purpose**: Production environment build
- **Features**:
  - Frontend build with optimized production assets (`webapp:prod`)
  - Frontend tests execution during Maven test phase
  - Git commit information inclusion
  - Spring profiles: `prod` with optional api-docs, TLS, e2e, and no-liquibase modifiers
  - Clean target/classes/static/ directory before build

#### `webapp` (Default Active)
- **Purpose**: Development with frontend build
- **Features**:
  - Frontend build for development (`webapp:build`)
  - Node.js and npm installation
  - Frontend Maven plugin integration
  - Spring profiles: `dev` with optional no-liquibase modifier

### Utility Profiles

#### `api-docs`
- **Purpose**: Enable OpenAPI documentation generation
- **Effect**: Adds `,api-docs` to active Spring profiles
- **Features**: SpringDoc OpenAPI UI and documentation endpoints

#### `docker-compose` (Default Active)
- **Purpose**: Docker Compose integration
- **Dependencies**: Spring Boot Docker Compose support
- **Configuration**: Uses `src/main/docker/services.yml`

#### `eclipse`
- **Purpose**: Eclipse IDE compatibility
- **Activation**: Automatically activated when `m2e.version` property is present
- **Features**: Undertow server dependency and M2E lifecycle mapping

#### `no-liquibase`
- **Purpose**: Skip database migrations
- **Effect**: Adds `,no-liquibase` to active Spring profiles
- **Use Case**: Quick startup without database schema changes

#### `tls`
- **Purpose**: Enable TLS/SSL configuration
- **Effect**: Adds `,tls` to active Spring profiles

#### `war`
- **Purpose**: Build WAR file instead of JAR
- **Plugin**: Maven WAR plugin configuration

#### `zipkin`
- **Purpose**: Distributed tracing with Zipkin
- **Dependencies**: Micrometer tracing with Brave and Zipkin reporter

#### `test-only`
- **Purpose**: Run tests without frontend build
- **Features**: Skips all frontend-maven-plugin executions
- **Use Case**: CI/CD pipelines focusing on backend testing

## Spring Profiles

### Primary Profiles

#### `dev` (Default)
- **Session Management**: In-memory sessions (no Redis dependency)
- **Cache**: Simple in-memory cache
- **Redis**: Auto-configuration disabled
- **DevTools**: Restart enabled, LiveReload disabled
- **Logging**: DEBUG level for application packages
- **CORS**: Permissive (`*` allowed origins)
- **Database**: PostgreSQL with JPA and R2DBC
- **Session Timeout**: 30 minutes

#### `dev-redis`
- **Purpose**: Development with Redis support
- **Usage**: `.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,dev-redis"`
- **Session Management**: Redis-backed sessions with namespace `rapidobackup:dev-sessions`
- **Cache**: Redis cache with `console:dev:` key prefix
- **Redis Pool**: Smaller connection pool (4 connections) optimized for development
- **Cache TTL**: 30 minutes
- **Logging**: Additional Redis and session debugging

#### `prod`
- **Session Management**: Redis-backed sessions with namespace `rapidobackup:sessions`
- **Cache**: Redis cache (inherited from base configuration)
- **Security**:
  - Secure cookies enabled
  - SameSite: strict
  - HTTP-only cookies
- **Logging**:
  - INFO level for application
  - File logging to `logs/console.log`
  - Log rotation (100MB max, 30 files history)
- **CORS**: Restricted to `https://secure.rapidobackup.com`
- **Database**: Production Liquibase context

#### `api-docs`
- **Purpose**: Enable OpenAPI documentation
- **SpringDoc**:
  - Swagger UI enabled at `/swagger-ui.html`
  - API docs at `/v3/api-docs`
  - API grouping by functionality (auth, agents, users)
  - Try-it-out functionality enabled

### Profile Groups

#### `dev` Group (Default)
- Includes: `dev`, `api-docs`
- Optional: `tls` (commented out by default)

## Configuration Hierarchy

The configuration follows this precedence order:
1. Command line arguments (`--spring.profiles.active`)
2. JVM system properties (`-Dspring.profiles.active`)
3. Maven profile property substitution (`@spring.profiles.active@`)
4. Default profile group configuration

## Database Configuration

### JPA (Traditional modules: auth, users, backup)
- **Driver**: PostgreSQL JDBC
- **Connection Pool**: HikariCP with optimized settings
- **Hibernate**: Second-level cache enabled with JCache
- **Schema Management**: Liquibase migrations

### R2DBC (Reactive modules: agents)
- **Driver**: R2DBC PostgreSQL
- **Connection Pool**: R2DBC Pool (5-20 connections)
- **Use Case**: High-performance agent communication

## Session Management

### Development (`dev`)
- **Type**: In-memory sessions
- **Advantages**: No external dependencies, fast startup
- **Cookie**: `RBSESSIONID` with relaxed security settings

### Development with Redis (`dev,dev-redis`)
- **Type**: Redis sessions with development namespace
- **Purpose**: Testing production-like session behavior
- **Configuration**: Smaller connection pool, development-specific key prefixes

### Production (`prod`)
- **Type**: Redis sessions with production namespace
- **Security**: Enhanced cookie security (secure, strict SameSite)
- **Persistence**: Session data persists across application restarts

## Usage Examples

### Development Commands

```powershell
# Development (default)
.\mvnw.cmd spring-boot:run

# Development with Redis
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,dev-redis"

# Production
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=prod"

# Development with API documentation
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,api-docs"

# Skip database migrations
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,no-liquibase"

# Development with TLS enabled
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,tls"
```

### Build Commands

```powershell
# Development build (with frontend)
.\mvnw.cmd clean package

# Production build
.\mvnw.cmd clean package -Pprod

# Test-only build (no frontend)
.\mvnw.cmd clean package -Ptest-only

# Build with API documentation
.\mvnw.cmd clean package -Papi-docs
```

### Database Commands

```powershell
# Run Liquibase migrations
.\mvnw.cmd liquibase:update

# Generate Liquibase diff
.\mvnw.cmd liquibase:diff

# Rollback Liquibase changes
.\mvnw.cmd liquibase:rollback "-Dliquibase.rollbackCount=1"
```

### Testing Commands

```powershell
# Run all tests
.\mvnw.cmd test

# Run tests with specific profile
.\mvnw.cmd test "-Dspring.profiles.active=test"

# Run tests without frontend build
.\mvnw.cmd test -Ptest-only
```

## Environment Variables

### Database Configuration
- `DB_PASSWORD`: PostgreSQL password (defaults to empty)
- `REDIS_PASSWORD`: Redis password (defaults to empty)
- `ADMIN_PASSWORD`: Default admin password (defaults to "admin123")

### Application Configuration
- `JAVA_OPTS`: Additional JVM options
- `SPRING_PROFILES_ACTIVE`: Override active Spring profiles

## Docker Configuration

### Development Docker Compose Files
- `src/main/docker/services.yml`: Full infrastructure (PostgreSQL + Redis)
- `src/main/docker/postgresql.yml`: PostgreSQL only
- `src/main/docker/redis.yml`: Redis only

### Redis Management
- **RedisInsight Web UI**: `http://localhost:8001`
- **redis-cli**: `docker exec -it <redis-container> redis-cli`
- **Sessions**: Keys with pattern `rapidobackup:sessions:*`
- **Cache**: Keys with pattern `console:*`

## Profile Selection Guide

| Use Case | Maven Profile | Spring Profile | Command |
|----------|---------------|----------------|---------|
| Local development | `dev` (default) | `dev` | `.\mvnw.cmd spring-boot:run` |
| Local dev with Redis | `dev` | `dev,dev-redis` | `.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,dev-redis"` |
| API documentation | `api-docs` | `dev,api-docs` | `.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,api-docs"` |
| Production build | `prod` | `prod` | `.\mvnw.cmd clean package -Pprod` |
| Quick testing | `test-only` | `test` | `.\mvnw.cmd test -Ptest-only` |
| No DB migration | `no-liquibase` | `dev,no-liquibase` | `.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=dev,no-liquibase"` |

## Common Issues and Solutions

### PowerShell Command Syntax
- **Quote parameters containing dots**: Use `"-Dspring.profiles.active=dev,redis"` not `-Dspring.profiles.active=dev,redis`
- **Maven goals without dots are OK**: Use `spring-boot:run` without quotes
- **Profile flags are OK**: Use `-Pprod` without quotes

### Redis Connection Issues
- Ensure Redis is running: `docker-compose -f src/main/docker/redis.yml up -d`
- Check RedisInsight at `http://localhost:8001`
- Use `dev` profile if Redis is not needed

### Database Migration Issues
- Use `no-liquibase` profile to skip migrations: `"-Dspring.profiles.active=dev,no-liquibase"`
- Check PostgreSQL connection: `docker-compose -f src/main/docker/postgresql.yml up -d`

### Frontend Build Issues
- Use `test-only` profile to skip frontend: `.\mvnw.cmd test -Ptest-only`
- Clear Node.js cache: `rm -rf node_modules package-lock.json`
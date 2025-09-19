# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Cloud administration console for RapidoBackup - online/local backup services, remote desktop, and Office 365 backup. The system manages agents installed on target workstations/servers with bidirectional secure communication.

**Company:** RapidoBackup  
**Product:** RapidoBackup Console

## Development 

**IMPORTANT - Shell Configuration when the development environment is Windows:**
- Operating System: Windows
- Required Shell: PowerShell ONLY
- Do NOT use bash, sh, or Unix commands (curl, grep, find, etc.)
- All commands must be PowerShell compatible
- Use PowerShell equivalents: `Invoke-RestMethod` instead of `curl`, `Select-String` instead of `grep`
- File operations: `Get-Content`, `Set-Content`, `Copy-Item`, `Remove-Item`

## Architecture - Monolithic Modular (Evolutive)

**Current Architecture:**
```
Frontend (React + ShadCN UI)
    ↓
Spring Boot Monolithic Backend
├── Auth Module (Integrated Spring Security + JWT)
├── User Module (Hierarchical management)
├── Agent Module (R2DBC Reactive + WebSocket + Long Polling)
├── Backup Module (Integration with existing Delphi/Java)
└── Monitor Module (Metrics and alerts)
    ↓
PostgreSQL + Redis
```

**Evolution Path:**
- Phase 1: Monolithic modular (current)
- Phase 2: Extract Agent Service
- Phase 3: Full microservices with API Gateway

## Technology Stack

**Backend:**
- Spring Boot 3.x with Spring Security
- Spring WebFlux (reactive) + R2DBC for agent module
- JPA/Hibernate for other modules + Liquibase (DB migrations)
- JWT authentication (integrated, no external OAuth)
- WebSocket + Long Polling for agent communication
- MapStruct for DTOs

**Frontend:**
- React 18 + TypeScript
- ShadCN UI + Tailwind CSS V4
- React Query for state management
- axios for API requests
- i18next for internationalization
- Recharts for dashboards

**Development Environment (Windows + VS Code):**
- Java 17+ (OpenJDK)
- Node.js 18+
- Docker Desktop for Windows
- PostgreSQL (via Docker)
- Redis (via Docker)

## Agent Communication

**Hybrid Protocol:**
- **Modern agents**: WebSocket (bidirectional real-time)
- **Delphi agents**: Long Polling (HTTP/HTTPS only)
- **Fallback**: Automatic WebSocket → Long Polling
- **Security**: mTLS + API keys with rotation

**Agent Module Architecture:**
- Uses R2DBC for reactive database access
- Separate from JPA configuration used by other modules
- Spring Boot auto-configuration handles JPA via `application.yml`


**Initial Setup:**
```powershell
# Clone and setup
git clone <repo>
cd rb-console

# Install dependencies
.\mvnw.cmd install
cd frontend && npm install

# Start infrastructure
docker-compose up -d postgres redis

# Database migration
.\mvnw.cmd liquibase:update
```

**Development:**
```powershell
# Start backend (from root)
.\mvnw.cmd spring-boot:run

# Start frontend (from frontend folder)
npm start

# Run tests
.\mvnw.cmd test

# Build
.\mvnw.cmd clean package

# Format code
.\mvnw.cmd spotless:apply

# Java compilation only (skip frontend)
.\mvnw.cmd compiler:compile
```

**Docker Development:**
```powershell
# Full stack
docker-compose -f docker-compose.dev.yml up

# Only infrastructure
docker-compose up postgres redis
```

## Project Structure

```
rb-console/
├── src/main/java/com/rapidobackup/console/
│   ├── agent/                   # Agent management module (R2DBC reactive)
│   ├── auth/                    # Authentication & JWT (JPA)
│   ├── user/                    # User management (JPA)
│   ├── common/                  # Shared utilities & DTOs
│   ├── config/                  # Application configuration
│   └── ConsoleApplication.java
│
├── src/main/webapp/             # Frontend integrated in Maven build
│   ├── src/
│   │   ├── components/          # React components (ShadCN UI)
│   │   ├── features/            # Feature modules (agents, auth, dashboard, etc.)
│   │   ├── hooks/               # React hooks
│   │   └── locales/             # i18n (en, fr, es)
│   └── package.json
│
├── src/main/resources/
│   └── config/liquibase/        # Database migrations
│       └── changelog/
│           ├── master.xml       # Main changelog file
│           ├── schema/          # Table definitions and structure
│           └── data/            # Data inserts and reference data
│
├── docker-compose*.yml          # Development & production Docker
├── pom.xml                      # Maven configuration
└── package.json                 # Frontend build integration
```

## Database Configuration

**Configuration Strategy:**
- **JPA/Hibernate**: Auto-configured via `application.yml` for user, auth, backup modules
- **R2DBC**: Explicitly configured via `R2dbcConfig.java` for agent module (reactive)
- **No manual EntityManagerFactory**: Spring Boot auto-configuration handles JPA setup
- **Separate transaction managers**: JPA and R2DBC have independent transaction handling

**Key Configuration Files:**
- `src/main/resources/application.yml`: Main configuration with JPA and R2DBC properties
- `src/main/java/.../agent/config/R2dbcConfig.java`: Reactive database configuration

**Liquibase Organization:**
- **Schema separation**: Table definitions in `schema/`, data inserts in `data/`
- **Execution order**: Schema first (structure), then data (inserts)
- **Default admin user**: `admin@console.local` / `admin` (password change required on first login)

## Core Requirements

**Agent Management:**
- Hybrid communication: WebSocket (modern) + Long Polling (Delphi)
- API key authentication with revocation
- Hierarchical agent management
- Extensible for remote desktop and telemetry
- Reactive database access for performance

**Backup Integration:**
- Integration with existing Delphi backup software
- Existing Java backup server (Hibernate/PostgreSQL)
- Cloud service and on-premise appliances support
- No inbound firewall ports required

**User Management:**
- Hierarchical: Admin → Grossiste → Partenaire → Client
- Fine-grained RBAC permissions
- Users access only assigned agents
- Complete audit trail

**Internationalization:**
- English (default), French, Spanish
- React i18next + Spring MessageSource

## Security Model

**Integrated Authentication:**
- Spring Security with JWT (no external OAuth)
- User login: email/password → JWT tokens
- Agent authentication: API keys with expiration
- Refresh token mechanism
- Role-based access control (RBAC)

**Agent Security:**
- mTLS for sensitive operations
- API key rotation
- Connection revocation
- Command encryption

## VS Code Extensions Recommended

```json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vmware.vscode-spring-boot",
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode",
    "ms-vscode.vscode-typescript-next",
    "formulahendry.auto-rename-tag",
    "christian-kohler.path-intellisense"
  ]
}
```

## Development Standards

- All code and comments in English
- All documentation in English
- Java: Google Style Guide with Spotless
- React: Prettier + ESLint configuration
- Database: Snake_case naming
- API: RESTful with consistent error handling
- Git: Conventional commits

## Testing Strategy

```powershell
# Backend tests
.\mvnw.cmd test                    # Unit tests
.\mvnw.cmd integration-test        # Integration tests

# Frontend tests
npm test                         # Jest tests
npm run test:e2e                 # Playwright E2E
```

## Migration to Microservices (Future)

**Preparation in monolith:**
- Internal event bus (Spring Events)
- Clear module boundaries
- Separate database schemas per module
- Configuration externalization
- Health checks and metrics

**Phase 2: Extract Agent Service**
- Move agent module to separate Spring Boot app
- Message queue (RabbitMQ) for inter-service communication
- Shared database initially, then separate

**Phase 3: API Gateway**
- Spring Cloud Gateway
- Service discovery (Consul)
- Load balancing and circuit breakers

## Important Instructions

- **Command Line**: Use PowerShell for Windows development commands
- **Configuration**: Avoid manual JPA configuration - use Spring Boot auto-configuration via `application.yml`
- **Database Access**: Agent module uses R2DBC (reactive), other modules use JPA
- **Language**: All rules and documentation must be written in English
- **Company**: RapidoBackup is both the company name and the backup software name
- **frontend**: path is @src/main/webapp/
- **frontend**: use mcp server Shadcn for ui
- **important** : tu ne dois pas corriger le code généré par openapitools
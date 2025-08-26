# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Cloud administration console for online/local backup services, remote desktop, and Office 365 backup. The system manages agents installed on target workstations/servers with bidirectional secure communication.

## Architecture - Monolithic Modular (Evolutive)

**Current Architecture:**
```
Frontend (React + ShadCN UI)
    ↓
Spring Boot Monolithic Backend
├── Auth Module (Integrated Spring Security + JWT)
├── User Module (Hierarchical management)
├── Agent Module (WebSocket + Long Polling)
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
- Spring WebFlux (reactive)
- Hibernate + Liquibase (DB migrations)
- JWT authentication (integrated, no external OAuth)
- WebSocket + Long Polling for agent communication
- MapStruct for DTOs

**Frontend:**
- React 18 + TypeScript
- ShadCN UI + Tailwind CSS
- React Query for state management
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

## Development Commands (Windows)

**Initial Setup:**
```cmd
# Clone and setup
git clone <repo>
cd testjdl

# Install dependencies
mvnw.cmd install
cd frontend && npm install

# Start infrastructure
docker-compose up -d postgres redis

# Database migration
mvnw.cmd flyway:migrate
```

**Development:**
```cmd
# Start backend (from root)
mvnw.cmd spring-boot:run

# Start frontend (from frontend folder)
npm start

# Run tests
mvnw.cmd test

# Build
mvnw.cmd clean package

# Format code
mvnw.cmd spotless:apply
```

**Docker Development:**
```cmd
# Full stack
docker-compose -f docker-compose.dev.yml up

# Only infrastructure
docker-compose up postgres redis
```

## Project Structure

```
testjdl/
├── src/main/java/com/company/console/
│   ├── auth/                    # Authentication module
│   │   ├── config/SecurityConfig.java
│   │   ├── service/AuthService.java
│   │   ├── controller/AuthController.java
│   │   └── jwt/JwtTokenProvider.java
│   │
│   ├── user/                    # User management module
│   │   ├── entity/User.java
│   │   ├── service/UserService.java
│   │   ├── controller/UserController.java
│   │   └── repository/UserRepository.java
│   │
│   ├── agent/                   # Agent management module
│   │   ├── websocket/AgentWebSocketHandler.java
│   │   ├── polling/LongPollingController.java
│   │   ├── service/AgentCommandQueue.java
│   │   ├── entity/Agent.java
│   │   └── manager/UnifiedAgentManager.java
│   │
│   ├── backup/                  # Backup operations module
│   │   ├── entity/BackupJob.java
│   │   ├── service/BackupService.java
│   │   ├── controller/BackupController.java
│   │   └── integration/DelphiService.java
│   │
│   ├── common/                  # Shared components
│   │   ├── event/InternalEventBus.java
│   │   ├── security/
│   │   ├── exception/
│   │   ├── dto/
│   │   └── config/
│   │
│   └── ConsoleApplication.java
│
├── frontend/
│   ├── src/
│   │   ├── components/          # Reusable UI components
│   │   ├── pages/               # Route components
│   │   ├── hooks/               # React hooks
│   │   ├── services/            # API calls
│   │   ├── store/               # State management
│   │   └── i18n/                # Internationalization
│   └── package.json
│
├── docker-compose.yml           # Production
├── docker-compose.dev.yml       # Development
├── pom.xml
└── README.md
```

## Core Requirements

**Agent Management:**
- Hybrid communication: WebSocket (modern) + Long Polling (Delphi)
- API key authentication with revocation
- Hierarchical agent management
- Extensible for remote desktop and telemetry

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

```cmd
# Backend tests
mvnw.cmd test                    # Unit tests
mvnw.cmd integration-test        # Integration tests

# Frontend tests
cd frontend
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
- mon entreprise s'appelle rapidobackup et le logiciel de sauvegarde s'appelle aussi rapidobackup
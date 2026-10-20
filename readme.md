# SupportDesk-SpringBoot

REST API backend for a support desk: customers' issues are logged as tickets, assigned to agents, moved through a controlled status workflow, discussed via comments, and audited through a ticket history.

Built as a technical assessment (Neurogine) with Java and Spring Boot.

## Requirement Assessment
- https://github.com/aimandesu/Neurogine-Assessment/blob/main/SupportDesk_FullStack_MidLevel_Assignment.pdf

## Postman Collection
- https://github.com/aimandesu/Neurogine-Assessment/blob/main/SupportDesk.postman_collection.json

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database](#database)
- [Authentication & Authorization](#authentication--authorization)
- [API Reference](#api-reference)
- [Business Rules](#business-rules)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Known Limitations / Roadmap](#known-limitations--roadmap)
- [References](#references)

## Features
- Register / login with JWT (stateless) authentication
- Role-based access control (`Admin`, `Agent`)
- Ticket CRUD with priority, SLA due date and auto-generated reference (`TCK-2026-0001`)
- Agent management: register as agent, update profile, activate / deactivate, filter list
- Assign / unassign tickets to agents
- Ticket status workflow with validation (`New -> InProgress -> Resolved -> Closed`)
- Comments on tickets (Closed tickets are read-only)
- Ticket history (audit trail of status and priority changes)
- Request validation and centralized exception handling

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 17 |
| Framework | Spring Boot 4.1.1 (Spring MVC) |
| Build tool | Maven (wrapper included) |
| Persistence | Spring Data JPA (Hibernate), Specifications for dynamic filtering |
| Database | H2 (file-based, with web console) |
| Security | Spring Security + JWT (jjwt 0.12.5), BCrypt password hashing |
| Mapping | MapStruct 1.6.3 |
| Boilerplate | Lombok |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Testing | Spring Boot Test, Spring Security Test |

## Architecture

Layered architecture. A request flows:

```
Client -> JwtAuthenticationFilter -> Controller -> Service -> Repository -> H2
                                        |
                                   DTO <-> Entity (MapStruct)
```

- **Controller**: HTTP layer, request validation, role checks (`@PreAuthorize`)
- **Service**: business operations behind interfaces (`TicketService` / `TicketServiceImpl`, etc.) extending a generic `IBaseRepository<T>`
- **Repository**: Spring Data JPA repositories, plus `AgentSpecifications` for dynamic agent filtering
- **Entity**: domain rules live on the entities (e.g. `Ticket` validates status transitions, priority changes and assignment)
- **DTO / Mapper**: request records and response DTOs, mapped with MapStruct
- **Responses**: wrapped in a `Success<T>` / `Failure<T>` result type (`ResultResponse`)

## Project Structure

```
src/main/java/org/example/namia/supportdeskspringboot
├── config/          # SecurityConfig (filter chain, method security), AppConfig (PasswordEncoder, UserDetailsService)
├── controller/      # AuthController, TicketController, AgentController, CommentController
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── model/
│   ├── dto/
│   │   ├── request/   # LoginRequest, RegisterRequest, CreateUpdateTicketRequest, ...
│   │   ├── response/  # TicketDto, AgentDto, CommentDto, TicketHistoryDto, JwtResponse
│   │   └── mapper/    # MapStruct mappers
│   ├── entity/      # User, Agent, Ticket, Comment, TicketHistory, Attachment
│   ├── enums/       # Roles, Priority, TStatus, Department, Action, Visibilities, AssignerOption
│   └── shared/      # ResultResponse, Success, Failure
├── repository/      # JPA repositories + AgentSpecifications
├── security/        # JwtAuthenticationFilter, JwtUtils
└── service/         # Service interfaces and implementations
src/main/resources
└── application.properties
```

## Getting Started

### Prerequisites
- JDK 17+
- Nothing else: Maven is bundled via the wrapper and the database is embedded H2

### Run locally

```bash
git clone https://github.com/aimandesu/Neurogine-Assessment.git
cd Neurogine-Assessment

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

### Build a jar

```bash
./mvnw clean package
java -jar target/SupportDesk-SpringBoot-0.0.1-SNAPSHOT.jar
```

## Configuration

Settings live in `src/main/resources/application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | JDBC URL | `jdbc:h2:file:./data/testdb` |
| `spring.datasource.username` | DB user | `sa` |
| `spring.datasource.password` | DB password | *(empty)* |
| `spring.h2.console.enabled` | Enable H2 web console | `true` |
| `spring.h2.console.path` | H2 console path | `/h2-console` |
| `spring.jpa.hibernate.ddl-auto` | Schema management | `update` |
| `spring.jpa.show-sql` | Log SQL | `true` |
| `app.jwt.secret` | JWT signing key | dev value in file |
| `app.jwt.expiration-ms` | Token lifetime | `86400000` (24h) |

> **Before deploying:** override `app.jwt.secret` with your own secret via an environment variable (e.g. `APP_JWT_SECRET`) and disable the H2 console. The values in the repo are for local development only.

## Database

- Embedded **H2** stored in a file at `./data/testdb`, so data survives restarts
- Schema is generated by Hibernate (`ddl-auto=update`)
- Inspect data at `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:file:./data/testdb`
    - User: `sa`, password empty

Main entities: `User`, `Agent` (one-to-one with `User`, one-to-many with `Ticket`), `Ticket`, `Comment`, `TicketHistory`, `Attachment`.

## Authentication & Authorization

JWT bearer tokens, stateless sessions, CSRF disabled (API-only).

1. Register: `POST /api/auth/register`
2. Login: `POST /api/auth/login` returns a token
3. Send it on every other request: `Authorization: Bearer <token>`

```http
POST /api/auth/register
Content-Type: application/json

{ "email": "admin@example.com", "password": "password123", "role": "Admin" }
```

```http
POST /api/auth/login
Content-Type: application/json

{ "email": "admin@example.com", "password": "password123" }
```

Public routes: `/api/auth/**` and `/h2-console/**`. Everything else requires a valid token.

Roles: `Admin`, `Agent` (enforced with `@PreAuthorize`).

## API Reference

Base path: `/api`

### Auth
| Method | Path | Description | Access |
|--------|------|-------------|--------|
| POST | `/auth/register` | Register a user (email, password, role) | Public |
| POST | `/auth/login` | Log in, returns JWT and user | Public |

### Tickets
| Method | Path | Description | Access |
|--------|------|-------------|--------|
| POST | `/tickets` | Create a ticket (starts as `New`) | Admin |
| GET | `/tickets` | List all tickets | Authenticated |
| GET | `/tickets/{id}` | Get a ticket | Authenticated |
| PUT | `/tickets/{id}` | Update title, description, customer info, priority | Authenticated |
| DELETE | `/tickets/{id}` | Delete a ticket | Authenticated |
| PUT | `/tickets/{id}/assignee` | Assign an agent | Admin |
| DELETE | `/tickets/{id}/assignee` | Unassign an agent | Admin |
| POST | `/tickets/{id}/status` | Change status (with resolution summary) | Authenticated |
| GET | `/tickets/{id}/history` | Ticket audit history | Authenticated |
| POST | `/tickets/{id}/comments` | Add a comment | Authenticated |

### Agents
| Method | Path | Description | Access |
|--------|------|-------------|--------|
| POST | `/agents` | Turn the current user into an agent | Authenticated |
| GET | `/agents` | List agents (filterable via query params) | Authenticated |
| GET | `/agents/{id}` | Get an agent | Authenticated |
| PUT | `/agents/{id}` | Update name, department, email, active | Agent |
| PATCH | `/agents/{id}/active` | Toggle active / inactive | Admin |

### Comments
| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/comments` | List all comments | Authenticated |

### Enums
- **Priority:** `Low`, `Normal`, `High`, `Critical`
- **Status (`TStatus`):** `New`, `InProgress`, `Resolved`, `Closed`
- **Department:** `Technical`, `Billing`, `General`
- **Roles:** `Admin`, `Agent`

## Business Rules

**Status workflow**

```
New --(agent must be assigned)--> InProgress --> Resolved --> Closed
                                      ^             |
                                      +-- reopen ---+  (max 3 times)
```

- Moving to `InProgress` from `New` requires an assigned agent
- Moving to `Resolved` requires a resolution summary
- Setting the same status again is rejected
- `Closed` tickets are read-only (no status changes, no new comments)

**SLA due date** (calculated from priority)

| Priority | Due after creation |
|----------|--------------------|
| Low | 7 days |
| Normal | 3 days |
| High | 1 day |
| Critical | 4 hours |

**Other rules**
- Priority cannot be changed once a ticket is `Resolved` or `Closed`
- Inactive agents cannot be assigned to tickets
- An agent can only be unassigned from a ticket they are assigned to
- An agent with active tickets cannot be deactivated (`AgentHasActiveTicketsException`)
- Emails must be unique (`EmailAlreadyExistsException`)
- Each ticket gets a unique reference like `TCK-2026-0001`
- Status and priority changes are recorded in the ticket history

## Error Handling

Exceptions are translated to consistent error responses by `GlobalExceptionHandler`, including:
- `ResourceNotFoundException` (not found)
- `EmailAlreadyExistsException` (duplicate email)
- `AgentHasActiveTicketsException` (deactivating an agent who still has active tickets)
- Validation errors from `@Valid` request bodies

## Testing

```bash
./mvnw test
```

Currently contains the Spring context load test.

## Known Limitations / Roadmap
- No frontend implementation yet either using (React, Angular etc.) 
- Token refresh and logout are not implemented yet
- `GET /tickets` returns everything; pagination is planned
- Attachments entity exists but there is no upload endpoint yet
- Switch from H2 to PostgreSQL / MySQL for production
- Move secrets out of `application.properties`
- Add unit and integration tests for services and controllers

## References
- https://medium.com/@dulanjayasandaruwan1998/understanding-spring-boot-architecture-flow-615d209b95f9
- https://medium.com/@anandjeyaseelan10/spring-boot-project-structure-explained-best-practices-c2ba46ea57eb
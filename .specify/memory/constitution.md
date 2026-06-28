<!--
SYNC IMPACT REPORT
==================
Version change: [template] → 1.0.0 (initial ratification)
Modified principles: none (first population of template)
Added sections:
  - Core Principles (5 principles defined)
  - Technology Stack and Constraints
  - Evaluation Standards
  - Governance
Removed sections: none
Templates requiring updates:
  - .specify/templates/plan-template.md  ✅ Constitution Check gates now derivable from this document
  - .specify/templates/spec-template.md  ✅ Key Entities and FR structure aligns with domain model
  - .specify/templates/tasks-template.md ✅ Phase structure aligns with evaluation criteria ordering
Follow-up TODOs:
  - JWT implementation in JwtUtil is still a stub; Principle III compliance is partial until completed
  - BCrypt encoding in UsuarioController.save() is absent; Principle IV gate blocks user creation endpoint
  - Docker/deployment instructions are optional but mentioned in professor feedback
-->

# Minimarket Plus Constitution

## Core Principles

### I. Layered Architecture (NON-NEGOTIABLE)

The codebase MUST maintain a strict four-layer structure:
`Controller → Service (interface + impl) → Repository → Entity`

- Controllers MUST NOT contain business logic; they delegate to service interfaces only.
- Service interfaces define the contract; `impl/` classes hold the implementation.
- Repositories extend Spring Data JPA; no raw SQL unless JPA cannot express the query.
- Entities live in `entity/`; they carry JPA annotations and no business logic.
- No cross-layer shortcuts: a controller MUST NOT call a repository directly.
- SOLID and DRY principles MUST be respected: extract shared logic into a common service or
  utility rather than duplicating it across classes.

Rationale: the professor feedback explicitly flagged excessive controller logic and service
duplication as risks. Consistent layering is a gate for the evaluation criterion on
architecture quality.

### II. JUnit Test Coverage (NON-NEGOTIABLE)

Every key entity MUST have unit tests covering at minimum one success scenario and one error
scenario before implementation is considered complete:

- `Producto`: add, update, delete; unauthorized access by non-admin.
- `Inventario`: entry/exit movement; association with product; permission enforcement.
- `Venta`: confirm sale only when all cart items have sufficient stock; cajero-only access.
- `Usuario`: valid and invalid authentication attempts; role enforcement.

Rules:
- Tests MUST be placed under `src/test/java`.
- Mockito MUST be used to isolate service-layer tests from the repository layer.
- Tests MUST run with `./mvnw test` without manual setup.
- The Red-Green-Refactor cycle SHOULD be followed: write the test first, confirm it fails,
  implement the minimum code to pass, then refactor.
- JaCoCo coverage reports SHOULD be generated alongside `mvn test`.

Rationale: IL5-IL8 indicators and evaluation criteria 1-3 require design, configuration,
implementation and execution of unit tests covering key entities. The professor noted only
6 tests existed at S5 delivery; coverage must expand significantly.

### III. API Documentation via Swagger/OpenAPI (NON-NEGOTIABLE)

Every REST endpoint exposed under `/api/**` or `/public/**` MUST be documented using
SpringDoc OpenAPI (Swagger UI):

- Add `springdoc-openapi-starter-webmvc-ui` to `pom.xml`.
- Annotate controllers with `@Tag`, `@Operation`, and `@ApiResponse` where the default
  description is insufficient.
- The Swagger UI MUST be accessible at `/swagger-ui/index.html` when the application runs.
- Security-protected endpoints MUST document their required authentication scheme.
- No new endpoint MAY be delivered without a corresponding OpenAPI description.

Rationale: the professor explicitly stated "integrar Swagger/OpenAPI sería una mejora concreta"
as a direct feedback item, and it is referenced in the user's instructions for this week.

### IV. Security and Role-Based Access Control

Spring Security MUST enforce authentication and authorization on all routes:

- Routes under `/public/**` are open; all others MUST require authentication.
- Three roles are recognized: `ROLE_ADMIN`, `ROLE_CAJERO`, `ROLE_CLIENTE`.
- Producto modification (create, update, delete): ROLE_ADMIN only.
- Inventario movement registration: ROLE_ADMIN or ROLE_CAJERO.
- Venta creation: ROLE_CAJERO only.
- User management: ROLE_ADMIN only.
- Passwords MUST be BCrypt-encoded before persistence; plain-text storage is prohibited.
- JWT: the `JwtUtil` stub MUST be implemented with token generation and validation
  before any endpoint that relies on token-based auth is considered complete.

Rationale: week 6 activity explicitly requires authentication and authorization testing for
each entity. The known gap in BCrypt encoding (CLAUDE.md) is a hard security defect that MUST
be resolved in this branch.

### V. Test Result Analysis and Continuous Improvement

Test execution MUST be followed by documented analysis:

- After each `./mvnw test` run, review pass/fail counts, error messages, and stack traces.
- Identify failure patterns: distinguish real code defects from incorrect assertions.
- Correct defective assertions to match actual business requirements (not the other way around
  unless the business rule itself is wrong).
- Propose and apply at least one concrete improvement per failing test category:
  refactoring, additional edge-case coverage, or corrected assertion.
- Test reports (Surefire XML/HTML) MUST be committed or captured as evidence for the
  technical report required by evaluation criterion 7.

Rationale: IL8 and evaluation criterion 8 require explicit analysis and improvement proposals
derived from test results. This principle makes that expectation operational.

## Technology Stack and Constraints

**Language**: Java 17

**Framework**: Spring Boot 3.4.1

**Build tool**: Maven (Wrapper `./mvnw` — use this, not a system Maven)

**Persistence**: H2 in-memory (`jdbc:h2:mem:testdb`), JPA/Hibernate (`ddl-auto=update`).
  No production database migration is required for this academic context.

**Security**: Spring Security + JWT (form login for browser; JWT for API clients).
  `CustomUserDetailsService` loads users by username. `CustomUserDetails` wraps `Usuario`.

**Testing**: JUnit 5 (via `spring-boot-starter-test`), Mockito, Surefire plugin.
  Coverage: JaCoCo (optional but recommended for criterion 4 evidence).

**API Documentation**: SpringDoc OpenAPI 2.x (`springdoc-openapi-starter-webmvc-ui`).
  Swagger UI is accessible only after authentication (protected route). A Bearer token
  obtained from the login endpoint MUST be provided in the Swagger UI "Authorize" dialog
  to test protected endpoints.

**Lombok**: available in `pom.xml` but entities currently use manual getters/setters;
  either approach is acceptable as long as it compiles cleanly.

**Domain model**:
```
Categoria <-- Producto --> Inventario
               |
  Carrito -----+
               |
  Venta -------+
    └── DetalleVenta (precio snapshot at sale time)

Usuario <-- Carrito
Usuario <-- Venta
Usuario <-> Rol (ManyToMany via usuario_roles)
```

**Constraints**:
- No DTOs: controllers receive and return entity objects directly (current approach; do not
  introduce DTOs unless a future activity explicitly requires it).
- No global exception handler: per-controller 404 handling is the current convention.
- H2 console (`/h2-console`) requires explicit permit in `SecurityConfig` to function;
  do not add it to production permit list without confirmation.

## Evaluation Standards

The following criteria drive the definition of "done" for week 6 work, derived from
`doc/md/pauta.md`. Completamente Logrado (CL, 100%) is the target for each:

| # | Criterion | CL Target |
|---|-----------|-----------|
| 1 | Unit test design for key entities | All entities, all success+error scenarios |
| 2 | Testing environment configuration | Complete, no execution errors |
| 3 | Test implementation with coverage | High coverage, all entities |
| 4 | Test execution and result analysis | Reports generated, coverage analyzed |
| 5 | Process documentation with evidence | Screenshots/reports, justified procedure |
| 6 | GitHub publication | Structured repo, clear README |
| 7 | Technical report | Methodology, config, analysis, justification |
| 8 | Improvement proposals | Well-justified, tied to test results |

## Governance

This constitution supersedes any conflicting conventions found elsewhere in the codebase.
Amendments MUST be made to this file with a version bump following semantic versioning:

- MAJOR: removal or redefinition of a non-negotiable principle.
- MINOR: new principle or section added.
- PATCH: wording clarification or non-semantic refinement.

All feature branches MUST pass a Constitution Check before implementation begins:
- Principle I gate: does the proposed change stay within the layered architecture?
- Principle II gate: are unit tests defined for every new or modified key entity?
- Principle III gate: is Swagger documentation included for every new endpoint?
- Principle IV gate: is BCrypt encoding applied before any user is persisted?
- Principle V gate: is a test analysis documented after each test execution round?

Runtime development guidance: see `CLAUDE.md` at the repository root.

**Version**: 1.0.0 | **Ratified**: 2026-06-28 | **Last Amended**: 2026-06-28

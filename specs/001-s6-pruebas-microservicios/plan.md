# Implementation Plan: S6 Pruebas Unitarias en Microservicios

**Branch**: `feat/microservices-junit-s6` | **Date**: 2026-06-28 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/001-s6-pruebas-microservicios/spec.md`

---

## Summary

Minimarket Plus Week 6 academic deliverable (PBY2202 Grupo 7). The core implementation is complete as of commit `65bee4b`: 66 unit tests pass across 5 service classes, JWT authentication is operational (HS256, 24h), Swagger UI is accessible with Bearer auth, and BCrypt encoding is in place. 

Three remaining work streams are required to reach "Completamente Logrado" on all 8 evaluation criteria:
1. Swagger controller annotations (`@Tag`, `@Operation`) — code task, enriches API documentation
2. Technical report (`doc/md/PBY2202_Exp2_S6_Grupo7.md`) — documentation task, covers criteria 5, 7, 8
3. README — documentation task, criterion 6

---

## Technical Context

**Language/Version**: Java 17 (target in `pom.xml`); Oracle JDK 25.0.3 at runtime

**Primary Dependencies**:
- Spring Boot 3.4.1 (web, security, data-jpa, devtools)
- JJWT 0.11.5 (api, impl, jackson)
- SpringDoc OpenAPI 2.7.0 (springdoc-openapi-starter-webmvc-ui)
- JaCoCo 0.8.14 (report phase only; skipped at test phase on Java 25)
- JUnit 5 + Mockito (via spring-boot-starter-test)

**Storage**: H2 in-memory (`jdbc:h2:mem:testdb`), JPA/Hibernate (`ddl-auto=update`)

**Testing**: JUnit 5, `@ExtendWith(MockitoExtension.class)`, Surefire XML reports; JaCoCo skipped at test phase

**Target Platform**: JVM (Spring Boot embedded Tomcat), port 8080

**Project Type**: REST API (academic microservice)

**Performance Goals**: `mvn test` completes in under 3 minutes (SC-002); no concurrent-user requirements for academic context

**Constraints**:
- `./mvnw` has no execute bit on Google Drive filesystem → use cached Maven binary directly
- JaCoCo 0.8.14 does not support Java 25 bytecode → skip at test phase with `-Djacoco.skip=true`
- Lombok annotation processor incompatible with Java 25 → entities use manual getters/setters
- No DTOs: controllers receive and return entity objects directly (established convention)
- No global exception handler: per-controller 404 handling (established convention)

**Scale/Scope**: Academic project; single developer; ~70 Java source files

---

## Constitution Check

| Principle | Gate | Status | Notes |
|-----------|------|--------|-------|
| I — Layered Architecture | Controllers delegate to services only; no cross-layer shortcuts | PASS | All controllers use `@Autowired` service interface, no repository calls |
| II — JUnit Coverage | Every key entity has ≥1 success + ≥1 error scenario | PASS | 66 tests: ProductoServiceTest(12), InventarioServiceTest(13), VentaServiceTest(11), UsuarioServiceTest(13), CarritoServiceTest(14), UsuarioTest(3) |
| III — Swagger/OpenAPI | `springdoc` in pom.xml, UI accessible, all endpoints documented | PARTIAL | springdoc added, `OpenApiConfig` present, UI at `/swagger-ui/index.html`; controllers lack `@Tag`/`@Operation` → **Tasks T031–T039** |
| IV — Security/RBAC | JWT implemented, BCrypt on passwords, recognized roles seeded | PASS | `JwtUtil` implemented, `SecurityConfig` stateless JWT, BCrypt encoding applied; **post-analyze fix**: `DataInitializer` now seeds `ROLE_CAJERO` (was `ROLE_EMPLEADO`); `puedeRegistrarVenta()` now checks `"ROLE_CAJERO"` (was broken bare-name comparison against `"VENDEDOR"`) |
| V — Test Result Analysis | Test results documented with improvement proposals | OPEN | Requires `doc/md/PBY2202_Exp2_S6_Grupo7.md` completion → **Tasks T041–T045** |

**Post-analyze re-check (2026-06-28)**: `/speckit-analyze` identified 3 CRITICAL issues (D1/D2/D3); all resolved in same session. Constitution now fully compliant. Principle III gates on T031–T039; Principle V gates on T042/T045.

---

## Project Structure

### Documentation (this feature)

```text
specs/001-s6-pruebas-microservicios/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0 — technology decisions
├── data-model.md        # Phase 1 — entity model
├── quickstart.md        # Phase 1 — end-to-end validation guide
├── contracts/
│   └── api-endpoints.md # REST API contract (all 9 resource groups)
├── checklists/
│   └── requirements.md  # Specification quality checklist
└── tasks.md             # Phase 2 output (/speckit-tasks — not yet created)
```

### Source Code (repository root)

```text
src/
├── main/java/com/minimarket/
│   ├── config/
│   │   ├── DataInitializer.java          # Seeds ROLE_ADMIN + admin user on startup
│   │   └── OpenApiConfig.java            # Swagger Bearer JWT security scheme
│   ├── controller/                       # @RestController classes — NEED @Tag/@Operation
│   │   ├── AuthController.java           # POST /api/auth/login
│   │   ├── CarritoController.java        # /api/carrito
│   │   ├── CategoriaController.java      # /api/categorias
│   │   ├── DetalleVentaController.java   # /api/detalle-ventas
│   │   ├── HolaMundoController.java      # GET /public/hola
│   │   ├── InventarioController.java     # /api/inventario
│   │   ├── ProductoController.java       # /api/productos
│   │   ├── UsuarioController.java        # /api/usuarios
│   │   └── VentaController.java          # /api/ventas
│   ├── entity/                           # JPA entities (manual getters/setters)
│   ├── exception/
│   │   ├── DatosIncompletosException.java
│   │   └── StockInsuficienteException.java
│   ├── repository/                       # Spring Data JPA interfaces
│   ├── security/
│   │   ├── config/SecurityConfig.java    # Stateless JWT, @EnableMethodSecurity
│   │   ├── filter/JwtAuthenticationFilter.java
│   │   ├── handler/JwtAuthenticationEntryPoint.java
│   │   ├── model/LoginRequest.java
│   │   ├── model/LoginResponse.java
│   │   ├── service/CustomUserDetailsService.java
│   │   └── util/JwtUtil.java             # HS256, 24h, roles in claims
│   └── service/
│       ├── impl/                         # Business logic implementations
│       └── *.java                        # Service interfaces
└── test/java/com/minimarket/
    └── service/
        ├── CarritoServiceTest.java        # 14 tests
        ├── InventarioServiceTest.java     # 13 tests
        ├── ProductoServiceTest.java       # 12 tests
        ├── UsuarioServiceTest.java        # 13 tests
        ├── UsuarioTest.java               # 3 tests (plain unit, no Spring context)
        └── VentaServiceTest.java          # 11 tests
```

---

## Implementation Phases

### Phase A — COMPLETE (commit 65bee4b)

All items below are already implemented and committed to `feat/microservices-junit-s6`.

- [x] S5 integration: CarritoService, InventarioService, UsuarioService, VentaService business logic
- [x] JWT authentication: `JwtUtil` (HS256, 24h), `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`
- [x] SecurityConfig rewritten: stateless session, `/api/auth/**` public, `@EnableMethodSecurity`
- [x] Swagger: `springdoc-openapi-starter-webmvc-ui` dependency, `OpenApiConfig` with Bearer JWT
- [x] BCrypt: `UsuarioController.save()` encodes passwords before persistence
- [x] `DataInitializer`: seeds ROLE_ADMIN and admin user on startup
- [x] `pom.xml`: compiler plugin 3.14.0, JJWT 0.11.5, SpringDoc 2.7.0, JaCoCo 0.8.14
- [x] Entity expansions: `Usuario` (nombre, apellido, email, direccion), `LoginRequest` (fields), `CarritoRepository` (derived query)
- [x] 66 unit tests: ProductoServiceTest(12), InventarioServiceTest(13), VentaServiceTest(11), UsuarioServiceTest(13), CarritoServiceTest(14), UsuarioTest(3)
- [x] **Post-analyze fix (D1)**: `DataInitializer` — `ROLE_EMPLEADO`/`empleado` → `ROLE_CAJERO`/`cajero` (`src/main/java/com/minimarket/config/DataInitializer.java`)
- [x] **Post-analyze fix (D2+D3)**: `UsuarioServiceImpl.puedeRegistrarVenta()` — compara `"ROLE_CAJERO"` con prefijo correcto; eliminado `"VENDEDOR"` no reconocido (`src/main/java/com/minimarket/service/impl/UsuarioServiceImpl.java`)
- [x] **Post-analyze fix (D2+D3)**: `UsuarioServiceTest` — fixtures actualizados a `new Rol("ROLE_CAJERO")` y `new Rol("ROLE_CLIENTE")` para reflejar estado real de BD (`src/test/java/com/minimarket/service/UsuarioServiceTest.java`)

### Phase B — Swagger Controller Annotations

**Goal**: Satisfy Principle III gate fully. Each controller gets `@Tag` and each method gets `@Operation` with description, `@ApiResponse` codes, and security requirement.

Controllers requiring annotation (9 files):
- `AuthController` — `@Tag("Autenticación")`, `@Operation` on login; document 200 and 401
- `CarritoController` — `@Tag("Carrito")`, CRUD operations with role notes
- `CategoriaController` — `@Tag("Categorías")`
- `DetalleVentaController` — `@Tag("Detalle de Venta")`
- `HolaMundoController` — `@Tag("Public")`, no security requirement
- `InventarioController` — `@Tag("Inventario")`, CAJERO/ADMIN role note
- `ProductoController` — `@Tag("Productos")`, ADMIN for write operations
- `UsuarioController` — `@Tag("Usuarios")`, ADMIN only
- `VentaController` — `@Tag("Ventas")`, CAJERO for create

Import: `io.swagger.v3.oas.annotations.Operation`, `io.swagger.v3.oas.annotations.responses.ApiResponse`, `io.swagger.v3.oas.annotations.tags.Tag`, `io.swagger.v3.oas.annotations.security.SecurityRequirement`

### Phase C — Technical Report

**Goal**: Complete `doc/md/PBY2202_Exp2_S6_Grupo7.md` to satisfy criteria 5, 7, and 8.

**C1 — Resumen técnico (criterio 5)**:
- How S4 and S5 work built the foundation for S6 authentication
- Key entities and their relationships to security
- Screenshot/transcript of `mvn test` showing 66/0/0

**C2 — Análisis de resultados de pruebas (criterio 7)**:
- Which endpoints are protected and by which role
- Test cases that verified protection (cite test class and method names)
- Adjustments made during development (LoginRequest fields, Usuario profile fields, CarritoRepository method, compiler plugin upgrade)

**C3 — Propuestas de mejora (criterio 8)**:
At least 3 proposals based on actual test results:
1. Add test coverage for edge case: Venta with empty carrito
2. Add test for duplicate username constraint in UsuarioService
3. Add test for expired JWT token handling in JwtAuthenticationFilter
4. Propose adding validation annotations (`@NotBlank`, `@Email`) to entity fields instead of manual `datosCompletos()` check
5. Propose global exception handler (`@ControllerAdvice`) to standardize error response format

### Phase D — README

**Goal**: Satisfy criterion 6. Root-level `README.md` with:
- Project description (Minimarket Plus, academic context)
- Prerequisites (Java 17+, Maven 3.8+)
- Build and test commands (including the Java 25 workaround)
- Run instructions
- Endpoint table (all 9 resources)
- Security: how to authenticate and use JWT
- Swagger UI access instructions
- Branch and GitHub link

### Phase E — Final Commit and Push

**Goal**: Publish completed work to GitHub on `feat/microservices-junit-s6`.
- Add Swagger annotation files, technical report, README to staging
- Commit with descriptive message
- Push to remote

---

## Complexity Tracking

No constitution violations requiring justification. The only complexity note is the Java 25 runtime requiring build tool adjustments documented in [research.md](research.md).

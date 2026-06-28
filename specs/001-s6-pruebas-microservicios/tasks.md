# Tasks: S6 Pruebas Unitarias en Microservicios

**Input**: Design documents from `specs/001-s6-pruebas-microservicios/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api-endpoints.md

**Branch**: `feat/microservices-junit-s6` | **Commit base**: `65bee4b`

**Status key**:
- `- [x]` — Complete (committed in Phase A, commit `65bee4b`)
- `- [ ]` — Pending implementation

---

## Phase 1: Setup (Shared Infrastructure) — COMPLETE

**Purpose**: Project initialization and build toolchain configuration.

- [x] T001 Upgrade `maven-compiler-plugin` to 3.14.0 for Java 25 compatibility in `pom.xml`
- [x] T002 Remove `<annotationProcessorPaths>` (Lombok incompatible with Java 25) in `pom.xml`
- [x] T003 [P] Add JJWT 0.11.5 dependencies (api, impl, jackson) to `pom.xml`
- [x] T004 [P] Add SpringDoc OpenAPI 2.7.0 dependency to `pom.xml`
- [x] T005 [P] Add JaCoCo 0.8.14 plugin with `prepare-agent` and `report` goals to `pom.xml`

**Checkpoint**: Build toolchain configured — `mvn compile` succeeds with Java 25.

---

## Phase 2: Foundational (Blocking Prerequisites) — COMPLETE

**Purpose**: Core security infrastructure and entity expansions required by all user stories.

- [x] T006 Implement `JwtUtil` (HS256, 24h expiration, roles claim) in `src/main/java/com/minimarket/security/util/JwtUtil.java`
- [x] T007 Implement `JwtAuthenticationFilter` (validate Bearer token on each request) in `src/main/java/com/minimarket/security/filter/JwtAuthenticationFilter.java`
- [x] T008 Implement `JwtAuthenticationEntryPoint` (return 401 JSON on auth failure) in `src/main/java/com/minimarket/security/handler/JwtAuthenticationEntryPoint.java`
- [x] T009 Rewrite `SecurityConfig` (stateless JWT, `@EnableMethodSecurity`, `/api/auth/**` public) in `src/main/java/com/minimarket/security/config/SecurityConfig.java`
- [x] T010 Add `nombre`, `apellido`, `email`, `direccion` fields to `Usuario` entity in `src/main/java/com/minimarket/entity/Usuario.java`
- [x] T011 Add `findByUsuarioIdAndProductoId` derived query to `CarritoRepository` in `src/main/java/com/minimarket/repository/CarritoRepository.java`
- [x] T012 Add fields and getters/setters to `LoginRequest` in `src/main/java/com/minimarket/security/model/LoginRequest.java`
- [x] T013 Create `DatosIncompletosException` in `src/main/java/com/minimarket/exception/DatosIncompletosException.java`
- [x] T014 Create `StockInsuficienteException` in `src/main/java/com/minimarket/exception/StockInsuficienteException.java`
- [x] T015 Create `DataInitializer` (seeds ROLE_ADMIN and admin user on startup) in `src/main/java/com/minimarket/config/DataInitializer.java`
- [x] T016 Add BCrypt encoding to `UsuarioController.save()` in `src/main/java/com/minimarket/controller/UsuarioController.java`

**Checkpoint**: Foundation ready — JWT works, passwords encoded, entities complete, security enforced.

---

## Phase 3: User Story 1 — Verificar control de acceso con pruebas unitarias (P1) 🎯 COMPLETE

**Goal**: 66 unit tests verify role-based access control across all key entities.

**Independent Test**: `mvn test -Djacoco.skip=true` → `Tests run: 66, Failures: 0, Errors: 0`

- [x] T017 [P] [US1] Implement `CarritoServiceImpl` business logic (`agregarProducto` with stock/cantidad validation) in `src/main/java/com/minimarket/service/impl/CarritoServiceImpl.java`
- [x] T018 [P] [US1] Implement `InventarioServiceImpl` business logic (entry/exit movements, product association) in `src/main/java/com/minimarket/service/impl/InventarioServiceImpl.java`
- [x] T019 [P] [US1] Implement `UsuarioServiceImpl` business logic (`datosCompletos`, `registrar`, `puedeRegistrarVenta`) in `src/main/java/com/minimarket/service/impl/UsuarioServiceImpl.java`
- [x] T020 [P] [US1] Implement `VentaServiceImpl` business logic (`calcularTotal`, `registrarVenta` with stock validation) in `src/main/java/com/minimarket/service/impl/VentaServiceImpl.java`
- [x] T021 [P] [US1] Create `CarritoServiceTest` (14 tests: add product success/error, stock validation, quantity validation) in `src/test/java/com/minimarket/service/CarritoServiceTest.java`
- [x] T022 [P] [US1] Create `InventarioServiceTest` (13 tests: entry/exit movements, product association, permission check) in `src/test/java/com/minimarket/service/InventarioServiceTest.java`
- [x] T023 [P] [US1] Create `UsuarioServiceTest` (13 tests: complete/incomplete data, registrar, puedeRegistrarVenta, role checks) in `src/test/java/com/minimarket/service/UsuarioServiceTest.java`
- [x] T024 [P] [US1] Create `VentaServiceTest` (11 tests: total calculation, sale registration, stock check, cajero-only) in `src/test/java/com/minimarket/service/VentaServiceTest.java`
- [x] T025 [P] [US1] Create `ProductoServiceTest` (12 tests: CRUD operations, findByCategoriaId, unauthorized access scenario) in `src/test/java/com/minimarket/service/ProductoServiceTest.java`
- [x] T026 [US1] Verify all 66 tests pass: `mvn test -Djacoco.skip=true` → BUILD SUCCESS

**Checkpoint**: 66/0/0 test result confirmed. `target/surefire-reports/*.xml` files present.

---

## Phase 4: User Story 2 — Autenticar y autorizar solicitudes de API con JWT (P1) — COMPLETE

**Goal**: API clients can authenticate and receive JWT tokens; all protected endpoints enforce roles.

**Independent Test**: `curl -X POST /api/auth/login` returns `{"token":"..."}` and `curl /api/productos` without token returns 401.

- [x] T027 [US2] Add `AuthController` (`POST /api/auth/login`) in `src/main/java/com/minimarket/controller/AuthController.java`
- [x] T028 [US2] Verify `SecurityConfig` permits `/api/auth/**` and requires auth on all other `/api/**` routes in `src/main/java/com/minimarket/security/config/SecurityConfig.java`
- [x] T029 [US2] Verify `JwtAuthenticationFilter` correctly populates `SecurityContext` from Bearer token in `src/main/java/com/minimarket/security/filter/JwtAuthenticationFilter.java`

**Checkpoint**: Login returns JWT; protected endpoints return 401 without token, 403 with wrong role.

---

## Phase 5: User Story 3 — Consultar la API documentada con Swagger UI (P2)

**Goal**: Swagger UI documents all 9 resource groups with descriptions, parameters, response codes, and security requirements.

**Independent Test**: `http://localhost:8080/swagger-ui/index.html` loads, shows all resources, and "Authorize" with JWT enables endpoint execution.

### Implementation for User Story 3

- [x] T030 [US3] Create `OpenApiConfig` with Bearer JWT security scheme in `src/main/java/com/minimarket/config/OpenApiConfig.java`
- [x] T031 [P] [US3] Add `@Tag("Autenticación")` and `@Operation` (login, 200/401 responses, no security requirement) to `AuthController` in `src/main/java/com/minimarket/controller/AuthController.java`
- [x] T032 [P] [US3] Add `@Tag("Carrito")`, `@Operation` (CRUD with role notes, 200/401/403/409 responses) to `CarritoController` in `src/main/java/com/minimarket/controller/CarritoController.java`
- [x] T033 [P] [US3] Add `@Tag("Categorías")`, `@Operation` (CRUD descriptions, 200/401/403/404 responses) to `CategoriaController` in `src/main/java/com/minimarket/controller/CategoriaController.java`
- [x] T034 [P] [US3] Add `@Tag("Detalle de Venta")`, `@Operation` (create/read descriptions, response codes) to `DetalleVentaController` in `src/main/java/com/minimarket/controller/DetalleVentaController.java`
- [x] T035 [P] [US3] Add `@Tag("Public")`, `@Operation(summary="Health check", security={})` to `HolaMundoController` in `src/main/java/com/minimarket/controller/HolaMundoController.java`
- [x] T036 [P] [US3] Add `@Tag("Inventario")`, `@Operation` (CAJERO/ADMIN roles noted, 200/401/403/409 responses) to `InventarioController` in `src/main/java/com/minimarket/controller/InventarioController.java`
- [x] T037 [P] [US3] Add `@Tag("Productos")`, `@Operation` (ADMIN for write operations, 200/401/403/404 responses) to `ProductoController` in `src/main/java/com/minimarket/controller/ProductoController.java`
- [x] T038 [P] [US3] Add `@Tag("Usuarios")`, `@Operation` (ADMIN only, BCrypt note, 200/401/403/404 responses) to `UsuarioController` in `src/main/java/com/minimarket/controller/UsuarioController.java`
- [x] T039 [P] [US3] Add `@Tag("Ventas")`, `@Operation` (CAJERO for create, 200/401/403/409 responses) to `VentaController` in `src/main/java/com/minimarket/controller/VentaController.java`
- [ ] T040 [US3] Verify Swagger UI: start app, open `http://localhost:8080/swagger-ui/index.html`, confirm all 9 groups present with descriptions and security lock icons

**Checkpoint**: Swagger UI shows complete API documentation. Authorize with JWT enables all protected endpoints.

---

## Phase 6: User Story 4 — Evaluar la calidad con informe técnico y propuestas de mejora (P2)

**Goal**: `doc/md/PBY2202_Exp2_S6_Grupo7.md` contains all 5 deliverable sections with evidence and improvement proposals.

**Independent Test**: Read the document and verify the 5 sections from the activity description are present and substantive.

### Implementation for User Story 4

- [x] T041 [US4] Write Section 1 — Resumen técnico del avance (S4→S5→S6 evolution, key entities, security foundation) in `doc/md/PBY2202_Exp2_S6_Grupo7.md`
- [x] T042 [US4] Write Section 2 — Análisis de resultados: endpoints protected by role, D1/D2/D3 fixes documented in `doc/md/PBY2202_Exp2_S6_Grupo7.md`
- [x] T043 [US4] Write Section 3 — Evidencia de ejecución: `mvn test` console output (67/0/0), Surefire reports, Java 25 + JaCoCo workaround in `doc/md/PBY2202_Exp2_S6_Grupo7.md`
- [x] T044 [US4] Write Section 4 — Preguntas de apoyo: metrics, code improvements after failures, edge case coverage in `doc/md/PBY2202_Exp2_S6_Grupo7.md`
- [x] T045 [US4] Write Section 5 — Propuestas de mejora (5 proposals with test references and metrics) in `doc/md/PBY2202_Exp2_S6_Grupo7.md`

**Checkpoint**: Document has all 5 sections, at least 3 improvement proposals with metric citations, and console evidence of 66/0/0 test result.

---

## Phase 7: User Story 5 — Publicar código en GitHub con README claro (P2)

**Goal**: Root-level `README.md` lets a new developer build, run, and test the project without prior knowledge.

**Independent Test**: Follow README instructions from scratch → `mvn test` succeeds and `http://localhost:8080/swagger-ui/index.html` loads.

### Implementation for User Story 5

- [x] T046 [US5] Create `README.md` at repo root with sections: Project Description, Prerequisites, Build & Test, Run, Endpoint Table, Authentication/JWT flow, Swagger UI instructions, Branch/GitHub link in `README.md`

**Checkpoint**: README reviewed; all commands are copy-pasteable and correct for the Java 25 environment.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, commit, and publication.

- [x] T047 Run full test suite to confirm no regressions from Swagger annotation work: `mvn test -Djacoco.skip=true` → **67/0/0** in `target/surefire-reports/` (67 includes MinimarketApplicationTests context load test)
- [x] T048 Quickstart validation: Scenario 1 (67/0/0 BUILD SUCCESS) y Scenario 7 (Surefire XMLs presentes) verificados. Scenarios 2-6 requieren app en ejecucion (verificacion manual con `./mvnw spring-boot:run`)
- [x] T049 Commit f530202: Swagger annotations, informe técnico, README, D1/D2/D3 fixes y speckit artifacts (56 files, 9245 insertions)
- [x] T050 Push `feat/microservices-junit-s6` a GitHub — https://github.com/kbsg01/DB_II-minimarket

**Checkpoint**: All 8 evaluation criteria achievable at "Completamente Logrado" level.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phases 1–4** (T001–T029): Complete — no action required
- **Phase 5** (T031–T040): Depends on Phases 1–4; controllers annotated in parallel (T031–T039 all touch different files)
- **Phase 6** (T041–T045): Depends on Phase 5 completion (need test results and final Swagger state to write accurately)
- **Phase 7** (T046): Depends on Phase 6 (README references the technical report)
- **Phase 8** (T047–T050): Depends on Phases 5–7 all complete

### User Story Dependencies

- **US1 (P1)**: Complete
- **US2 (P1)**: Complete
- **US3 (P2)**: Can start immediately — 9 controllers can be annotated in parallel (T031–T039)
- **US4 (P2)**: Start after US3 Swagger annotations are validated (needs accurate endpoint list)
- **US5 (P2)**: Start after US4 technical report is drafted (README links to it)

### Within Each User Story (Remaining Work)

- **US3**: T031–T039 all parallelizable (different files); T040 (Swagger UI verification) must follow all annotations
- **US4**: T041–T045 sequential (same document file); commit after all sections complete
- **US5**: T046 single task; reviewed and committed immediately

### Parallel Opportunities

- T031–T039: All 9 controller annotations can be completed in parallel (independent files)
- T041–T044: If authoring in separate sections/passes, multiple sections can be drafted in parallel then merged

---

## Parallel Example: User Story 3

```text
# These 9 tasks touch independent files — launch together:
T031: AuthController.java       — @Tag("Autenticación")
T032: CarritoController.java    — @Tag("Carrito")
T033: CategoriaController.java  — @Tag("Categorías")
T034: DetalleVentaController.java — @Tag("Detalle de Venta")
T035: HolaMundoController.java  — @Tag("Public")
T036: InventarioController.java — @Tag("Inventario")
T037: ProductoController.java   — @Tag("Productos")
T038: UsuarioController.java    — @Tag("Usuarios")
T039: VentaController.java      — @Tag("Ventas")

# After all 9 complete:
T040: Swagger UI verification
```

---

## Implementation Strategy

### MVP First (US3 Only — Minimum for Constitution Principle III)

1. Complete T031–T039 (9 controller annotations) in parallel
2. Complete T040 (Swagger UI verification)
3. **STOP and VALIDATE**: All endpoints visible in Swagger with descriptions
4. Commit Phase B

### Incremental Delivery

1. Phase B (T031–T040) → Swagger complete → commit
2. Phase C (T041–T045) → Technical report complete → commit
3. Phase D (T046) → README complete → commit
4. Phase E (T047–T050) → Final validation + push → delivery ready

### Current Priority Order

1. **T031–T039** (parallel): Swagger controller annotations — blocks T040
2. **T040**: Swagger UI verification — blocks constitution Principle III gate
3. **T041–T045** (sequential, same file): Technical report sections
4. **T046**: README
5. **T047**: Regression test run
6. **T048**: Quickstart validation
7. **T049–T050**: Commit and push

---

## Notes

- `[P]` = task touches an independent file; can run in parallel with other `[P]` tasks in same phase
- All T001–T029 are pre-checked (`[x]`) — committed in Phase A (`65bee4b`)
- T030 is also pre-checked — `OpenApiConfig` is committed
- Remaining work: **20 tasks** (T031–T050)
- Highest-value parallel block: T031–T039 (9 controller annotations, all independent files)
- Commits should group: {T031–T040}, {T041–T045}, {T046}, {T047–T050}

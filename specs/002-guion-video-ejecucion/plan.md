# Implementation Plan: Recuperación de Implementación Previa y Guion del Video EFT

**Branch**: `002-guion-video-ejecucion` | **Date**: 2026-07-16 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/002-guion-video-ejecucion/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Esta feature tiene dos entregables encadenados: (1) consolidar en `feat/eft-s9`, capacidad
por capacidad y sin merges git literales, la mejor implementación real encontrada entre
las 8 ramas semanales del repositorio (S1-S8) — confirmada empíricamente como la de
`feat/microservices-junit-s6`, que resulta ser un superconjunto de S1/S4/S5 y muy superior
al estado real (no al descrito en README) de S7/S8 — y (2) producir un guion de video de
7 a 10 minutos que solo demuestre capacidades verificadas por ejecución real, nunca por
lectura de informes previos. Ambos entregables están directamente motivados por el
feedback real del profesor sobre la entrega S8 (`doc/grupo7.html`), que bajó la
calificación a "Medianamente Logrado" exactamente por la brecha entre informe y código
verificable — brecha que este plan existe para no repetir.

## Technical Context

**Language/Version**: Java 17 (idéntico al resto del proyecto; sin cambios de versión).

**Primary Dependencies**: Las ya confirmadas como reales en `feat/microservices-junit-s6`:
`spring-boot-starter-web/security/data-jpa`, `spring-boot-starter-actuator`,
`io.jsonwebtoken:jjwt-api/impl/jackson` (JJWT 0.11.5), `springdoc-openapi-starter-webmvc-ui`
2.7.0, `jacoco-maven-plugin` (cobertura de pruebas), Lombok, H2. `spring-hateoas` se agrega
como dependencia nueva porque HATEOAS pasó a ser bloqueante (FR-011) y no existe en ninguna
rama real del historial (ver research.md).

**Storage**: H2 en memoria (`jdbc:h2:mem:testdb`), igual que en todas las ramas revisadas.

**Testing**: JUnit 5 + Mockito, heredando las 66 pruebas ya reales de S6
(`CarritoServiceTest`, `InventarioServiceTest`, `ProductoServiceTest`, `UsuarioServiceTest`,
`UsuarioTest`, `VentaServiceTest`) más las pruebas nuevas que exija la implementación real
de HATEOAS. `jacoco-maven-plugin` para evidencia de cobertura citable en el informe.

**Target Platform**: Servidor JVM ejecutado localmente (`./mvnw spring-boot:run`), grabable
en pantalla para el video Kaltura.

**Project Type**: Web service — mismo monolito modular por dominio de `specs/001`. Esta
feature no agrega una arquitectura nueva: reconcilia el estado real del código con la
arquitectura ya definida.

**Performance Goals**: No aplica una meta de rendimiento nueva; el objetivo medible es de
integridad del build: `./mvnw test` MUST terminar en `BUILD SUCCESS` (SC-006), no una meta
de latencia o throughput.

**Constraints**: `feat/eft-s9` MUST ser la única rama de entrega (FR-010); ninguna
capacidad se declara lograda en informe/guion sin evidencia ejecutable (FR-001, FR-003,
FR-011); el build MUST reproducirse en un entorno estándar de revisión sin intervención
manual (SC-006), replicando la condición que el profesor usó para evaluar S8.

**Scale/Scope**: Consolidación de 8 ramas semanales (S1-S8) en una, más la redacción de un
guion de 7 a 10 minutos con segmentos asignados a 2-3 integrantes.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| # | Principio | Estado | Nota |
|---|-----------|--------|------|
| I | Arquitectura de Microservicios por Dominio | PASS | La consolidación reutiliza los mismos paquetes por dominio ya validados en S6 (`controller`, `service`, `service.impl`, `repository`, `entity`, `security`); no se introduce una topología nueva. |
| II | Seguridad No Negociable: JWT y Roles | PASS (al consolidar) | S6 ya tiene JWT real (`JwtAuthenticationFilter`, `JwtUtil` con JJWT, `JwtAuthenticationEntryPoint`) y roles en `SecurityConfig`; consolidarlo en `feat/eft-s9` es precisamente lo que cierra el gate que el plan de `specs/001` había dejado abierto. |
| III | Pruebas Unitarias Obligatorias | PASS | Las 66 pruebas reales de S6 se consolidan tal cual; SC-006 añade un gate de build/tests explícito y bloqueante, más estricto que el mínimo constitucional. |
| IV | Documentación OpenAPI + HATEOAS | GATE ABIERTO (a implementar) | OpenAPI ya es real en S6 (`springdoc-openapi` + `OpenApiConfig`); HATEOAS no existe en ninguna rama y ahora es bloqueante (FR-011) — este plan lo trata como trabajo pendiente explícito, no como algo ya resuelto, para no repetir el error de S8. |
| V | Integración Funcional de Extremo a Extremo | PASS | `quickstart.md` valida el flujo completo (login JWT → endpoint protegido por rol → Swagger UI → suite de pruebas) sobre `feat/eft-s9` ya consolidado. |
| VI | Trazabilidad hacia el Informe y la Evidencia | PASS | Esta feature existe precisamente para restaurar esa trazabilidad después de que `doc/grupo7.html` documentara su ausencia en S8; el guion y el informe solo citan lo verificado. |
| VII | Calidad y Mantenibilidad del Código | PASS | Se retiran duplicados y código stub (p. ej. `JwtUtil` vacío de S7/S8/S9) en favor del código real de S6; se mantiene la convención de paquetes existente. |

No hay violaciones que requieran la tabla de Complexity Tracking: la fila "GATE ABIERTO"
(HATEOAS) es trabajo pendiente que este plan hace explícito, no una excepción al diseño
constitucional.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
src/main/java/com/minimarket/
├── MinimarketApplication.java
├── config/               # DataInitializer, OpenApiConfig (consolidados desde S6)
├── controller/            # incl. AuthController (consolidado desde S6)
├── entity/
├── exception/             # DatosIncompletosException, StockInsuficienteException (S6)
├── repository/
├── security/
│   ├── config/            # SecurityConfig con reglas por rol reales (S6)
│   ├── filter/             # JwtAuthenticationFilter (S6)
│   ├── handler/             # JwtAuthenticationEntryPoint (S6)
│   ├── model/               # CustomUserDetails, LoginRequest, LoginResponse (S6)
│   ├── service/              # CustomUserDetailsService
│   └── util/                 # JwtUtil real con JJWT (S6)
├── service/ y service/impl/
└── web/                   # (nuevo) assemblers HATEOAS — único paquete sin precedente
                            # real en ninguna rama; ver Constitution Check, Principio IV

src/test/java/com/minimarket/
├── UsuarioTest.java                 # consolidado desde S6
└── service/                          # Carrito/Inventario/Producto/Usuario/VentaServiceTest (S6)

doc/
├── grupo7.html                      # feedback del profesor (input obligatorio, no se edita)
└── guion-video-eft.md               # (nuevo) guion final entregable, producido a partir de
                                       # contracts/guion-template.md durante /speckit-tasks
```

**Structure Decision**: Se reutiliza el mismo layout de proyecto único ya definido en
`specs/001-minimarket-backend-spec/plan.md`. Esta feature no agrega una estructura de
código nueva; su trabajo es reconciliar el contenido de esos paquetes con el código real
ya validado en `feat/microservices-junit-s6`, y añadir el único artefacto de documentación
nuevo (`doc/guion-video-eft.md`).

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |

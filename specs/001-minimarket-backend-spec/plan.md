# Implementation Plan: Backend MiniMarket Plus (EFT S9)

**Branch**: `001-minimarket-backend-spec` | **Date**: 2026-07-16 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-minimarket-backend-spec/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

El backend de MiniMarket Plus debe cubrir cuatro capacidades, **todas P1** tras la
clarificación de 2026-07-16 en `specs/002-guion-video-ejecucion`: (1) autenticación JWT
con autorización por roles, (2) inventario centralizado en tiempo real por sucursal con
reposición automática hacia proveedores y reportes de rotación, (3) consulta de
disponibilidad y pedidos en línea (retiro/despacho) con promociones centralizadas, y (4)
documentación de API bajo OpenAPI (OAS) con navegación HATEOAS — esta última reclasificada
de P3 a P1 porque el feedback real del profesor sobre la entrega de la Semana 8
(`doc/grupo7.html`) bajó la calificación a "Medianamente Logrado" precisamente por declarar
HATEOAS sin código real que lo respaldara.

**Dependencia con `specs/002-guion-video-ejecucion`**: ese plan ya determinó que el gate II
(JWT y roles) no se implementa desde cero, sino que se **consolida** desde el código real
y ya probado de `feat/microservices-junit-s6` (JWT completo, OpenAPI, 66 pruebas
unitarias) hacia `feat/eft-s9`, evitando repetir trabajo. Este plan asume esa consolidación
como prerrequisito de su propia fase de Setup, y se enfoca en lo que la consolidación de
S6 **no** cubre: las entidades de dominio faltantes (`Sucursal`, `Proveedor`,
`OrdenDeCompra`, `Promocion`, `Pedido`) y la implementación real de HATEOAS (que no existe
en ninguna rama del historial).

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot 3.4.1 (`spring-boot-starter-web`,
`spring-boot-starter-data-jpa`, `spring-boot-starter-security`),
`spring-boot-starter-actuator`, `jacoco-maven-plugin`. `io.jsonwebtoken:jjwt-api/impl/jackson`
(JWT) y `springdoc-openapi-starter-webmvc-ui` (OAS + Swagger UI) se obtienen **consolidando
el código real de `feat/microservices-junit-s6`** (ver `specs/002-guion-video-ejecucion/research.md`),
no implementándolos desde cero. `org.springframework.hateoas:spring-hateoas` sí es una
dependencia nueva: ninguna rama del historial tiene HATEOAS real. **Lombok NO es una
dependencia del proyecto**: se evaluó durante la consolidación de S6 y se retiró por ser
incompatible con el *annotation processing* de JDK 25 del entorno de build, y por no tener
ningún uso real en el código (`specs/002-guion-video-ejecucion/research.md`, Decisión 7).
Ver [research.md](./research.md) para las decisiones y alternativas evaluadas.

**Storage**: H2 en memoria vía Spring Data JPA / Hibernate (perfil ya configurado en
`application.properties`); esquema gestionado por `spring.jpa.hibernate.ddl-auto=update`.

**Testing**: JUnit 5 + Mockito (`spring-boot-starter-test`, ya en `pom.xml`) para pruebas
unitarias de servicios; `spring-security-test` (ya en `pom.xml`) para pruebas de
autorización por rol con `@WithMockUser` a nivel de controlador.

**Target Platform**: Servidor JVM ejecutando el backend REST localmente (`mvn
spring-boot:run`), suficiente para la evaluación EFT sin infraestructura cloud.

**Project Type**: Web service — monolito modular por dominio (single Spring Boot
project). "Microservicios" se interpreta, según la Constitución del proyecto (Principio
I), como módulos de dominio cohesionados (controller/service/repository/entity) dentro de
un único desplegable, no como servicios físicamente independientes.

**Performance Goals**: Responder operaciones CRUD y de consulta en menos de 2 segundos
bajo el uso típico de una demostración académica (decenas de solicitudes, no carga de
producción); no se exige una SLA formal.

**Constraints**: Cumplimiento de la Ley de Protección de Datos Personales de Chile
(no exponer contraseñas ni datos financieros completos en respuestas); autenticación
MUST usar JWT por exigencia explícita del caso de negocio; el sistema MUST ejecutarse
con el perfil H2 ya configurado, sin dependencias de servicios externos reales.

**Scale/Scope**: Equipo de 2-3 integrantes, entrega en la Semana 9 del curso. El modelo
de datos MUST soportar múltiples sucursales (dimensión `Sucursal`), sin requerir que las
10 sucursales reales de la cadena estén cargadas para la evaluación.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| # | Principio | Estado | Nota |
|---|-----------|--------|------|
| I | Arquitectura de Microservicios por Dominio | PASS | Se mantienen y amplían los paquetes por dominio existentes (`controller`, `service`, `service.impl`, `repository`, `entity`); se añaden dominios `sucursal`, `proveedor`, `ordencompra`, `promocion`, `pedido` siguiendo el mismo patrón. |
| II | Seguridad No Negociable: JWT y Roles | PASS (vía consolidación) | Ya no se implementa desde cero: se resuelve consolidando el JWT real de `feat/microservices-junit-s6` según `specs/002-guion-video-ejecucion/plan.md`, que este plan asume como prerrequisito de su fase de Setup. |
| III | Pruebas Unitarias Obligatorias | PASS | El plan reserva pruebas de servicio (JUnit 5 + Mockito) y de autorización (`spring-security-test`) como parte de cada historia de usuario; se heredan además las 66 pruebas reales ya consolidadas desde S6. |
| IV | Documentación OpenAPI + HATEOAS | GATE ABIERTO Y BLOQUEANTE (reclasificado) | OpenAPI se resuelve por consolidación (igual que JWT). HATEOAS **no existe en ninguna rama real** y ahora es bloqueante (Historia de Usuario 4 pasó de P3 a P1): ni el informe ni el guion del video (`specs/002`, FR-011) pueden declararlo logrado sin código verificable (`EntityModel`/`CollectionModel`/`linkTo`), replicando el motivo exacto por el que `doc/grupo7.html` bajó la calificación de S8. |
| V | Integración Funcional de Extremo a Extremo | PASS | `quickstart.md` define el flujo de validación end-to-end (login → catálogo → pedido → descuento de stock → venta) exigido por este principio. |
| VI | Trazabilidad hacia el Informe y la Evidencia | PASS | `research.md`, `data-model.md` y `quickstart.md` quedan disponibles como evidencia reutilizable para la plantilla oficial de informe; ver también el gate de build/tests (SC-006 en `specs/002`) que refuerza este principio. |
| VII | Calidad y Mantenibilidad del Código | PASS | Se mantiene la convención de paquetes y nomenclatura en español ya establecida; se identifica `HolaMundoController` como código de prueba a retirar antes de la entrega final. |

No hay violaciones que requieran la tabla de Complexity Tracking: la fila "GATE ABIERTO Y
BLOQUEANTE" (HATEOAS) es trabajo pendiente de implementación explícitamente priorizado,
no una excepción al diseño constitucional.

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
├── controller/          # REST controllers per domain (existentes + nuevos)
├── service/             # Service interfaces per domain
├── service/impl/        # Service implementations
├── repository/          # Spring Data JPA repositories
├── entity/              # JPA entities (existentes + Sucursal, Proveedor,
│                         # OrdenDeCompra, Promocion, Pedido)
├── security/
│   ├── config/          # SecurityConfig (a reescribir con filtro JWT + reglas por rol)
│   ├── model/            # CustomUserDetails, LoginRequest
│   ├── service/           # CustomUserDetailsService
│   └── util/              # JwtUtil (a implementar)
└── web/                  # (nuevo) ensamblado de EntityModel/HATEOAS y config OpenAPI

src/test/java/com/minimarket/
├── service/              # Pruebas unitarias de servicios (JUnit 5 + Mockito)
└── security/             # Pruebas de autorización por rol (@WithMockUser)
```

**Structure Decision**: Se conserva la estructura Maven de proyecto único ya presente en
el repositorio (`src/main/java/com/minimarket/...`), organizada por dominio dentro de los
paquetes `controller`, `service`, `service.impl`, `repository` y `entity`. No se adopta un
layout multi-módulo ni microservicios físicamente separados (ver Constitución, Principio
I, y Technical Context → Project Type). Las pruebas replican el mismo árbol de paquetes
bajo `src/test/java/com/minimarket/`.

## Complexity Tracking

No aplica: el Constitution Check no registra violaciones que requieran justificación. Los
gates abiertos (JWT/OpenAPI vía consolidación, HATEOAS bloqueante) son trabajo pendiente
de implementación, no excepciones al diseño constitucional.

# Implementation Plan: Documentación Avanzada OpenAPI y HATEOAS

**Branch**: `feat/hateoas-s8` | **Date**: 2026-07-11 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-hateoas-openapi-avanzado/spec.md`

## Summary

Sobre el backend Minimarket Plus ya integrado en `feat/openapi-docs-s7` (Spring Boot 3.4.1, JPA/H2, HTTP Basic, OpenAPI 3 vía springdoc), se agrega Spring HATEOAS a los 7 controladores de negocio (Producto, Categoría, Carrito, Inventario, Usuario, Venta, DetalleVenta): cada recurso se envuelve en `EntityModel`/`CollectionModel` con enlaces `self`, colección y relaciones significativas entre entidades, construidos mediante un `RepresentationModelAssembler` por recurso. Se reutilizan métodos de repositorio ya existentes pero sin usar (`findByCategoriaId`, `findByProductoId`, `findByVentaId`, `findByUsuarioId`) exponiéndolos como parámetros de filtro opcionales en los endpoints de colección existentes, de forma que los enlaces relacionados apunten a URIs reales y funcionales sin crear nuevas rutas anidadas. La documentación OpenAPI existente se amplía para reflejar los nuevos modelos de respuesta con `_links`.

## Technical Context

**Language/Version**: Java 17, Spring Boot 3.4.1 (Maven)

**Primary Dependencies**: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, springdoc-openapi-starter-webmvc-ui 2.7.0 (ya presentes) + **spring-boot-starter-hateoas** (nueva, org.springframework.hateoas)

**Storage**: H2 en memoria (sin cambios; datos de demostración vía `DataLoader`)

**Testing**: JUnit 5 + Spring Boot Test + JaCoCo (ya configurado); se agregan pruebas `@WebMvcTest`/`@SpringBootTest` que verifican la presencia y resolución de `_links` en las respuestas

**Target Platform**: JVM, servidor embebido Tomcat, ejecución local (`http://localhost:8080`)

**Project Type**: Servicio backend único (API REST), sin frontend

**Performance Goals**: N/A — no es una feature de rendimiento; se mantiene el comportamiento de respuesta síncrona actual sin degradación perceptible en el dataset de demostración

**Constraints**: No romper la autenticación HTTP Basic ni las rutas `/api/**` ya documentadas y usadas por el README/Postman; los cambios deben ser aditivos sobre el contrato OpenAPI existente (no eliminar ni renombrar operaciones vigentes)

**Scale/Scope**: 7 controladores de negocio, ~30 operaciones REST, dataset de demostración de pocas decenas de filas en H2

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` no ha sido completado en este proyecto (plantilla sin llenar, sin principios definidos). No existen gates de constitución que evaluar; se procede usando como única guía la pauta de evaluación sumativa S8 y las convenciones ya establecidas en el código de S1–S7 (controladores delgados, servicios con lógica, anotaciones `@Schema`/`@Operation` explícitas, sin Lombok).

**Resultado**: PASS (sin gates aplicables).

## Project Structure

### Documentation (this feature)

```text
specs/001-hateoas-openapi-avanzado/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md         # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/
│   └── hateoas-links.md # Phase 1 output — contrato de enlaces por recurso
└── tasks.md             # Phase 2 output (/speckit-tasks)
```

### Source Code (repository root)

Proyecto Spring Boot único (sin frontend separado); estructura ya existente que se extiende:

```text
src/main/java/com/minimarket/
├── config/
│   ├── DataLoader.java
│   └── OpenApiConfig.java
├── controller/                 # se modifican: envuelven respuestas en EntityModel/CollectionModel
│   ├── ProductoController.java
│   ├── CategoriaController.java
│   ├── CarritoController.java
│   ├── InventarioController.java
│   ├── UsuarioController.java
│   ├── VentaController.java
│   └── DetalleVentaController.java
├── assembler/                  # NUEVO: un RepresentationModelAssembler por recurso
│   ├── ProductoModelAssembler.java
│   ├── CategoriaModelAssembler.java
│   ├── CarritoModelAssembler.java
│   ├── InventarioModelAssembler.java
│   ├── UsuarioModelAssembler.java
│   ├── VentaModelAssembler.java
│   └── DetalleVentaModelAssembler.java
├── entity/                     # sin cambios estructurales
├── repository/                 # sin cambios (métodos finder ya existen)
├── service/ e impl/            # sin cambios de contrato (se mantiene List<T> como hoy)
└── security/                   # sin cambios (fuera de alcance de esta feature)

src/test/java/com/minimarket/
└── controller/ (o hateoas/)    # NUEVO: pruebas que validan _links por recurso
```

**Structure Decision**: Se mantiene el proyecto único ya existente en la raíz del repo (no se usa `S8/minimarket/`, ver Assumptions del spec). Se añade el paquete `assembler` como única capa nueva, siguiendo el patrón estándar de Spring HATEOAS (`RepresentationModelAssembler`) para no duplicar lógica de construcción de enlaces dentro de cada controlador.

## Complexity Tracking

*Sin violaciones que justificar — no hay gates de constitución definidos y la única capa nueva (`assembler`) es el patrón estándar recomendado por Spring HATEOAS para este caso de uso.*

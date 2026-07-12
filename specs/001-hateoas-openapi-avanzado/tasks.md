---

description: "Task list for Documentación Avanzada OpenAPI y HATEOAS (S8)"
---

# Tasks: Documentación Avanzada OpenAPI y HATEOAS

**Input**: Design documents from `specs/001-hateoas-openapi-avanzado/`

**Prerequisites**: [plan.md](./plan.md), [spec.md](./spec.md), [research.md](./research.md), [data-model.md](./data-model.md), [contracts/hateoas-links.md](./contracts/hateoas-links.md), [quickstart.md](./quickstart.md)

**Tests**: Se incluyen tareas de prueba para US2 (verificación de `_links`), siguiendo la convención JUnit/JaCoCo ya establecida en el proyecto desde S4–S6.

**Organization**: Tareas agrupadas por historia de usuario (US1, US2, US3) según `spec.md`.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias)
- **[Story]**: Historia de usuario a la que pertenece (US1, US2, US3)

## Path Conventions

Proyecto Spring Boot único en la raíz del repo: `src/main/java/com/minimarket/...`, `src/test/java/com/minimarket/...`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar la dependencia de Spring HATEOAS que usan US1 (esquemas) y US2 (enlaces reales)

- [X] T001 Agregar dependencia `org.springframework.boot:spring-boot-starter-hateoas` en `pom.xml` (raíz del repo), junto a las dependencias ya existentes de `spring-boot-starter-web` y `springdoc-openapi-starter-webmvc-ui`
- [X] T002 Ejecutar `./mvnw -q compile` para confirmar que la nueva dependencia resuelve y el proyecto compila sin cambios adicionales

**Checkpoint**: Spring HATEOAS disponible en el classpath; ninguna historia de usuario requiere pasos adicionales de infraestructura antes de empezar

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: No aplica — no hay infraestructura bloqueante adicional. Los métodos de repositorio/servicio que usará US2 (`findByCategoriaId`, `findByProductoId`, `findByUsuarioId`, `findByVentaId`) ya existen en el código de `feat/openapi-docs-s7` y no requieren cambios (ver [data-model.md](./data-model.md)).

**Checkpoint**: Fundación lista (Phase 1 completa) — las historias de usuario pueden comenzar

---

## Phase 3: User Story 1 - Documentación OpenAPI avanzada alineada a OAS (Priority: P1) 🎯 MVP

**Goal**: Completar la documentación OpenAPI de los 7 controladores de negocio con los códigos de respuesta y ejemplos que aún faltan (en particular `400 Bad Request`), y reforzar la descripción global del contrato para que mencione la navegación por enlaces — de forma verificable en Swagger UI sin depender de que HATEOAS esté implementado en runtime todavía.

**Independent Test**: Abrir `http://localhost:8080/swagger-ui.html`, expandir cada operación `POST`/`PUT` de los 7 recursos y confirmar que documenta `400`, `401` y (según corresponda) `404`, con ejemplos de cuerpo de petición — sin necesidad de que los enlaces `_links` existan aún en las respuestas reales.

### Implementation for User Story 1

- [X] T003 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/ProductoController.java` (validación de campos obligatorios: nombre, precio, stock, categoría)
- [X] T004 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/CategoriaController.java`
- [X] T005 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/CarritoController.java`
- [X] T006 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/InventarioController.java`
- [X] T007 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/UsuarioController.java`
- [X] T008 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a la operación `POST` en `src/main/java/com/minimarket/controller/VentaController.java`
- [X] T009 [P] [US1] Agregar `@ApiResponse(responseCode = "400", ...)` a las operaciones `POST`/`PUT` en `src/main/java/com/minimarket/controller/DetalleVentaController.java`
- [X] T010 [US1] Actualizar `info().description()` en `src/main/java/com/minimarket/config/OpenApiConfig.java` para mencionar que las respuestas de recurso individual y colección incluyen enlaces de hipermedia (`_links`) para navegación entre recursos relacionados
- [X] T011 [US1] Actualizar la tabla "Endpoints documentados" y agregar sección sobre `_links`/parámetros de filtro (`categoriaId`, `productoId`, `usuarioId`, `ventaId`) en `README.md`

**Checkpoint**: Historia 1 completa — la documentación OpenAPI está lista para reflejar el nuevo formato de respuesta que introducirá la Historia 2

---

## Phase 4: User Story 2 - HATEOAS con enlaces dinámicos (Priority: P2)

**Goal**: Envolver las respuestas de los 7 controladores de negocio en `EntityModel`/`CollectionModel` con enlaces `self`, colección y relaciones (ver [contracts/hateoas-links.md](./contracts/hateoas-links.md)), agregando los parámetros de filtro opcionales que habilitan los enlaces relacionados, sin romper la autenticación HTTP Basic existente.

**Independent Test**: Con `./mvnw spring-boot:run` corriendo, ejecutar `curl -u admin:admin123 http://localhost:8080/api/productos/1` y verificar que la respuesta trae `_links.self`, `_links.productos`, `_links.categoria`, `_links.inventario` con URIs resolubles; ejecutar `curl -i http://localhost:8080/api/productos/1` (sin credenciales) y verificar `401`.

### Tests for User Story 2

- [X] T012 [P] [US2] Prueba `@SpringBootTest` + `MockMvc` que verifica `_links.self`, `_links.categoria` e `_links.inventario` en `GET /api/productos/{id}` en `src/test/java/com/minimarket/controller/ProductoControllerHateoasTest.java`
- [X] T013 [P] [US2] Prueba que verifica `_links.detalles` en `GET /api/ventas/{id}` y que `GET /api/detalle-ventas?ventaId={id}` filtra correctamente en `src/test/java/com/minimarket/controller/VentaControllerHateoasTest.java`
- [X] T014 [P] [US2] Prueba que verifica que un request sin credenciales a `GET /api/productos/{id}` sigue devolviendo `401` tras envolver la respuesta en `EntityModel` en `src/test/java/com/minimarket/controller/HateoasSecurityRegressionTest.java`

### Implementation for User Story 2

- [X] T015 [P] [US2] Crear `src/main/java/com/minimarket/assembler/ProductoModelAssembler.java` (`RepresentationModelAssembler<Producto, EntityModel<Producto>>`) con enlaces `self`, `productos`, `categoria`, `inventario`
- [X] T016 [P] [US2] Crear `src/main/java/com/minimarket/assembler/CategoriaModelAssembler.java` con enlaces `self`, `categorias`, `productos`
- [X] T017 [P] [US2] Crear `src/main/java/com/minimarket/assembler/CarritoModelAssembler.java` con enlaces `self`, `carritos`, `producto`, `usuario`
- [X] T018 [P] [US2] Crear `src/main/java/com/minimarket/assembler/InventarioModelAssembler.java` con enlaces `self`, `inventario`, `producto`
- [X] T019 [P] [US2] Crear `src/main/java/com/minimarket/assembler/UsuarioModelAssembler.java` con enlaces `self`, `usuarios`, `carritos`, `ventas`
- [X] T020 [P] [US2] Crear `src/main/java/com/minimarket/assembler/VentaModelAssembler.java` con enlaces `self`, `ventas`, `usuario`, `detalles`
- [X] T021 [P] [US2] Crear `src/main/java/com/minimarket/assembler/DetalleVentaModelAssembler.java` con enlaces `self`, `detalle-ventas`, `venta`, `producto`
- [X] T022 [US2] Modificar `src/main/java/com/minimarket/controller/ProductoController.java`: inyectar `ProductoModelAssembler`, cambiar los métodos GET/POST/PUT para devolver `EntityModel<Producto>`/`CollectionModel<EntityModel<Producto>>`, y agregar `@RequestParam(required = false) Long categoriaId` en el listado que delega a `productoService.findByCategoriaId` cuando está presente (depende de T015)
- [X] T023 [US2] Modificar `src/main/java/com/minimarket/controller/CategoriaController.java`: inyectar `CategoriaModelAssembler`, devolver `EntityModel`/`CollectionModel` en GET/POST/PUT (depende de T016)
- [X] T024 [US2] Modificar `src/main/java/com/minimarket/controller/CarritoController.java`: inyectar `CarritoModelAssembler`, devolver `EntityModel`/`CollectionModel`, agregar `@RequestParam(required = false) Long usuarioId` delegando a `carritoService.findByUsuarioId` (depende de T017)
- [X] T025 [US2] Modificar `src/main/java/com/minimarket/controller/InventarioController.java`: inyectar `InventarioModelAssembler`, devolver `EntityModel`/`CollectionModel`, agregar `@RequestParam(required = false) Long productoId` delegando a `inventarioService.findByProductoId` (depende de T018)
- [X] T026 [US2] Modificar `src/main/java/com/minimarket/controller/UsuarioController.java`: inyectar `UsuarioModelAssembler`, devolver `EntityModel`/`CollectionModel` en GET/POST/PUT (depende de T019)
- [X] T027 [US2] Modificar `src/main/java/com/minimarket/controller/VentaController.java`: inyectar `VentaModelAssembler`, devolver `EntityModel`/`CollectionModel`, agregar `@RequestParam(required = false) Long usuarioId` delegando a `ventaService.findByUsuarioId` (depende de T020)
- [X] T028 [US2] Modificar `src/main/java/com/minimarket/controller/DetalleVentaController.java`: inyectar `DetalleVentaModelAssembler`, devolver `EntityModel`/`CollectionModel`, agregar `@RequestParam(required = false) Long ventaId` delegando a `detalleVentaService.findByVentaId` (depende de T021)
- [X] T029 [US2] Actualizar los `@ApiResponse` con `content = @Content(schema = @Schema(implementation = ...))` de los 7 controladores para referenciar el tipo envolvente (`EntityModel<T>`/`CollectionModel<EntityModel<T>>>`) en vez de la entidad plana, consistente con T022–T028
- [X] T030 [US2] Ejecutar `./mvnw test` y confirmar que la suite completa (existente + T012–T014) pasa en verde

**Checkpoint**: Historias 1 y 2 completas — Swagger UI documenta los enlaces y las respuestas reales de la API los incluyen, sin romper la autenticación existente

---

## Phase 5: User Story 3 - Informe técnico y entrega (Priority: P3)

**Goal**: Redactar el informe técnico pedido por el Paso 5 de la guía de aprendizaje (resumen técnico, análisis de la documentación, evidencia de ejecución, reflexión técnica y preguntas de apoyo) y dejar el proyecto listo para subir a GitHub.

**Independent Test**: Revisar que el archivo del informe cubre los 4 puntos del Paso 5 y enlaza evidencia verificable (comandos/capturas descritos en [quickstart.md](./quickstart.md)).

### Implementation for User Story 3

- [X] T031 [US3] Ejecutar los escenarios de `quickstart.md` (Swagger UI, curl con y sin credenciales, filtro por query param, exportación a Postman) y registrar las salidas/capturas como evidencia
- [X] T032 [US3] Redactar `doc/md/PBY2202_Exp3_S8_Grupo7.md` con: resumen técnico del avance respecto a S7, análisis de la documentación generada (qué endpoints se documentaron con más detalle, dificultades al integrar HATEOAS y solución, mejoras realizadas), evidencia de ejecución (referencia a T031), reflexión técnica (aporte de OpenAPI+HATEOAS a calidad/mantenibilidad/navegabilidad, estrategia de mantención futura) y respuestas a las preguntas de apoyo de la guía
- [X] T033 [US3] Actualizar `openapi/api-docs.json` exportando el contrato desde `/v3/api-docs` una vez implementada la Historia 2

**Checkpoint**: Las 3 historias de usuario están completas; el proyecto queda listo para commit/push y entrega en GitHub

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — inicia de inmediato
- **Foundational (Phase 2)**: No aplica (ver nota) — no bloquea a las historias
- **User Story 1 (Phase 3)**: Depende de Setup (T001). No depende de US2
- **User Story 2 (Phase 4)**: Depende de Setup (T001). Es funcionalmente independiente de US1, pero T029 reutiliza los archivos ya tocados en T003–T009 (mismo controlador), por lo que conviene completar US1 antes de US2 en cada controlador para evitar conflictos de edición
- **User Story 3 (Phase 5)**: Depende de que US1 y US2 estén implementadas (necesita evidencia real de Swagger UI y `_links` funcionando)

### Parallel Opportunities

- T003–T009 (US1, un controlador cada una) son paralelas entre sí
- T015–T021 (assemblers, US2) son paralelas entre sí
- T022–T028 (controladores, US2) tienen dependencia 1 a 1 con su assembler (T022 depende de T015, etc.) pero son paralelas entre sí una vez creados los assemblers
- T012–T014 (tests US2) pueden escribirse en paralelo antes de T022–T028, y deben fallar hasta que la implementación esté lista

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (Setup)
2. Completar Phase 3 (US1) — documentación OpenAPI ya alineada a OAS con 400 documentado
3. Validar en Swagger UI de forma independiente
4. Continuar con Phase 4 (US2) para completar los criterios 2, 3 y 4 de la pauta (mayor ponderación)

### Incremental Delivery

1. Setup → US1 (documentación) → validar en Swagger UI
2. + US2 (enlaces reales) → validar con curl/Postman siguiendo `quickstart.md`
3. + US3 (informe) → entrega final en GitHub

## Notes

- [P] = archivos distintos, sin dependencias entre sí
- Commit sugerido por fase (US1, luego US2, luego US3) para mantener trazabilidad con la pauta de evaluación
- Verificar que `./mvnw test` se mantiene en verde después de cada fase

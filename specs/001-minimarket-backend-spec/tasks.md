---

description: "Task list for Backend MiniMarket Plus (EFT S9)"
---

# Tasks: Backend MiniMarket Plus (EFT S9)

**Input**: Design documents from `specs/001-minimarket-backend-spec/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml,
quickstart.md, y la **Fase 2 Foundational de `specs/002-guion-video-ejecucion/tasks.md`
ya completa** (JWT, roles y OpenAPI reales consolidados en `feat/eft-s9`, `BUILD SUCCESS`
confirmado).

**Tests**: Se incluyen tareas de prueba porque el Principio III de la Constitución
(NON-NEGOTIABLE) exige pruebas unitarias para cada servicio de negocio nuevo.

**Organization**: Tareas agrupadas por historia de usuario para permitir implementación y
verificación independiente de cada una.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias pendientes)
- **[Story]**: Historia de usuario a la que pertenece la tarea (US1, US2, US3, US4)
- Cada tarea incluye rutas de archivo exactas

## Path Conventions

Proyecto único Maven: `src/main/java/com/minimarket/...`, `src/test/java/com/minimarket/...`.

---

## Phase 1: Setup

**Purpose**: Confirmar el prerrequisito de consolidación antes de construir sobre él

- [X] T001 Confirmar que `specs/002-guion-video-ejecucion/tasks.md` (Fase 2 Foundational,
      T004-T015) ya está completa: `./mvnw test` en `BUILD SUCCESS` con JWT real, roles y
      `springdoc-openapi` consolidados, y `spring-hateoas` ya presente en `pom.xml`. Este
      plan **no repite** esa consolidación; solo continúa a partir de ella.
      ✅ Confirmado: `specs/002` completa (T001-T025, T029-T030), `BUILD SUCCESS` con
      67/67 pruebas antes de iniciar este plan.

**Checkpoint**: Base de seguridad/OpenAPI real confirmada — se puede empezar a construir
las entidades y capacidades nuevas de este plan.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Entidades y ajustes de dominio compartidos por más de una historia de
usuario (Sucursal, Proveedor, y los cambios sobre `Producto`/`Inventario`/`Usuario` que
todas las historias necesitan)

**⚠️ CRITICAL**: Ninguna historia de usuario puede comenzar hasta que T002-T010 compilen.

- [X] T002 [P] Crear entidad `Sucursal` en `src/main/java/com/minimarket/entity/Sucursal.java`
      (`id`, `nombre`, `direccion`, `region` — data-model.md).
- [X] T003 [P] Crear entidad `Proveedor` en `src/main/java/com/minimarket/entity/Proveedor.java`
      (`id`, `nombre`, `contacto` — data-model.md).
- [X] T004 Crear `SucursalRepository` en
      `src/main/java/com/minimarket/repository/SucursalRepository.java` (depende de T002).
- [X] T005 Crear `ProveedorRepository` en
      `src/main/java/com/minimarket/repository/ProveedorRepository.java` (depende de T003).
- [X] T006 Modificar `src/main/java/com/minimarket/entity/Producto.java` agregando los
      campos `stockMinimo` (Integer) y `proveedor` (`@ManyToOne Proveedor`) — data-model.md
      (depende de T003).
- [X] T007 Modificar `src/main/java/com/minimarket/entity/Inventario.java` agregando el
      campo `sucursal` (`@ManyToOne Sucursal`, not null) — data-model.md (depende de T002).
- [X] T008 [P] En `src/main/java/com/minimarket/entity/Usuario.java`, anotar el campo
      `password` con `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` (o
      equivalente) para que nunca se incluya en respuestas serializadas — FR-004,
      Principio II de la Constitución.
- [X] T009 ~~Crear el paquete `web/` con una clase de configuración base para HATEOAS~~
      **Diferido a T037**: Spring Boot autoconfigura HATEOAS con solo tener `spring-hateoas`
      en el classpath (ya agregado en `specs/002`); crear un paquete/clase vacía ahora
      habría sido código muerto (mismo criterio ya aplicado a Lombok y `LoginResponse` en
      `specs/002`). El paquete `web/` se crea recién en T037 con el primer assembler real.
- [X] T010 Ejecutar `./mvnw compile` para confirmar que las entidades nuevas y modificadas
      (T002-T009) compilan correctamente antes de iniciar cualquier historia de usuario.
      ✅ `BUILD SUCCESS`, 57 archivos fuente.

**Checkpoint**: Modelo de dominio base listo — las 4 historias de usuario pueden avanzar.

---

## Phase 3: User Story 1 - Autenticación segura y autorización por roles (Priority: P1) 🎯 MVP

**Goal**: Cada uno de los 7 roles del caso de negocio accede únicamente a las operaciones
que le corresponden.

**Independent Test**: Registrar un usuario por rol, iniciar sesión, y verificar que cada
uno accede solo a las operaciones permitidas (spec.md, Acceptance Scenarios 1-4).

### Implementation for User Story 1

- [X] T011 [US1] Actualizar los datos semilla (`DataInitializer`, consolidado desde
      `specs/002`) en `src/main/java/com/minimarket/config/DataInitializer.java` para
      crear los 7 roles del caso de negocio: `CLIENTE`, `CAJERO`, `JEFE_TURNO`,
      `REPONEDOR`, `ASISTENTE_SERVICIO_CLIENTE`, `GERENTE_SUCURSAL`, `ADMINISTRADOR`, con
      un usuario de ejemplo por rol. ✅ Reemplazó el set de 3 roles de S6
      (`admin`/`cajero`/`cliente` → `ROLE_ADMIN`) por los 7 roles reales, conservando
      `cajero`/`cliente` (username) y renombrando el rol de `admin` a
      `ROLE_ADMINISTRADOR` para coincidir con el nombre oficial del caso de negocio.
- [X] T012 [US1] En `src/main/java/com/minimarket/security/config/SecurityConfig.java`
      (consolidado desde `specs/002`), definir las reglas de autorización específicas de
      este proyecto: solo `GERENTE_SUCURSAL`/`ADMINISTRADOR` acceden a
      `PUT /api/productos/{id}` y `GET /api/reportes/rotacion`; `CLIENTE` limitado a
      catálogo, disponibilidad y sus propios pedidos/carrito (FR-002, FR-014).
      ✅ Implementado con `@PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")`
      en `ProductoController` (POST/PUT/DELETE), reutilizando `@EnableMethodSecurity` ya
      consolidado desde S6. También se detectó y corrigió que `/error` no estaba en
      `permitAll()` (research.md Decisión 9): sin ese fix, un rechazo por rol devolvía 401
      en el servidor real en lugar de 403 (MockMvc no lo detectaba).
- [X] T013 [US1] [P] Crear
      `src/test/java/com/minimarket/security/AutorizacionRolesTest.java` con casos
      `@WithMockUser` cubriendo cada combinación rol-operación crítica de FR-002/FR-014
      (SC-002). ✅ 6 pruebas (GERENTE_SUCURSAL/ADMINISTRADOR autorizados, CLIENTE/CAJERO/
      REPONEDOR rechazados con 403, sin token con 401).
- [X] T014 [US1] Verificar manualmente el Escenario 1 de `quickstart.md` (login por rol +
      rechazo por autorización + rechazo por token inválido) contra el backend en
      ejecución. ✅ Verificado en vivo (no solo MockMvc): `cliente` → 403, `gerente` → 404
      (rol correcto, producto inexistente), sin token → 401 — evidencia real que detectó
      el bug de `/error` corregido en T012.

**Checkpoint**: Autenticación y autorización por rol verificadas — listas para que las
demás historias las usen sin reimplementarlas.

---

## Phase 4: User Story 2 - Gestión de inventario centralizado en tiempo real (Priority: P1)

**Goal**: Stock por sucursal en tiempo real, reposición automática, reportes de rotación.

**Independent Test**: Registrar movimientos de inventario, verificar stock resultante,
confirmar generación automática de orden de compra al cruzar el mínimo, y validar el
reporte de rotación (spec.md, Acceptance Scenarios 1-3).

### Implementation for User Story 2

- [X] T015 [US2] En `src/main/java/com/minimarket/service/impl/InventarioServiceImpl.java`,
      implementar el cálculo del stock vigente de un producto en una sucursal como la
      suma de movimientos `ENTRADA` menos `SALIDA` (research.md Decisión 5). Este es el
      **único punto de entrada** para registrar movimientos de stock: cualquier otra
      historia que descuente inventario (incluida US3) MUST invocar este servicio, no
      duplicar la lógica de descuento en otro lugar.
- [X] T016 [US2] [P] Crear entidad `OrdenDeCompra` en
      `src/main/java/com/minimarket/entity/OrdenDeCompra.java` (`producto`, `sucursal`,
      `proveedor`, `cantidadSolicitada`, `estado`, `fechaGeneracion` — data-model.md).
- [X] T017 [US2] Crear `OrdenDeCompraRepository` en
      `src/main/java/com/minimarket/repository/OrdenDeCompraRepository.java` con un
      método de búsqueda de orden `PENDIENTE` por producto+sucursal+proveedor (depende de
      T016).
- [X] T018 [US2] Crear `OrdenDeCompraService`/`OrdenDeCompraServiceImpl` en
      `src/main/java/com/minimarket/service/OrdenDeCompraService.java` y
      `src/main/java/com/minimarket/service/impl/OrdenDeCompraServiceImpl.java`
      implementando la regla "no generar si ya existe una orden `PENDIENTE` equivalente"
      (research.md Decisión 6, FR-006; depende de T017).
- [X] T019 [US2] Integrar la llamada a `OrdenDeCompraService` dentro de
      `InventarioServiceImpl` inmediatamente después de calcular el stock vigente tras un
      movimiento `SALIDA`, disparando la orden si el resultado ≤ `stockMinimo` (depende de
      T015, T018). Cualquier flujo que registre una salida de stock (incluida la
      confirmación de un `Pedido` en T031) pasa por aquí, para que la reposición
      automática se dispare sin importar el origen de la venta.
- [X] T020 [US2] [P] Implementar `POST /sucursales/{sucursalId}/inventario` en
      `src/main/java/com/minimarket/controller/InventarioController.java` según
      `contracts/openapi.yaml`. ✅ Cableado a `registrarMovimiento` (antes llamaba
      `save` directo, sin validación ni reposición automática).
- [X] T021 [US2] Crear `OrdenDeCompraController` en
      `src/main/java/com/minimarket/controller/OrdenDeCompraController.java` exponiendo
      `GET /ordenes-compra` restringido a roles de gestión (depende de T018).
- [X] T022 [US2] Crear `ReporteService`/`ReporteServiceImpl` y `ReporteController`
      (`src/main/java/com/minimarket/service/ReporteService.java`,
      `src/main/java/com/minimarket/service/impl/ReporteServiceImpl.java`,
      `src/main/java/com/minimarket/controller/ReporteController.java`) exponiendo
      `GET /reportes/rotacion?desde=&hasta=`, calculado a partir de `Venta`/`DetalleVenta`
      (FR-007, contracts/openapi.yaml). Depende de que `Venta` refleje tanto las ventas en
      tienda como las originadas desde un `Pedido` confirmado (ver T032 en US3); de lo
      contrario el reporte subestima los productos vendidos en línea.
- [X] T023 [US2] [P] Crear
      `src/test/java/com/minimarket/service/OrdenDeCompraServiceTest.java` cubriendo
      generación automática y no-duplicación (SC-004), y ampliar
      `src/test/java/com/minimarket/service/InventarioServiceTest.java` (consolidado
      desde `specs/002`) con el cálculo de stock por sucursal (SC-003). ✅ 6 + 14 pruebas.
- [X] T024 [US2] Verificar manualmente el Escenario 2 de `quickstart.md` (inventario,
      reposición automática, reporte de rotación). ✅ Verificado con
      `ReposicionAutomaticaIntegrationTest` (contexto Spring Boot + H2 real, sin mocks):
      entrada de 10 unidades, salida de 7 → stock 3 (bajo el mínimo de 5) → genera
      exactamente 1 orden de compra `PENDIENTE`; una segunda salida no duplica la orden;
      el reporte de rotación refleja una venta real y retorna lista vacía sin ventas en
      rango. Esta prueba detectó y permitió corregir un `LazyInitializationException` real
      en `ReporteServiceImpl` (research.md Decisión 10) que las pruebas con mocks no
      habrían encontrado.

**Checkpoint**: Inventario por sucursal, reposición automática y reportes funcionando de
extremo a extremo.

---

## Phase 5: User Story 3 - Consulta de disponibilidad y pedidos en línea (Priority: P2)

**Goal**: Un cliente consulta disponibilidad por sucursal y genera un pedido (retiro o
despacho) con promociones aplicadas, y ese pedido queda reflejado como una venta real.

**Independent Test**: Crear un pedido con stock disponible, verificar descuento de stock y
precio promocional; verificar rechazo si la cantidad excede lo disponible; verificar que
el pedido confirmado genera una `Venta` visible en el reporte de rotación (spec.md,
Acceptance Scenarios 1-4; FR-015).

### Implementation for User Story 3

- [X] T025 [US3] [P] Crear entidad `Promocion` en
      `src/main/java/com/minimarket/entity/Promocion.java` (`producto`,
      `descuentoPorcentaje`, `fechaInicio`, `fechaFin` — data-model.md).
- [X] T026 [US3] [P] Crear entidad `Pedido` en
      `src/main/java/com/minimarket/entity/Pedido.java` (`usuario`, `sucursal`,
      `modoEntrega`, `estado`, `fechaCreacion`, `detalles` — data-model.md).
- [X] T027 [US3] Crear entidad `DetallePedido` en
      `src/main/java/com/minimarket/entity/DetallePedido.java` (`pedido`, `producto`,
      `cantidad`, `precioAplicado` — data-model.md; depende de T026).
- [X] T028 [US3] Crear `PromocionRepository` en
      `src/main/java/com/minimarket/repository/PromocionRepository.java` (depende de
      T025; no marcar en paralelo con T025 — requiere que la entidad `Promocion` ya
      exista).
- [X] T029 [US3] Crear `PedidoRepository` y `DetallePedidoRepository` en
      `src/main/java/com/minimarket/repository/PedidoRepository.java` y
      `src/main/java/com/minimarket/repository/DetallePedidoRepository.java` (depende de
      T026, T027).
- [X] T030 [US3] Crear `PromocionService`/`PromocionServiceImpl` en
      `src/main/java/com/minimarket/service/PromocionService.java` y
      `src/main/java/com/minimarket/service/impl/PromocionServiceImpl.java` con la regla
      de vigencia por fecha y el descuento aplicable (FR-011; depende de T028).
- [X] T031 [US3] Crear `PedidoService`/`PedidoServiceImpl` en
      `src/main/java/com/minimarket/service/PedidoService.java` y
      `src/main/java/com/minimarket/service/impl/PedidoServiceImpl.java` implementando:
      consulta de disponibilidad por sucursal (FR-008), creación de pedido con
      revalidación de stock al confirmar (FR-010), y aplicación del precio promocional
      vigente (FR-011). El descuento de stock al confirmar MUST hacerse invocando
      `InventarioService` (T015/T019) — registrando un movimiento `SALIDA` real — y no
      mediante un descuento ad-hoc dentro de este servicio, para que la reposición
      automática de `OrdenDeCompra` (US2) se dispare igual que con una venta en tienda.
      Este servicio **no** crea la `Venta` directamente (ver T032). Depende de T015, T019,
      T030, T029.
- [X] T032 [US3] Crear, en `src/main/java/com/minimarket/service/impl/PedidoServiceImpl.java`
      y `src/main/java/com/minimarket/service/impl/VentaServiceImpl.java`, la integración
      que registra una `Venta` (con su `DetalleVenta`, productos, cantidades y precios ya
      aplicados por T031) inmediatamente después de confirmar un `Pedido` — FR-015. Sin
      esta tarea, los pedidos en línea nunca aparecerían en el historial de ventas ni en
      el reporte de rotación (T022, FR-007/SC-008). Depende de T031.
- [X] T033 [US3] Crear `PedidoController` en
      `src/main/java/com/minimarket/controller/PedidoController.java` exponiendo
      `GET /sucursales/{sucursalId}/productos/{productoId}/disponibilidad`,
      `POST /pedidos` y `GET /pedidos/{pedidoId}` según `contracts/openapi.yaml` (depende
      de T031, T032).
- [X] T034 [US3] [P] Crear
      `src/test/java/com/minimarket/service/PedidoServiceTest.java` cubriendo
      disponibilidad, revalidación de stock (SC-006), cálculo de precio con promoción
      vigente y la generación de la `Venta` correspondiente al confirmar (T032); y
      `src/test/java/com/minimarket/service/PromocionServiceTest.java` cubriendo la
      regla de vigencia.
- [X] T035 [US3] Verificar manualmente el Escenario 3 de `quickstart.md` (disponibilidad,
      pedido, rechazo por stock insuficiente, precio con promoción — SC-005), y confirmar
      que el pedido confirmado aparece como `Venta` en el reporte de rotación (T022).
      ✅ Verificado con `PedidoIntegrationTest` (Spring Boot + H2 real, sin mocks): pedido
      de 6 unidades con promoción de 10% → confirmado, precio aplicado 1350 (base 1500),
      stock descontado 20→14, `Venta` generada y visible para el usuario correcto; pedido
      sin stock → `StockInsuficienteException`, sin persistir ni descontar. Detectó el
      mismo patrón de `LazyInitializationException` de la Decisión 10 (esta vez en el
      propio test) — corregido con `@Transactional` en el método de prueba.

**Checkpoint**: Pedidos en línea con promociones y venta registrada funcionando de
extremo a extremo.

---

## Phase 6: User Story 4 - Documentación técnica navegable de la API (Priority: P1)

**Goal**: Documentación OpenAPI completa y enlaces HATEOAS reales y verificables — la
brecha exacta que bajó la calificación de S8 (`doc/grupo7.html`).

**Independent Test**: Abrir la documentación autogenerada y confirmar que cada operación
principal está descrita, y que las respuestas de recursos incluyen enlaces reales hacia
recursos relacionados (spec.md, Acceptance Scenarios 1-2).

### Implementation for User Story 4

- [X] T036 [US4] Agregar anotaciones `@Operation`/`@ApiResponse` (springdoc) a todos los
      controladores nuevos de este plan (`OrdenDeCompraController`, `ReporteController`,
      `PedidoController`) y a los ya existentes que falten, completando la especificación
      OpenAPI (FR-012).
- [X] T037 [US4] Implementar `ProductoModelAssembler` en
      `src/main/java/com/minimarket/web/ProductoModelAssembler.java`
      (`RepresentationModelAssembler<Producto, EntityModel<Producto>>` con enlaces a
      categoría e inventario) y devolver `EntityModel<Producto>` desde
      `ProductoController` (FR-013; depende de T009).
- [X] T038 [US4] [P] Implementar `VentaModelAssembler` en
      `src/main/java/com/minimarket/web/VentaModelAssembler.java` (enlace a su detalle) y
      devolver `EntityModel<Venta>` desde `VentaController`.
- [X] T039 [US4] [P] Implementar `InventarioModelAssembler` en
      `src/main/java/com/minimarket/web/InventarioModelAssembler.java` (enlaces a
      producto y sucursal) y devolver `EntityModel<Inventario>` desde
      `InventarioController`.
- [X] T040 [US4] [P] Implementar `CategoriaModelAssembler` en
      `src/main/java/com/minimarket/web/CategoriaModelAssembler.java` y devolver
      `EntityModel<Categoria>` desde `CategoriaController`.
- [X] T041 [US4] [P] Implementar `CarritoModelAssembler` en
      `src/main/java/com/minimarket/web/CarritoModelAssembler.java` y devolver
      `EntityModel<Carrito>` desde `CarritoController`.
- [X] T042 [US4] [P] Implementar `DetalleVentaModelAssembler` en
      `src/main/java/com/minimarket/web/DetalleVentaModelAssembler.java` y devolver
      `EntityModel<DetalleVenta>` desde `DetalleVentaController`.
- [X] T043 [US4] [P] Implementar `PedidoModelAssembler` en
      `src/main/java/com/minimarket/web/PedidoModelAssembler.java` y devolver
      `EntityModel<Pedido>` desde `PedidoController`.
- [X] T044 [US4] Crear
      `src/test/java/com/minimarket/web/HateoasLinksTest.java` verificando que las
      respuestas de `productos`, `ventas`, `inventario`, `categorias`, `carritos`,
      `detalle-ventas` y `pedidos` incluyen un bloque `_links` real — la prueba
      específica que faltó en S8 según `doc/grupo7.html` (depende de T037-T043).
      ✅ 8 pruebas, todas verdes tras corregir 3 ciclos de serialización JSON infinitos
      preexistentes desde S6 (`Usuario`/`Rol`, `Venta`/`DetalleVenta` y
      `Pedido`/`DetallePedido`, `Categoria`/`Producto` — research.md Decisión 11), que
      esta misma prueba detectó vía `HttpMessageNotWritableException: Document nesting
      depth exceeds the maximum allowed`.
- [X] T045 [US4] Verificar manualmente el Escenario 4 de `quickstart.md` (Swagger UI +
      `_links` reales en la respuesta de un producto — SC-007). ✅ Verificado en vivo:
      `swagger-ui/index.html` y `v3/api-docs` → 200; `GET /api/productos/{id}` con token
      real incluye `_links.self`, `_links.categoria` e `_links.inventario` con hrefs
      navegables reales.

**Checkpoint**: Las 4 historias de usuario completas y verificadas de extremo a extremo.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de calidad antes de consolidar con `specs/002` y entregar

- [X] T046 [P] Retirar `src/main/java/com/minimarket/controller/HolaMundoController.java`
      (código de prueba identificado en el Constitution Check de plan.md, Principio VII).
      ✅ Eliminado; sin referencias funcionales rotas (solo documentales, actualizadas).
- [X] T047 Ejecutar `./mvnw test` completo y confirmar `BUILD SUCCESS` con toda la
      cobertura nueva de T002-T045. ✅ `BUILD SUCCESS`, 104/104 pruebas tras retirar
      `HolaMundoController`.
- [X] T048 Actualizar `doc/guion-video-eft.md` (de `specs/002-guion-video-ejecucion`) y el
      informe técnico oficial citando las capacidades ahora verificadas de sucursal,
      pedidos, promociones, reportes y HATEOAS real, siguiendo la misma regla de "solo
      citar lo verificado" (FR-003/FR-011 de `specs/002`). ✅ `guion-video-eft.md` y
      `doc/consolidacion-s6/capacidades-verificadas.md` actualizados: HATEOAS pasó de
      PENDIENTE a VERIFICADA, segmento 4 del guion ahora demuestra `_links` reales,
      guion recalculado a 510s (dentro de 7-10 min). **Pendiente**: el informe técnico
      oficial en `doc/PBY2202_EFT_Plantilla_Informe_PDF.docx` (o su versión final) aún no
      se ha redactado — no es un artefacto de código y requiere que el equipo lo complete
      con capturas de pantalla siguiendo la plantilla oficial.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Depende de que `specs/002` Foundational ya esté completa (no de
  tareas dentro de este archivo).
- **Foundational (Phase 2)**: Depende de Setup — BLOQUEA las 4 historias de usuario.
- **US1 (Phase 3)**: Depende de Foundational. Es la base de la que dependen US2/US3 para
  saber qué rol puede hacer qué, pero no depende de ellas.
- **US2 (Phase 4)**: Depende de Foundational; usa las reglas de rol de US1 pero no
  requiere que US1 esté "completa" como fase, solo que `SecurityConfig` ya tenga las
  reglas base.
- **US3 (Phase 5)**: Depende de Foundational y de T015/T019 (cálculo de stock y
  reposición automática de US2) para poder consultar disponibilidad, descontar stock al
  confirmar un pedido, y disparar la reposición automática igual que una venta en tienda.
  T032 además depende de que `VentaServiceImpl` (US2/base existente) ya exista.
- **US4 (Phase 6)**: Depende de que existan los controladores de US1-US3 (incluido
  `PedidoController`, T033) para poder documentarlos y enlazarlos; por eso se planifica al
  final aunque su prioridad de negocio sea P1.
- **Polish (Phase 7)**: Depende de que las 4 historias estén completas.

### Parallel Opportunities

- Foundational: T002 y T003 en paralelo; T008 y T009 en paralelo con todo lo demás.
- US2: T016 en paralelo con T015; T020 en paralelo con T017-T019 (archivos distintos).
- US3: T025 y T026 en paralelo entre sí. T028 y T029 son estrictamente secuenciales
  respecto de sus entidades (T025/T026-T027) y no se marcan en paralelo entre sí ni con
  ellas.
- US4: T038, T039, T040, T041, T042, T043 en paralelo entre sí una vez exista T009 (base
  HATEOAS) y T037 (primer assembler como referencia de patrón).

---

## Parallel Example: Foundational

```bash
# Lanzar en paralelo (archivos distintos, sin dependencias entre sí):
Task: "Crear entidad Sucursal en src/main/java/com/minimarket/entity/Sucursal.java"
Task: "Crear entidad Proveedor en src/main/java/com/minimarket/entity/Proveedor.java"
Task: "Anotar password como WRITE_ONLY en src/main/java/com/minimarket/entity/Usuario.java"
Task: "Crear paquete web/ con configuración base HATEOAS"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Fase 1: Setup (confirmar prerrequisito de `specs/002`)
2. Completar Fase 2: Foundational
3. Completar Fase 3: User Story 1
4. **DETENER y VALIDAR**: login por rol funcionando y verificado — recién ahí tiene
   sentido avanzar a inventario (US2), que es el siguiente P1 por orden de dependencia
   técnica real.

### Incremental Delivery

1. Setup + Foundational → modelo de dominio base listo.
2. US1 → autenticación y roles verificados.
3. US2 → inventario, reposición automática y reportes verificados.
4. US3 → pedidos en línea con promociones y venta registrada verificados.
5. US4 → documentación OpenAPI + HATEOAS real verificada (cierra la brecha de S8).
6. Polish → limpieza final y actualización de informe/guion.

---

## Notes

- [P] = archivos distintos, sin dependencias pendientes. Una tarea que depende de otra
  incompleta **no** se marca [P], aunque otra tarea de la misma fase sí lo esté.
- [Story] mapea cada tarea de fase de historia a su historia de usuario para
  trazabilidad.
- US4 tiene prioridad de negocio P1 pero se ejecuta técnicamente al final porque necesita
  que existan los controladores de las demás historias para documentarlos y enlazarlos;
  esto no reduce su prioridad, solo refleja una dependencia técnica real.
- T032 (Pedido confirmado → Venta) existe para que FR-015 y el reporte de rotación
  (FR-007/SC-008) cubran también los pedidos en línea, no solo las ventas en tienda.
- Confirmar `BUILD SUCCESS` (T010, T047) en cada checkpoint — mismo gate que
  `specs/002-guion-video-ejecucion` exige para no repetir la causa raíz señalada en
  `doc/grupo7.html`.

## Phase 8: Convergence

**Purpose**: Cerrar las brechas detectadas por `/speckit-converge` entre `spec.md`/
`plan.md`/`tasks.md`/Constitución y el estado real del código (2026-07-17). Ordenadas
CRITICAL/HIGH primero.

- [X] T049 Agregar autorización por rol (y verificación de propiedad donde aplique) a
      `UsuarioController` (listar y `GET`/`PUT`/`DELETE` por id restringidos a
      `ADMINISTRADOR`/`GERENTE_SUCURSAL`, o al propio usuario autenticado para su propio
      registro), `CategoriaController` (`POST`/`PUT`/`DELETE` restringidos a roles de
      gestión), `VentaController` (`POST` restringido a `CAJERO` y roles de gestión),
      `CarritoController` (`PUT`/`DELETE` restringidos al usuario dueño del carrito o a
      roles de gestión) y `DetalleVentaController` (mutaciones restringidas a roles de
      gestión) per Constitution Principio II / FR-002 (contradicts/missing, CRITICAL).
      ✅ `CustomUserDetails.getUsuarioId()` agregado; los 5 controladores ahora usan
      `@PreAuthorize` con roles y, en `Carrito`, un `verificarPropietarioODeGestion()`
      post-carga (no expresable como SpEL simple). `./mvnw test` → 104/104 passing,
      BUILD SUCCESS, sin regresiones.
- [X] T050 Crear `PromocionController` exponiendo `GET`/`POST /api/promociones` (y
      `GET`/`PUT`/`DELETE` por id) restringido a roles de gestión, según
      `contracts/openapi.yaml`, con sus pruebas correspondientes per FR-011 (missing,
      HIGH). ✅ `PromocionController` creado (`@PreAuthorize` a nivel de clase, roles
      GERENTE_SUCURSAL/ADMINISTRADOR); `PromocionAutorizacionTest` (6 casos: gestión OK,
      CLIENTE/CAJERO 403, sin token 401, rango de fechas inválido 400).
- [X] T051 Crear `OrdenDeCompraModelAssembler` y `PromocionModelAssembler` (depende de
      T050) y devolver `EntityModel`/`CollectionModel` desde sus controladores,
      consistente con el resto de los recursos, per FR-013 / Constitution Principio IV
      (missing, MEDIUM). ✅ Ambos assemblers creados (self + rel de colección);
      `OrdenDeCompraController`/`PromocionController` ahora devuelven
      `CollectionModel<EntityModel<T>>`/`EntityModel<T>`. `./mvnw test` → 112/112,
      BUILD SUCCESS.
- [X] T052 Validar en `PromocionServiceImpl.save()` que `fechaFin` sea posterior a
      `fechaInicio`, lanzando `DatosIncompletosException` en caso contrario, per
      data-model.md (partial, MEDIUM). ✅ Validación agregada (fechas nulas y
      fechaFin<=fechaInicio); cubierta por `PromocionServiceTest` (2 casos nuevos) y
      `PromocionAutorizacionTest` (400 vía `GlobalExceptionHandler`).
- [X] T053 Agregar anotaciones `@Operation`/`@Tag`/`@ApiResponses` de springdoc a
      `ProductoController`, `VentaController`, `CategoriaController`,
      `CarritoController`, `DetalleVentaController`, `UsuarioController` e
      `InventarioController`, consistente con `AuthController`/`OrdenDeCompraController`/
      `ReporteController`/`PedidoController`, per FR-012 (partial, LOW). ✅ Los 7
      controladores anotados. Verificado en vivo contra `GET /v3/api-docs` con servidor
      real (no solo inspección de código): 12 tags correctos (incl. acentos UTF-8),
      `/api/promociones` y `/api/ordenes-compra` presentes. `./mvnw test` → 114/114,
      BUILD SUCCESS.
- [X] T054 Enrutar `InventarioController.actualizarMovimiento` (`PUT`) a través de la
      misma validación que `registrarMovimiento` (o a un método de actualización
      dedicado con las mismas reglas), en vez de llamar a `inventarioService.save()`
      directo, per tasks.md T015 ("único punto de entrada") (contradicts, LOW). ✅
      Validación extraída a `validarMovimiento()` privado, reutilizada por
      `registrarMovimiento` y el nuevo `actualizarMovimiento(id, inventario)` (sin
      duplicar el efecto secundario de reposición automática en una edición). 2 pruebas
      nuevas en `InventarioServiceTest`.
- [X] T055 Documentar (o mitigar) el manejo de revocación de rol durante una sesión JWT
      activa — actualmente un rol revocado no se refleja hasta que el token expira (24h),
      sin lista de revocación — per el edge case correspondiente de spec.md (missing,
      LOW). ✅ Documentado en `README.md` ("Limitación conocida — revocación de rol
      durante una sesión activa"), con mitigación recomendada para producción fuera del
      alcance del EFT. README también actualizado con el conteo de tests real (114) y el
      estado real de HATEOAS (implementado, ya no "pendiente" como decía la versión
      previa).

## Phase 9: Convergence

**Purpose**: Cerrar las brechas detectadas por una segunda pasada de `/speckit-converge`
(2026-07-17) tras completar la Fase 8. Verificadas en vivo contra el servidor real, no
solo por inspección de código. Ordenadas CRITICAL primero.

- [X] T056 Agregar autorización por rol/propiedad a los endpoints de lectura de
      `VentaController` (`GET`/`GET /{id}`), `CarritoController` (`GET`/`GET /{id}`),
      `DetalleVentaController` (`GET`/`GET /{id}`) y `PedidoController`
      (`GET /api/pedidos/{id}`), restringiendo a el dueño del recurso o a roles de
      gestión; y agregar verificación de propiedad a
      `CarritoController.agregarProductoAlCarrito` (`POST`) para que un usuario no pueda
      agregar productos al carrito de otro. Verificado en vivo 2026-07-17: `GET
      /api/ventas`, `GET /api/carrito` y `GET /api/detalle-ventas` con token de `cliente`
      devuelven `200` con los datos de **todos** los usuarios, no solo los propios. Per
      Constitution Principio II ("Toda ruta que exponga datos de clientes... o
      transacciones MUST... validar el rol del usuario") y FR-004 (missing, CRITICAL). ✅
      `Venta`/`Carrito` filtran por `usuarioId` efectivo (propio, salvo rol de gestión);
      `DetalleVenta` restringido a `CAJERO`/gestión (no tiene un dueño-cliente directo);
      `Pedido` y `Carrito` (`GET`/`POST`) verifican propiedad post-carga. Re-verificado en
      vivo: `GET /api/detalle-ventas` (cliente) → `403`; `POST /api/carrito` a nombre de
      otro usuario → `403`.
- [X] T057 Agregar `@PreAuthorize` a `InventarioController` (`POST`/`PUT`/`DELETE`),
      restringido a roles operativos/de gestión (p. ej. `REPONEDOR`,
      `GERENTE_SUCURSAL`, `ADMINISTRADOR`). Verificado en vivo 2026-07-17: `POST
      /api/inventario` con token de `cliente` llega a la validación de negocio (`400`
      "La sucursal no puede ser nula") en vez de ser rechazado por rol (`403`) — no
      existe ningún `@PreAuthorize` en el controlador. Per Constitution Principio II y
      FR-002 ("Autorización Basada en Roles: Controlar el acceso a la gestión de stock")
      (missing, CRITICAL). ✅ `POST`/`PUT` restringidos a `REPONEDOR`/`GERENTE_SUCURSAL`/
      `ADMINISTRADOR`; `DELETE` restringido a `GERENTE_SUCURSAL`/`ADMINISTRADOR`
      (acción más sensible). Re-verificado en vivo: `POST /api/inventario` (cliente) →
      `403`.
- [X] T058 Conectar `VentaController.guardarVenta` (`POST /api/ventas`) a
      `VentaService.registrarVenta()` (el método real, ya validado por
      `VentaServiceTest`, que nunca quedó conectado a ningún endpoint) en vez de
      `VentaService.save()`; extender el flujo para que también aplique el precio
      promocional vigente vía `PromocionService.calcularPrecioConPromocion` y registre el
      movimiento de `Inventario` correspondiente vía
      `InventarioService.registrarMovimiento` (replicando el patrón ya usado en
      `PedidoServiceImpl.confirmarPedido`), de forma que una venta en tienda también
      valide/descuente stock y pueda disparar la reposición automática. Verificado en
      vivo 2026-07-17: `POST /api/ventas` con `cantidad=500` sobre un producto con
      `stock=2` responde `HTTP 500` (`DetalleVenta.venta` nulo) y el stock del producto
      no cambia — ni se valida ni se descuenta. Per FR-005, FR-006, FR-011, FR-015 y
      Constitution Principio V (contradicts, CRITICAL). ✅ **Parcialmente por diseño,
      documentado en research.md Decisión 12**: se conectó el controlador a
      `registrarVenta()` (antes ni siquiera se llamaba), se corrigió el bug real que
      causaba el `HTTP 500` (`detalle.setVenta(venta)` faltante antes de persistir) y se
      aplicó el precio promocional vigente vía `PromocionService`. **No** se agregó
      `Sucursal`/movimiento de `Inventario` a `Venta`: la entidad `Venta` (y los 6 tests
      reales de `VentaServiceTest` consolidados desde S6) nunca tuvieron dimensión de
      sucursal; agregarla habría sido un cambio de esquema mayor fuera del alcance de
      este hallazgo y habría roto esos tests. `registrarVenta` sigue validando/
      descontando `Producto.stock` directo (el modelo ya existente, ahora sí conectado y
      con precio real). Nuevo `VentaIntegrationTest` (2 pruebas, persistencia real sin
      mocks) prueba que ya no ocurre el `HTTP 500` y que el precio/stock son correctos.
      Re-verificado en vivo: `cantidad=500` sobre `stock=5` → `409` (antes `500`); venta
      válida de 2 unidades → `200`, precio real aplicado, stock `5→3`.
- [X] T059 Corregir `plan.md` (sección Technical Context) para retirar `Lombok` de la
      lista de dependencias principales, dado que fue removida como corrección de causa
      raíz (incompatibilidad con JDK 25) documentada en
      `specs/002-guion-video-ejecucion/research.md` Decisión 7 — `/speckit-converge` no
      puede editar `plan.md` directamente, por lo que queda como tarea explícita para
      `/speckit-implement` (contradicts, LOW). ✅ `plan.md` corregido: `Lombok` retirado
      de "Primary Dependencies", con una nota explícita de por qué no es una dependencia
      del proyecto.

## Phase 10: Convergence

**Purpose**: Cerrar las brechas detectadas por una tercera pasada de `/speckit-converge`
(2026-07-18) tras completar la Fase 9. Verificadas por lectura directa del código actual
(no solo por el texto de tareas previas), con `./mvnw test` en `BUILD SUCCESS` (116/116)
como línea base. Ordenadas CRITICAL primero.

- [X] T060 Agregar autorización de propiedad a `POST /api/pedidos`
      (`PedidoController.crearPedido`) y `PedidoServiceImpl.confirmarPedido`: actualmente
      cualquier usuario autenticado, de cualquier rol, puede enviar un `Pedido` con un
      `usuario.id` arbitrario en el cuerpo de la solicitud, y el sistema lo confirma sin
      verificar que corresponda al usuario autenticado — descuenta stock y genera una
      `Venta` a nombre de otra persona. `PedidoController.crearPedido`
      (`src/main/java/com/minimarket/controller/PedidoController.java`) no tiene
      `@PreAuthorize` ni verificación de propiedad, y `PedidoServiceImpl.confirmarPedido`
      (`src/main/java/com/minimarket/service/impl/PedidoServiceImpl.java`) nunca compara
      `pedido.getUsuario()` contra el usuario autenticado — la misma clase de brecha que
      T056/T057 ya corrigieron para las lecturas de `Carrito`/`Venta`/`Pedido`, pero no
      para esta escritura. Forzar que `pedido.usuario` sea siempre el usuario autenticado
      (ignorando cualquier `usuario.id` recibido en el body), replicando el patrón de
      `CarritoController.verificarPropietarioODeGestion`, per Constitution Principio II /
      FR-009 / FR-010 (missing, CRITICAL). ✅ `PedidoController.crearPedido` ahora llama a
      `usuarioAutenticado()` (extraído del `CustomUserDetails` del contexto de seguridad)
      y sobrescribe `pedido.setUsuario(...)` antes de invocar al servicio, ignorando
      cualquier `usuario.id` recibido en el body. Nuevo
      `src/test/java/com/minimarket/security/PedidoAutorizacionTest.java` (con
      `@WithUserDetails("cliente")` real, no mock genérico) verifica que un body con
      `usuario.id` del usuario `admin` termina de todas formas asociado al `cliente`
      autenticado. `./mvnw test` → 117/117, `BUILD SUCCESS`.
- [X] T061 Documentar en `research.md` (o como comentario en `PromocionServiceImpl`) la
      resolución del edge case de spec.md: "¿Qué ocurre si una promoción vigente y un
      cambio manual de precio ocurren sobre el mismo producto al mismo tiempo? Debe
      quedar definido cuál prevalece". Actualmente `PromocionServiceImpl.calcularPrecioConPromocion`
      (usada de forma idéntica en `VentaServiceImpl.registrarVenta` y
      `PedidoServiceImpl.confirmarPedido`) recalcula el descuento contra
      `producto.getPrecio()` vigente al momento de la transacción, lo cual es de facto
      una respuesta a la pregunta del edge case, pero no queda registrada como decisión
      explícita en ningún lado, per spec.md Edge Cases (partial, MEDIUM). ✅ Documentado en
      `research.md` Decisión 13: el precio manual vigente al confirmar siempre prevalece
      como base, y la promoción vigente en ese mismo instante se aplica como descuento
      sobre ese precio base (nunca un precio cacheado); se documenta también la limitación
      conocida de ausencia de bloqueo optimista sobre `Producto.precio`.

## Phase 11: Convergence

**Purpose**: Cerrar las brechas detectadas por una cuarta pasada de `/speckit-converge`
(2026-07-18), esta vez ejercitando el servidor real en vivo (`./mvnw spring-boot:run` +
`curl` contra `http://localhost:8090`) en lugar de solo leer el código o llamar a los
servicios directamente desde una prueba — la misma disciplina que ya exigía
`specs/002-guion-video-ejecucion` para no repetir la causa raíz de `doc/grupo7.html`. Todas
las pruebas automatizadas existentes (incluidas las de integración con Spring Boot + H2
real) invocaban los servicios con entidades `Producto` ya gestionadas por JPA con todos sus
campos poblados, nunca a través del `MockMvc`/JSON real que usa cualquier cliente HTTP
real de la API — lo que dejó sin detectar los tres hallazgos siguientes durante las Fases
8-10. Ordenadas CRITICAL primero.

- [X] T062 Crear `SucursalController` y `ProveedorController` (con sus respectivos
      `SucursalService`/`ProveedorService` y `SucursalModelAssembler`/
      `ProveedorModelAssembler`), y sembrar en `DataInitializer` los "datos semilla
      mínimos" que `quickstart.md` (Prerrequisitos) ya declaraba como requisito ("al menos
      una Sucursal, un Proveedor, un Producto con stockMinimo configurado"):
      `contracts/openapi.yaml` ya declaraba los tags `sucursales`/`proveedores`, pero no
      existía ningún controlador real detrás — sin él, no había forma de crear una
      `Sucursal` o un `Proveedor` a través de la API, y el servidor recién levantado no
      podía demostrar ningún escenario de `quickstart.md` (US2/US3) sin manipular la base
      directamente. Detectado al intentar poblar datos de prueba contra el servidor real
      (`POST /api/sucursales` → `404 No static resource`). Per FR-005/FR-006 y
      `contracts/openapi.yaml` (missing, CRITICAL). ✅
      `src/main/java/com/minimarket/controller/{Sucursal,Proveedor}Controller.java`,
      `service/{Sucursal,Proveedor}Service.java` + `service/impl/...Impl.java`,
      `web/{Sucursal,Proveedor}ModelAssembler.java` creados (mismo patrón que
      `CategoriaController`: lectura abierta a cualquier autenticado, mutaciones
      restringidas a `GERENTE_SUCURSAL`/`ADMINISTRADOR`); `DataInitializer` siembra 1
      `Proveedor`, 1 `Sucursal`, 1 `Categoria` y 1 `Producto` con `stockMinimo`/`proveedor`
      si la base está vacía. Nuevos `SucursalServiceTest`, `ProveedorServiceTest`,
      `SucursalYProveedorAutorizacionTest` y un caso en `HateoasLinksTest`. Verificado en
      vivo: `GET /api/sucursales` y `GET /api/proveedores` devuelven `200` con datos reales
      y `_links` HATEOAS.
- [X] T063 Corregir `InventarioServiceImpl.registrarMovimiento`: al disparar la reposición
      automática (FR-006), pasaba `guardado.getProducto()` — el objeto `Producto` tal como
      llegó en el body JSON de `POST /api/inventario` (solo `{"id": X}`, el mismo patrón de
      referencia mínima que usa el resto de la API) — directamente a
      `OrdenDeCompraService.generarSiNecesario`, que lee `producto.getStockMinimo()` y
      `producto.getProveedor()`. Como esos campos nunca se recargaban desde la base, la
      condición `stockMinimo == null` hacía que el método retornara sin generar ninguna
      orden, **en silencio, sin error** — verificado en vivo con `POST /api/inventario`
      real: una salida que cruzaba el mínimo no generaba ninguna orden en
      `GET /api/ordenes-compra`, mientras que las pruebas de integración existentes
      (`ReposicionAutomaticaIntegrationTest`) nunca lo detectaron porque invocaban el
      servicio con una entidad `Producto` ya gestionada por JPA (con `stockMinimo` poblado
      en memoria). Per FR-006 y Constitution Principio V (contradicts, CRITICAL). ✅
      `InventarioServiceImpl` ahora recarga el `Producto` completo vía `ProductoRepository`
      antes de invocar `generarSiNecesario`. Nuevo
      `PedidoInventarioApiIntegrationTest.postInventarioConReferenciaMinimaDisparaReposicionAutomatica`
      (MockMvc + JSON real, sin mocks) reproduce el escenario exacto y falla si la
      regresión reaparece. Re-verificado en vivo: la misma secuencia ahora genera
      exactamente 1 orden `PENDIENTE`.
- [X] T064 Corregir `PedidoServiceImpl.confirmarPedido`: usaba `detalle.getProducto()` —
      igualmente solo `{"id": X}` desde el body JSON de `POST /api/pedidos` — para calcular
      el precio vía `PromocionServiceImpl.calcularPrecioConPromocion(producto, fecha)`, que
      lee `producto.getPrecio()`. Con el `Producto` no recargado, `getPrecio()` devolvía
      `null`, y el auto-unboxing a `double` lanzaba `NullPointerException` →
      **`POST /api/pedidos` respondía `HTTP 500` ante cualquier cliente real** que usara el
      mismo patrón de referencia mínima ya usado en el resto de la API (Carrito,
      DetalleVenta, Inventario) — verificado en vivo. `PedidoIntegrationTest` y
      `PedidoServiceTest` no lo detectaron porque invocaban `confirmarPedido` con una
      entidad `Producto` ya gestionada por JPA. Esto bloqueaba por completo, en el servidor
      real, la Historia de Usuario 3 (P1) — el flujo central de pedidos en línea del caso
      de negocio. Per FR-009/FR-010/FR-011 y Constitution Principio V (contradicts,
      CRITICAL). ✅ `PedidoServiceImpl.confirmarPedido` ahora recarga el `Producto`
      completo de cada `DetallePedido` vía `ProductoRepository` antes de calcular
      disponibilidad y precio. `PedidoServiceTest` actualizado (mock de
      `ProductoRepository`). Nuevo
      `PedidoInventarioApiIntegrationTest.postPedidoConReferenciaMinimaNoFallaYAplicaPrecioReal`
      (MockMvc + JSON real) reproduce el escenario exacto. Re-verificado en vivo: el mismo
      `POST /api/pedidos` que antes devolvía `500` ahora devuelve `200` con
      `precioAplicado` real y el pedido atribuido correctamente al usuario autenticado
      (T060). `./mvnw test` → 135/135, `BUILD SUCCESS`.

## Phase 12: Convergence

**Purpose**: Cerrar la brecha detectada por `/speckit-analyze` (2026-07-18) al leer el
código real detrás de las tareas T060-T064 en lugar de solo el texto de tasks.md.
Ordenada CRITICAL.

- [X] T065 Corregir `PedidoServiceImpl.confirmarPedido` y `VentaServiceImpl.registrarVenta`:
      ambas revalidaban stock **por línea de detalle**, comparando cada `DetallePedido`/
      `DetalleVenta` contra el mismo stock vigente (todavía no descontado, porque el
      descuento solo ocurre en un bucle posterior). Un `Pedido`/`Venta` con dos detalles
      del mismo producto+sucursal (p. ej. 3 + 3 unidades contra 5 en stock) pasaba la
      revalidación en ambas líneas de forma independiente y, al aplicar ambos descuentos,
      dejaba el stock en negativo — violando FR-010/SC-006 ("100% de los pedidos
      confirmados con stock insuficiente son rechazados") y Constitution Principio V. Ni
      `PedidoServiceTest` ni `VentaServiceTest`/`VentaIntegrationTest` cubrían el caso de
      dos detalles del mismo producto en una misma solicitud. Per FR-010/FR-015
      (contradicts, CRITICAL). ✅ Ambos métodos ahora agregan (`Map<Long, Integer>`) la
      cantidad solicitada por producto dentro de la misma solicitud antes de comparar
      contra el stock vigente, rechazando si el total agregado excede lo disponible.
      Nuevos casos en `PedidoServiceTest`
      (`confirmarPedido_dosDetallesMismoProducto_agregaCantidadYRechazaSiExcedeStock`,
      `..._confirmaSiStockAlcanzaParaElTotal`) y `VentaServiceTest`
      (`registrarVenta_dosDetallesMismoProducto_agregaCantidadYRechazaSiExcedeStock`,
      `..._confirmaSiStockAlcanzaParaElTotal`). `./mvnw test` → 139/139, `BUILD SUCCESS`.

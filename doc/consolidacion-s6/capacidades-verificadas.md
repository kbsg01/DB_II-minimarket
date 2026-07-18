# Capacidades verificadas — Consolidación S6 → feat/eft-s9

**Fecha**: 2026-07-17
**Propósito**: Registrar, por ejecución real (no por lectura de README/informes), qué
capacidades del backend consolidado están efectivamente operativas. Es la única fuente
autorizada para decidir qué puede citarse en `doc/guion-video-eft.md` y en el informe
oficial (FR-001, FR-003, FR-011 de `spec.md`). Instancia concreta de la tabla
`CapacidadVerificada` definida en `data-model.md`.

## Tabla de capacidades

| Capacidad | Rama origen | Estado | Evidencia |
|---|---|---|---|
| Build y suite de pruebas | `feat/microservices-junit-s6` (consolidado) + Convergencia (T049-T055) | **VERIFICADA** | `./mvnw test` → `BUILD SUCCESS`, `Tests run: 114, Failures: 0, Errors: 0, Skipped: 0` (T015, actualizado tras Fase 8: Convergencia de `specs/001`). |
| Autenticación JWT (login) | `feat/microservices-junit-s6` | **VERIFICADA** | `POST /api/auth/login` con `admin`/`admin123` → `200` con `{token, username, roles:["ROLE_ADMINISTRADOR"]}` real (JJWT HS256). |
| Rechazo de credenciales inválidas | `feat/microservices-junit-s6` | **VERIFICADA** | `POST /api/auth/login` con contraseña incorrecta → `401`. |
| Rechazo sin token / autorización por rol | `feat/microservices-junit-s6` | **VERIFICADA** | `GET /api/productos` sin header `Authorization` → `401`; con `Bearer <token>` válido → `200`. |
| Documentación OpenAPI (Swagger UI) | `feat/microservices-junit-s6` | **VERIFICADA** | `GET /swagger-ui/index.html` → `200`; `GET /v3/api-docs` → `200`. |
| Datos de demostración (`DataInitializer`) | `feat/microservices-junit-s6` | **VERIFICADA** | Roles `ROLE_ADMIN`/`ROLE_CAJERO`/`ROLE_CLIENTE` y usuarios `admin`/`cajero`/`cliente` creados al arrancar (logs de Hibernate `insert into usuario...` observados en el arranque). |
| HATEOAS (enlaces `_links`, 7 recursos) | `specs/001-minimarket-backend-spec` (implementación real nueva) | **VERIFICADA** | `GET /api/productos/{id}` con token real → `_links.self`, `_links.categoria`, `_links.inventario` con hrefs navegables reales. `HateoasLinksTest` (8 pruebas) cubre productos, ventas, inventario, categorías, carritos, detalle-ventas y pedidos. Esto es exactamente lo que S8 declaró sin código real (`doc/grupo7.html`) — ahora es código real y probado. |
| Inventario por sucursal + reposición automática | `specs/001-minimarket-backend-spec` | **VERIFICADA** | `ReposicionAutomaticaIntegrationTest`: salida que cruza el stock mínimo genera exactamente 1 `OrdenDeCompra PENDIENTE`; una segunda salida no duplica. |
| Reporte de rotación de productos | `specs/001-minimarket-backend-spec` | **VERIFICADA** | `GET /api/reportes/rotacion?desde=&hasta=` refleja ventas reales en el rango; rango sin ventas → lista vacía explícita. |
| Pedidos en línea con promociones y venta generada | `specs/001-minimarket-backend-spec` | **VERIFICADA** | `PedidoIntegrationTest`: pedido con promoción de 10% → precio aplicado correcto, stock descontado, `Venta` generada; pedido sin stock → rechazado (409, `StockInsuficienteException`). |
| Autorización por rol específica del negocio (7 roles) | `specs/001-minimarket-backend-spec` | **VERIFICADA** | `AutorizacionRolesTest` + verificación en vivo: solo `GERENTE_SUCURSAL`/`ADMINISTRADOR` pueden `PUT /api/productos/{id}`; el resto recibe `403` real (no `401` — bug de `/error` corregido, research.md Decisión 9). |
| Datos de catálogo (productos/categorías) precargados | — | **PENDIENTE** | `DataInitializer` solo siembra roles/usuarios (por diseño de S6); productos/categorías/sucursales se crean vía API o pruebas de integración, no al arrancar. No es un defecto: no lo exige ningún FR de `specs/001`. |
| Autorización por rol/ownership en Usuario/Categoria/Venta/Carrito/DetalleVenta | `specs/001-minimarket-backend-spec` (Fase 8: Convergencia, T049) | **VERIFICADA** | Hallazgo CRITICAL de `/speckit-converge`: estos 5 controladores no tenían ninguna restricción de rol (cualquier `CLIENTE` autenticado podía listar/editar/borrar usuarios ajenos). Corregido con `@PreAuthorize` por rol y, en `Carrito`, verificación de propiedad post-carga. |
| `PromocionController` (CRUD completo) | `specs/001-minimarket-backend-spec` (T050) | **VERIFICADA** | No existía (hallazgo HIGH de `/speckit-converge`: `contracts/openapi.yaml` definía `/promociones` pero nunca se implementó). `PromocionAutorizacionTest` (6 casos) + validación de rango de fechas (T052). |
| HATEOAS en OrdenDeCompra y Promocion | `specs/001-minimarket-backend-spec` (T051) | **VERIFICADA** | `OrdenDeCompraModelAssembler`/`PromocionModelAssembler` agregados; ambos controladores devuelven `EntityModel`/`CollectionModel`, completando los 9 recursos de negocio con HATEOAS real. |
| Documentación OpenAPI en todos los controladores de negocio | `specs/001-minimarket-backend-spec` (T053) | **VERIFICADA** | Los 7 controladores que carecían de `@Operation`/`@Tag`/`@ApiResponses` (Producto, Venta, Categoria, Carrito, DetalleVenta, Usuario, Inventario) fueron anotados; verificado en vivo contra `GET /v3/api-docs` (12 tags, incl. acentos UTF-8 correctos), no solo por inspección de código. |

## Discrepancias adicionales detectadas al contrastar README de S7/S8 contra el código (T020)

- El README de `feat/hateoas-s8` afirma autenticación **HTTP Basic** con credenciales
  `admin`/`admin123`, `cajero1`/`cajero123`, `cliente1`/`cliente123`. La implementación
  real consolidada (de S6) usa **JWT**, no HTTP Basic, y los usuarios demo son
  `admin`/`cajero`/`cliente` (sin sufijo `1`). Esto es consistente con FR-001 de
  `specs/001` (JWT exigido explícitamente por el caso de negocio) — la descripción de S8
  no se sigue ni se cita.
- El README de S8 afirma "Swagger UI, `/v3/api-docs` y `/public/**` son de acceso
  público". Verificado: `swagger-ui`, `v3/api-docs` y `/api/auth/**` están efectivamente
  permitidos sin autenticación en el `SecurityConfig` consolidado (coincide en este punto
  puntual, aunque el mecanismo de autenticación general descrito alrededor no coincide).
- El README de S8 declara un bloque `_links` en 7 recursos de negocio y filtros de
  navegación por query param (`?categoriaId=`, `?productoId=`, etc.). Ninguno de los dos
  está implementado en el código consolidado (ni en S8 mismo, según el hallazgo
  independiente de `doc/grupo7.html`). Se mantiene como **PENDIENTE**, igual que HATEOAS.
- No se encontraron otras afirmaciones verificables además de las ya cubiertas por
  `doc/grupo7.html` (springdoc/hateoas en `pom.xml`, `EntityModel`/`CollectionModel`,
  cantidad de archivos de test, resultado de `./mvnw test`).

## Actualización 2026-07-17: HATEOAS pasó de PENDIENTE a VERIFICADA

Al completar `specs/001-minimarket-backend-spec` (US4), HATEOAS dejó de ser la brecha que
`doc/grupo7.html` señaló en S8: ahora existe código real (`RepresentationModelAssembler`,
`EntityModel`, `CollectionModel`, `linkTo(methodOn(...))`) en los 7 recursos de negocio,
verificado tanto por `HateoasLinksTest` como en vivo contra el servidor real. Implementar
esto también destapó y corrigió 3 bugs reales de serialización JSON circular
preexistentes desde S6 (`Usuario`/`Rol`, `Venta`/`DetalleVenta` y `Pedido`/`DetallePedido`,
`Categoria`/`Producto` — ver `specs/001-minimarket-backend-spec/research.md` Decisión 11),
que nunca se habían detectado porque ningún endpoint anterior serializaba esas relaciones
bidireccionales juntas con datos reales.

## Regla de uso

Ninguna fila con `estado = PENDIENTE` puede citarse en `doc/guion-video-eft.md` como si
ya estuviera lograda (FR-003, FR-011). La única fila pendiente restante (catálogo
precargado) no es un defecto: es una decisión de diseño de `DataInitializer` (sembrar
solo roles/usuarios), no un requisito incumplido.

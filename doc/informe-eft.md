# Desarrollo Backend II (PBY2202)
## Evaluación Final Transversal (EFT) — Semana 9

**Proyecto**: Backend "MiniMarket Plus"
**Repositorio / rama de entrega única**: `feat/eft-s9` (todas las capacidades de S1-S8 quedan
integradas en esta rama; ver `specs/002-guion-video-ejecucion/research.md` §1-2 para el
detalle de la consolidación)
**Fecha de este borrador**: 2026-07-17
**Integrantes del equipo**: *(pendiente — no se han provisto los nombres reales en esta
sesión; ver nota al final)*
**Jefe de proyecto**: *(pendiente)*

> ⚠️ **Nota de honestidad metodológica**: este informe se redacta únicamente a partir de
> capacidades verificadas por ejecución real (`doc/consolidacion-s6/capacidades-verificadas.md`),
> no por lectura de informes o README de entregas anteriores. Esta disciplina responde
> directamente al hallazgo documentado en `doc/grupo7.html`: un grupo previo declaró en su
> informe HATEOAS, OpenAPI y pruebas que el código entregado no sustentaba, lo que derivó
> en una calificación de "Medianamente Logrado". Cada afirmación de este documento tiene
> evidencia de ejecución citada junto a ella.

---

## 1. Descripción general

"MiniMarket Plus" es una cadena de minimarkets con 10 sucursales en la Región
Metropolitana. Este proyecto implementa el backend que centraliza su inventario, sus
ventas y pedidos en línea, y la seguridad de sus usuarios (clientes y 7 roles internos:
Administrador, Gerente de Sucursal, Jefe de Turno, Cajero, Reponedor, Asistente de
Servicio al Cliente y Cliente), conforme al caso de negocio de las instrucciones
específicas de esta EFT.

El desarrollo se organizó con Spec-Kit (`speckit.specify` → `speckit.plan` →
`speckit.tasks` → `speckit.implement` → `speckit.converge`) sobre dos especificaciones:

| Especificación | Alcance |
|---|---|
| `specs/001-minimarket-backend-spec` | Backend completo: autenticación JWT, autorización por rol, inventario multi-sucursal con reposición automática, pedidos en línea con promociones, reportes de rotación, documentación OpenAPI + HATEOAS. |
| `specs/002-guion-video-ejecucion` | Recuperación/consolidación de la implementación real de semanas anteriores (S1-S8) y guion del video de presentación (`doc/guion-video-eft.md`). |

---

## 2. Configuración de los frameworks de seguridad

**Stack**: Spring Security 6 + JWT (JJWT 0.11.5, algoritmo HS256), sesiones *stateless*,
`@EnableMethodSecurity` con autorización método a método vía `@PreAuthorize`.

### 2.1 Componentes implementados

| Componente | Archivo | Responsabilidad |
|---|---|---|
| `SecurityConfig` | `security/config/SecurityConfig.java` | Cadena de filtros: CSRF deshabilitado (API *stateless*), `SessionCreationPolicy.STATELESS`, rutas públicas (`/public/**`, `/api/auth/**`, Swagger UI, `/v3/api-docs/**`, `/error`), `JwtAuthenticationFilter` antepuesto a `UsernamePasswordAuthenticationFilter`, `BCryptPasswordEncoder`. |
| `JwtUtil` | `security/util/JwtUtil.java` | Emisión y validación de tokens JWT HS256, expiración de 24 h. |
| `JwtAuthenticationFilter` | `security/filter/JwtAuthenticationFilter.java` | `OncePerRequestFilter` que extrae el header `Authorization: Bearer <token>`, valida el JWT y puebla el `SecurityContextHolder`. |
| `JwtAuthenticationEntryPoint` | `security/handler/JwtAuthenticationEntryPoint.java` | Responde `401` con cuerpo JSON, sin exponer detalles internos (FR-003). |
| `CustomUserDetailsService` / `CustomUserDetails` | `security/service/`, `security/model/` | Carga el `Usuario` + sus `Rol` desde la base y expone `getUsuarioId()` para verificaciones de propiedad en SpEL (`#id == authentication.principal.usuarioId`). |
| `DataInitializer` | `config/DataInitializer.java` | Crea los 7 roles del caso de negocio y un usuario de demostración por rol al arrancar (ver tabla siguiente). |

### 2.2 Usuarios de demostración (roles del caso de negocio)

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMINISTRADOR` |
| `gerente` | `gerente123` | `ROLE_GERENTE_SUCURSAL` |
| `jefeturno` | `jefeturno123` | `ROLE_JEFE_TURNO` |
| `cajero` | `cajero123` | `ROLE_CAJERO` |
| `reponedor` | `reponedor123` | `ROLE_REPONEDOR` |
| `asistente` | `asistente123` | `ROLE_ASISTENTE_SERVICIO_CLIENTE` |
| `cliente` | `cliente123` | `ROLE_CLIENTE` |

### 2.3 Autorización por rol y por propiedad del recurso

Cada operación de mutación queda restringida al rol mínimo necesario vía `@PreAuthorize`,
por ejemplo:

```java
@PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
@PutMapping("/{id}")
public ResponseEntity<Producto> actualizarProducto(...)
```

Para recursos donde el propietario también debe poder actuar sobre su propio dato
(`Usuario`, `Carrito`), se combina el rol con una verificación de identidad:

```java
@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE_SUCURSAL') or #id == authentication.principal.usuarioId")
```

Para `Carrito`, la propiedad solo puede conocerse tras cargar la entidad (no es expresable
en SpEL puro sobre el `@PathVariable`), por lo que se implementó una verificación manual
post-carga (`CarritoController.verificarPropietarioODeGestion`) que lanza
`AccessDeniedException` si el solicitante no es ni el dueño del carrito ni un rol de
gestión.

### 2.4 Evidencia de ejecución en vivo (servidor real, no simulado)

```
$ curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}'

{"roles":["ROLE_ADMINISTRADOR"],
 "token":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOSVNUUkFET1IiXSwic3ViIjoi...",
 "username":"admin"}
→ HTTP 200
```

```
$ curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"contraseña_incorrecta"}'
→ HTTP 401
```

```
$ curl http://localhost:8080/api/productos          # sin header Authorization
→ HTTP 401

$ curl -X POST http://localhost:8080/api/productos \
    -H "Authorization: Bearer <token_de_cliente>" \
    -d '{"nombre":"Test","precio":100.0,"stock":1}'   # rol CLIENTE, operación reservada a gestión
→ HTTP 403
```

Verificado 2026-07-17 contra el servidor real (`./mvnw spring-boot:run`), no solo por
inspección de código ni por pruebas simuladas con mocks.

### 2.5 Limitación conocida (documentada, no oculta)

El token JWT es autocontenido, expira a las 24 h y no existe lista de revocación en el
servidor: si el rol de un usuario cambia mientras tiene una sesión activa, el cambio no se
refleja hasta que el token expira. Documentado en `README.md` y en
`specs/001-minimarket-backend-spec/tasks.md` (T055), con la mitigación recomendada para
producción (versión de token por usuario, fuera del alcance de este EFT).

---

## 3. Detalle de las pruebas unitarias implementadas

**Resultado de la suite completa** (verificado 2026-07-17, última ejecución tras
completar la Fase 9 de Convergencia de `specs/001`):

```
$ ./mvnw test
...
[INFO] Tests run: 116, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 3.1 Inventario de clases de prueba (15 clases, 116 pruebas)

| Clase | Tipo | Qué cubre |
|---|---|---|
| `UsuarioTest` | Unitaria (entidad) | Constructores de `Usuario`/`Rol`. |
| `service.ProductoServiceTest` | Unitaria (Mockito) | CRUD de productos. |
| `service.CarritoServiceTest` | Unitaria (Mockito) | `agregarProducto` con validación de stock (`StockInsuficienteException`, `DatosIncompletosException`). |
| `service.InventarioServiceTest` | Unitaria (Mockito) | Validación de movimientos (tipo, cantidad, producto, sucursal), reposición automática al llegar a stock mínimo, y la nueva `actualizarMovimiento` (T054) sin duplicar el efecto de reposición. |
| `service.UsuarioServiceTest` | Unitaria (Mockito) | `datosCompletos`, `registrar`, `puedeRegistrarVenta`. |
| `service.VentaServiceTest` | Unitaria (Mockito) | `calcularTotal`, `registrarVenta`. |
| `service.OrdenDeCompraServiceTest` | Unitaria (Mockito) | Generación de orden de compra solo cuando corresponde (stock mínimo definido, proveedor asociado, sin duplicar orden pendiente). |
| `service.PromocionServiceTest` | Unitaria (Mockito) | Vigencia de promociones por fecha, cálculo de precio con descuento, y la validación de rango de fechas agregada en la Convergencia (T052). |
| `service.PedidoServiceTest` | Unitaria (Mockito) | Confirmación de pedido con revalidación de stock y aplicación de precio promocional. |
| `service.ReposicionAutomaticaIntegrationTest` | **Integración** (`@SpringBootTest`, sin mocks) | Una salida que cruza el stock mínimo genera exactamente 1 `OrdenDeCompra`; una segunda salida no duplica. Detectó y forzó la corrección de un `LazyInitializationException` real en `ReporteServiceImpl`. |
| `service.PedidoIntegrationTest` | **Integración** (`@SpringBootTest`, sin mocks) | Pedido con promoción vigente → precio correcto, stock descontado, venta generada; pedido sin stock suficiente → `409`. |
| `security.AutorizacionRolesTest` | Integración (`@SpringBootTest` + `MockMvc`) | Solo `GERENTE_SUCURSAL`/`ADMINISTRADOR` pueden `PUT /api/productos/{id}`; el resto recibe `403`; sin token, `401`. |
| `security.PromocionAutorizacionTest` | Integración (`@SpringBootTest` + `MockMvc`) | `PromocionController` restringido a roles de gestión; rango de fechas inválido → `400`. |
| `web.HateoasLinksTest` | Integración (`@SpringBootTest` + `MockMvc`) | Enlaces `_links` reales en los 9 recursos de negocio. Detectó y forzó la corrección de **3 ciclos de serialización JSON infinitos** preexistentes (`Usuario↔Rol`, `Venta↔DetalleVenta`/`Pedido↔DetallePedido`, `Categoria↔Producto`). |
| `service.VentaIntegrationTest` | **Integración** (`@SpringBootTest`, sin mocks) | Venta directa en tienda: aplica el precio promocional vigente y descuenta stock con persistencia real; venta con stock insuficiente se rechaza sin alterar el stock. Prueba la corrección de T058 (segunda pasada de Convergencia) — antes de esa corrección, esta misma operación fallaba con `HTTP 500`. |

### 3.2 Por qué se usan pruebas de integración sin mocks además de unitarias

Varias de las pruebas más valiosas de este proyecto (`ReposicionAutomaticaIntegrationTest`,
`PedidoIntegrationTest`, `HateoasLinksTest`) usan un contexto Spring real con base de datos
H2 real, deliberadamente sin mocks. Esta decisión (`specs/001-minimarket-backend-spec/research.md`
Decisiones 10 y 11) detectó **4 defectos reales que las pruebas unitarias con Mockito no
habrían encontrado**: un `LazyInitializationException` que solo ocurre fuera de una sesión
de Hibernate activa, y 3 ciclos de serialización JSON circular que solo se manifiestan
cuando Jackson serializa relaciones bidireccionales con datos reales no vacíos.

---

## 4. Mejoras aplicadas al sistema durante el desarrollo

Se identificaron y corrigieron 6 defectos reales de la base consolidada, y 7 brechas
adicionales detectadas por una auditoría de convergencia (`/speckit-converge`) contra la
especificación y la pauta de evaluación. Todas están documentadas con su causa raíz en
`specs/001-minimarket-backend-spec/research.md` y en `specs/002-guion-video-ejecucion/research.md`.

### 4.1 Defectos de la base consolidada (detectados durante `speckit.implement`)

| # | Defecto | Causa raíz | Corrección |
|---|---|---|---|
| 1 | `MalformedInputException` al leer `application.properties` | Archivo codificado en ISO-8859-1, no UTF-8 — el mismo defecto exacto que `doc/grupo7.html` documentó para la entrega S8 | Reescrito en UTF-8; `project.build.sourceEncoding=UTF-8` agregado a `pom.xml` |
| 2 | `ExceptionInInitializerError` al compilar con JDK 25 | La dependencia `lombok` (sin ningún uso real en el código) es incompatible con el *annotation processing* de JDK 25 | Retirada la dependencia `lombok`; `maven-compiler-plugin` fijado en `3.14.0` |
| 3 | `403` esperado devuelto como `401` en operaciones sin autorización | `/error` no estaba en `permitAll()`: el reenvío interno de Spring Boot a `/error` re-entraba la cadena de filtros, donde `JwtAuthenticationFilter` omite por diseño el *error dispatch*, dejando la petición como anónima y sobrescribiendo el `403` original | `/error` agregado a `permitAll()` en `SecurityConfig` |
| 4 | `LazyInitializationException` en `ReporteServiceImpl.obtenerRotacion` | Se iteraba `Venta.detalles` (`@OneToMany` perezoso) fuera de una transacción activa | `@Transactional(readOnly = true)` agregado al método |
| 5-7 | 3 ciclos de serialización JSON infinita (`HttpMessageNotWritableException`, profundidad de anidamiento > 1000) | Relaciones bidireccionales `Usuario↔Rol`, `Venta↔DetalleVenta`/`Pedido↔DetallePedido`, `Categoria↔Producto` sin control de ciclo — nunca se habían detectado porque ningún endpoint anterior serializaba esas relaciones juntas con datos reales | `@JsonIgnore` / `@JsonIgnoreProperties` agregados en el lado correspondiente de cada relación |

### 4.2 Brechas cerradas en la Fase de Convergencia (`/speckit-converge` → T049-T055)

Tras completar la implementación inicial, una auditoría explícita contra `spec.md`,
`plan.md`, `tasks.md` y la Constitución del proyecto encontró 7 brechas, incluyendo un
hallazgo **CRITICAL** de seguridad:

| Tarea | Severidad | Hallazgo | Corrección |
|---|---|---|---|
| T049 | **CRITICAL** | `UsuarioController`, `CategoriaController`, `VentaController`, `CarritoController` y `DetalleVentaController` no tenían ninguna restricción de rol — cualquier usuario autenticado (incluido un `CLIENTE`) podía listar, editar o eliminar usuarios ajenos | `@PreAuthorize` por rol y verificación de propiedad agregados a los 5 controladores |
| T050 | HIGH | `contracts/openapi.yaml` definía `/promociones` pero nunca se implementó `PromocionController` | Controlador creado (`GET`/`POST`/`PUT`/`DELETE`), restringido a roles de gestión |
| T051 | MEDIUM | `OrdenDeCompra` y `Promocion` carecían de enlaces HATEOAS | `OrdenDeCompraModelAssembler` y `PromocionModelAssembler` creados |
| T052 | MEDIUM | `PromocionServiceImpl.save()` no validaba que `fechaFin` fuera posterior a `fechaInicio` | Validación agregada, lanza `DatosIncompletosException` |
| T053 | LOW | 7 controladores carecían de anotaciones `@Operation`/`@Tag`/`@ApiResponses` de springdoc | Anotaciones agregadas y verificadas en vivo contra `/v3/api-docs` |
| T054 | LOW | El `PUT` de `InventarioController` llamaba a `save()` directo, sin pasar por la validación de `registrarMovimiento` | Validación extraída a un método compartido; nuevo `actualizarMovimiento` reutiliza las mismas reglas |
| T055 | LOW | Falta de documentación sobre revocación de rol durante una sesión JWT activa | Documentado en `README.md` con mitigación recomendada |

Tras aplicar las 7 correcciones, la suite completa se re-ejecutó (`114/114`,
`BUILD SUCCESS`) sin regresiones.

### 4.3 Segunda pasada de Convergencia (`/speckit-converge` → T056-T059)

Una segunda auditoría, verificada en vivo contra el servidor real (no solo por
inspección de código), encontró 3 hallazgos **CRITICAL** adicionales que la primera
pasada no había alcanzado a cubrir, y 1 hallazgo LOW de documentación:

| Tarea | Severidad | Hallazgo | Corrección |
|---|---|---|---|
| T056 | **CRITICAL** | `GET /api/ventas`, `GET /api/carrito` y `GET /api/detalle-ventas` no tenían restricción alguna: cualquier usuario autenticado (incluido un `CLIENTE`) veía las ventas, carritos y detalles de **todos** los demás usuarios. `POST /api/carrito` tampoco validaba que el carrito perteneciera al solicitante | Lectura de `Venta`/`Carrito` filtrada al propio usuario salvo rol de gestión; `DetalleVenta` restringido a `CAJERO`/gestión; `Pedido` y `Carrito` verifican propiedad post-carga |
| T057 | **CRITICAL** | `InventarioController` no tenía un solo `@PreAuthorize`: cualquier usuario autenticado podía registrar, alterar o eliminar movimientos de inventario de cualquier producto/sucursal | `@PreAuthorize` agregado, restringido a `REPONEDOR`/`GERENTE_SUCURSAL`/`ADMINISTRADOR` (eliminación restringida a los dos últimos) |
| T058 | **CRITICAL** | `VentaController.guardarVenta` llamaba a `VentaService.save()` en vez de `VentaService.registrarVenta()` (el método real, ya cubierto por pruebas unitarias, pero nunca conectado a ningún endpoint): una venta directa en tienda no validaba ni descontaba stock, no aplicaba promociones, y de hecho fallaba con `HTTP 500` en uso normal | Controlador conectado a `registrarVenta()`; corregido el bug real que causaba el `500` (`DetalleVenta.venta` no vinculado antes de persistir); aplicado el precio promocional vigente. Documentado como parcial por diseño (`research.md` Decisión 12): no se agregó dimensión de sucursal a `Venta` para no romper 6 pruebas reales ya validadas de S6 — queda como mejora futura explícita, no oculta |
| T059 | LOW | `plan.md` seguía listando `Lombok` como dependencia, pese a haber sido retirada | `plan.md` corregido |

Tras estas 4 correcciones, la suite completa se re-ejecutó (`116/116`, `BUILD SUCCESS`) y
los tres hallazgos CRITICAL se reverificaron en vivo contra el servidor real:
`GET /api/detalle-ventas` (rol `CLIENTE`) → `403`; `POST /api/inventario` (rol `CLIENTE`)
→ `403`; una venta con cantidad mayor al stock disponible → `409` (antes `500`), y una
venta válida aplica el precio real y descuenta stock correctamente.

---

## 5. Documentación técnica de la API (OpenAPI + HATEOAS)

### 5.1 OpenAPI Specification (OAS)

- Librería: `springdoc-openapi-starter-webmvc-ui` 2.7.0.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Contrato JSON: `http://localhost:8080/v3/api-docs`
- **12 tags documentados**, uno por recurso de negocio (verificado en vivo, incluyendo
  acentos UTF-8 correctos): Autenticación, Carrito, Categorías, Detalle de Ventas,
  Inventario, Pedidos, Productos, Promociones, Reportes, Usuarios, Ventas, Órdenes de
  Compra.
- Cada operación mutable documenta sus códigos de respuesta relevantes (`200`/`201`,
  `400`, `403`, `404`) vía `@ApiResponses`.

### 5.2 HATEOAS

Patrón `RepresentationModelAssembler` (`com.minimarket.web.*ModelAssembler`) implementado
en los **9 recursos de negocio**: Producto, Categoría, Inventario, Carrito, Venta,
DetalleVenta, Pedido, OrdenDeCompra y Promoción. Cada respuesta incluye un bloque
`_links` con enlaces navegables reales (no simulados), por ejemplo:

```json
GET /api/productos  (Authorization: Bearer <token admin>)

{
  "_embedded": {
    "productoList": [{
      "id": 1, "nombre": "Arroz 1kg", "precio": 1200.0, "stock": 50,
      "categoria": {"id": 1, "nombre": "Abarrotes"},
      "_links": {
        "self":       {"href": "http://localhost:8080/api/productos/1"},
        "productos":  {"href": "http://localhost:8080/api/productos"},
        "categoria":  {"href": "http://localhost:8080/api/categorias/1"},
        "inventario": {"href": "http://localhost:8080/api/inventario?productoId=1"}
      }
    }]
  },
  "_links": {"self": {"href": "http://localhost:8080/api/productos"}}
}
```

Evidencia verificada 2026-07-17 contra el servidor real, con datos reales creados vía la
propia API (no *fixtures* precargados). Esta es exactamente la capacidad que
`doc/grupo7.html` señaló como **declarada en el informe sin código real** en la entrega de
la Semana 8 del grupo anterior — en esta entrega existe implementación real,
`web.HateoasLinksTest` (8 pruebas) y la evidencia en vivo de esta sección.

---

## 6. Capturas de pantalla / evidencia paso a paso

> Las capturas de pantalla propiamente tales (imágenes) deben incorporarse al PDF final
> por el equipo al completar la plantilla oficial. Esta sección documenta la secuencia
> exacta de pasos y la salida textual esperada de cada uno, verificada contra el servidor
> real, para que sirvan de guía a esas capturas.

1. **Arranque**: `./mvnw spring-boot:run` → aplicación disponible en `http://localhost:8080`.
2. **Login** (`POST /api/auth/login`, `admin`/`admin123`) → `200` con `token`/`username`/`roles`.
3. **Autorización sin token** (`GET /api/productos`) → `401`.
4. **Autorización con rol insuficiente** (`POST /api/productos` con token de `cliente`) → `403`.
5. **Creación de datos vía API** (`POST /api/categorias`, `POST /api/productos` como `admin`) → `200`.
6. **HATEOAS** (`GET /api/productos`) → bloque `_links` con `self`/`productos`/`categoria`/`inventario`.
7. **Swagger UI** (`http://localhost:8080/swagger-ui/index.html`) → 12 tags, "Authorize" con el token JWT.
8. **Pruebas unitarias** (`./mvnw test`) → `Tests run: 116, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.

El guion completo, con la secuencia detallada para el video de presentación, está en
`doc/guion-video-eft.md`.

---

## 7. Autoevaluación frente a la pauta de evaluación

| # | Criterio (pauta) | Evidencia de este proyecto | Nivel autopercibido |
|---|---|---|---|
| 1 | Desarrolla microservicios implementando las operaciones requeridas (15 pts) | 9 recursos de negocio con CRUD completo + reglas de negocio (stock, promociones, pedidos, reposición automática) | Completamente Logrado |
| 2 | Implementa frameworks de seguridad (15 pts) | JWT (JJWT HS256) + `@PreAuthorize` por rol y por propiedad en 12 controladores; verificado en vivo (§2.4) | Completamente Logrado |
| 3 | Configura y ejecuta pruebas unitarias (10 pts) | 116 pruebas, 15 clases, incluyendo integración real sin mocks que detectó 5 defectos reales (§3) | Completamente Logrado |
| 4 | Documenta la API con OpenAPI y HATEOAS (10 pts) | Swagger UI + 12 tags; HATEOAS real en 9 recursos, verificado en vivo (§5) | Completamente Logrado |
| 5 | Integra los componentes del backend (15 pts) | `./mvnw test` → `BUILD SUCCESS`; flujo de pedido→venta→reposición automática probado de punta a punta | Completamente Logrado |
| 6 | Genera un informe detallando el proceso y las evidencias (10 pts) | Este documento, con evidencia de ejecución citada en cada sección y trazabilidad a `research.md`/`capacidades-verificadas.md` | Completamente Logrado |
| 7 | Presenta el proyecto en un video (15 pts) | Guion completo en `doc/guion-video-eft.md`; **grabación pendiente** (bloqueada por falta de nombres reales del equipo y la sesión de grabación en sí) | *(pendiente de ejecutar)* |
| 8 | Organiza el código con buenas prácticas (10 pts) | Sin código muerto (Lombok retirado por no usarse), separación por capas (controller/service/repository/entity), validaciones centralizadas, manejo de excepciones vía `@RestControllerAdvice` | Completamente Logrado |

**Nota de transparencia**: el criterio 7 (video) no puede autoevaluarse como logrado
todavía porque la grabación no se ha realizado — esto se declara explícitamente en vez de
omitirse, siguiendo la misma disciplina de evidencia que el resto de este informe.

---

## 8. Pendientes antes de la entrega final

- [ ] Nombres reales de los integrantes del equipo (2-3 personas) y su jefe de proyecto —
      bloquea completar la portada de este informe y la columna "Responsable" de
      `doc/guion-video-eft.md`.
- [ ] Grabación del video (Kaltura, 7-10 min) siguiendo `doc/guion-video-eft.md`.
- [ ] Incorporar capturas de pantalla reales (imágenes) a la plantilla oficial
      (`doc/PBY2202_EFT_Plantilla_Informe_PDF.docx`) usando la secuencia de la sección 6.
- [ ] Crear el repositorio público en GitHub, subir el proyecto completo y el video, y
      generar el enlace a entregar en el AVA (pasos 1-4 de las instrucciones específicas).
- [ ] Exportar este borrador a la plantilla `.docx` oficial y a PDF final.

---

*Fuentes de evidencia citadas en este informe*: `doc/consolidacion-s6/capacidades-verificadas.md`,
`specs/001-minimarket-backend-spec/research.md`, `specs/002-guion-video-ejecucion/research.md`,
`specs/001-minimarket-backend-spec/tasks.md` (Fases 8 y 9: Convergencia), ejecución en vivo del
servidor y de `./mvnw test` el 2026-07-17.

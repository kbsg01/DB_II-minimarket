# Research: Backend MiniMarket Plus (EFT S9)

Ninguna entrada del Technical Context quedó marcada como `NEEDS CLARIFICATION`; este
documento registra las decisiones de diseño técnico necesarias para pasar de la
especificación (spec.md) a un modelo de datos y contratos concretos.

## 1. Interpretación de "microservicios" en el caso de negocio

- **Decision**: Implementar módulos de dominio cohesionados (controller/service/
  repository/entity por dominio) dentro de un único desplegable Spring Boot, tal como ya
  existe en el repositorio.
- **Rationale**: El caso de negocio usa "microservicios" de forma coloquial para referirse
  a las operaciones que deben implementarse (inventario, ventas, usuarios, pedidos), no a
  una arquitectura de despliegue distribuido. Un equipo de 2-3 integrantes con una semana
  de desarrollo no tiene margen para infraestructura de microservicios real (service
  discovery, API gateway, mensajería) sin comprometer las demás historias. La Constitución
  del proyecto (Principio I) ya adoptó esta interpretación.
- **Alternatives considered**: Microservicios físicamente independientes por dominio
  (rechazado: sobre-ingeniería para el alcance y plazo del EFT, y no exigido literalmente
  por la pauta de evaluación, que evalúa "operaciones requeridas" y "ejecución correcta",
  no topología de despliegue).

## 2. Librería para JWT — actualizado: se obtiene por consolidación, no por elección nueva

- **Decision**: `io.jsonwebtoken:jjwt-api` + `jjwt-impl` + `jjwt-jackson` (JJWT 0.11.5,
  versión exacta ya usada y probada en `feat/microservices-junit-s6`).
- **Rationale**: `specs/002-guion-video-ejecucion/research.md` (Decisión 1) confirmó que
  esta elección ya fue hecha, implementada y probada realmente en S6
  (`JwtAuthenticationFilter`, `JwtUtil`, `JwtAuthenticationEntryPoint`, `AuthController`,
  vigencia 24 horas HS256). Este plan consolida ese código en vez de reimplementarlo desde
  cero sobre el `JwtUtil` vacío que había en `feat/eft-s9`.
- **Alternatives considered**: Igual que antes (`spring-boot-starter-oauth2-resource-server`,
  Nimbus JOSE+JWT directo) — ver razones de rechazo originales; se mantienen válidas y ya
  fueron descartadas también implícitamente por el equipo al escribir el JWT real de S6.

## 3. Documentación OpenAPI (OAS) — actualizado: se obtiene por consolidación

- **Decision**: `org.springdoc:springdoc-openapi-starter-webmvc-ui` 2.7.0 (versión exacta
  ya usada y probada en `feat/microservices-junit-s6`, vía `OpenApiConfig`).
- **Rationale**: Igual que la Decisión 2: ya está implementada y funcionando en S6; se
  consolida directamente en lugar de reconfigurarla desde cero.
- **Alternatives considered**: Sin cambios respecto a la versión anterior de este
  documento (Springfox descartado, spec manual descartada).

## 4. Enlaces HATEOAS — actualizado: bloqueante, sin precedente real en ninguna rama

- **Decision**: `org.springframework.hateoas:spring-hateoas`, usando `EntityModel` /
  `CollectionModel` y `WebMvcLinkBuilder` para construir enlaces desde los controladores.
- **Rationale**: A diferencia de JWT y OpenAPI, **ninguna rama del historial (S1-S8)
  contiene una implementación real de HATEOAS** — `feat/hateoas-s8` la declara en su
  README/informe pero el código revisado no tiene `EntityModel`/`CollectionModel`/`linkTo`,
  hallazgo confirmado independientemente por el feedback del profesor
  (`doc/grupo7.html`), que penalizó exactamente esa discrepancia. Por eso esta es la única
  de las cuatro decisiones de esta lista que requiere implementación nueva real, y por eso
  la Historia de Usuario 4 se reclasificó de P3 a P1 (ver Clarifications en
  `specs/002-guion-video-ejecucion/spec.md`).
- **Alternatives considered**: Construcción manual de enlaces como campos de string en los
  DTOs (rechazado: no sigue un estándar reconocible, difícil de mantener consistente entre
  todos los controladores, y es precisamente el tipo de atajo que ya generó la observación
  negativa del profesor sobre S8).

## 5. Modelo de datos multi-sucursal

- **Decision**: Introducir la entidad `Sucursal` como dimensión propia. `Inventario`,
  `OrdenDeCompra` y `Pedido` referencian una `Sucursal`. El catálogo (`Producto`,
  `Categoria`) permanece compartido entre todas las sucursales; el campo `stock` que hoy
  vive directamente en `Producto` se reemplaza como fuente de verdad por la suma de
  movimientos de `Inventario` agrupados por producto y sucursal.
- **Rationale**: La pauta y las instrucciones exigen stock en tiempo real "en todas las
  sucursales" y reportes de rotación; sin una dimensión `Sucursal` no es posible
  diferenciar el stock ni los pedidos por punto de venta, que es precisamente el
  requisito de negocio central del caso.
- **Alternatives considered**: Mantener `Producto.stock` como único contador global
  (rechazado: no soporta el requisito explícito de stock por sucursal); duplicar el
  catálogo completo por sucursal (rechazado: sobre-ingeniería, el catálogo de productos es
  el mismo en todas las tiendas según el caso de negocio, solo el stock varía).

## 6. Reposición automática (orden de compra al llegar a stock mínimo)

- **Decision**: `InventarioService` calcula el stock vigente de un producto en una
  sucursal tras cada movimiento de salida; si el resultado es menor o igual al nivel
  mínimo configurado para ese producto y no existe ya una `OrdenDeCompra` pendiente
  equivalente (mismo producto + sucursal + proveedor sin recepción), genera una nueva
  orden de compra automáticamente.
- **Rationale**: Cubre el requisito funcional FR-006 y el edge case de doble generación
  ante movimientos concurrentes identificado en spec.md (verificación de "orden pendiente
  equivalente" antes de crear una nueva).
- **Alternatives considered**: Job programado (cron) que recorre todo el inventario
  periódicamente (rechazado para este alcance: introduce latencia entre el cruce del
  umbral y la generación de la orden, y complejidad de scheduling innecesaria para un
  proyecto académico).

## 7. Estrategia de pruebas unitarias

- **Decision**: JUnit 5 + Mockito para pruebas de servicios (mockeando repositorios),
  cubriendo reglas de negocio (cálculo de stock, generación de orden de compra, cálculo de
  total con promoción, validación de disponibilidad). `spring-security-test`
  (`@WithMockUser`) para pruebas de autorización por rol a nivel de controlador.
- **Rationale**: Ya son dependencias presentes en `pom.xml`; no se requiere introducir un
  framework de pruebas adicional. Cumple el Principio III (NON-NEGOTIABLE) de la
  Constitución.
- **Alternatives considered**: Pruebas de integración completas con Testcontainers
  (rechazado como requisito mínimo: el perfil H2 en memoria ya definido es suficiente para
  el alcance evaluado; puede añadirse opcionalmente sin bloquear el resto del plan).

## 8. Cumplimiento de la Ley de Protección de Datos Personales (Chile)

- **Decision**: Los DTOs/`EntityModel` de salida excluyen `password` y cualquier dato
  financiero sensible; las contraseñas se almacenan con `BCryptPasswordEncoder` (ya
  configurado en `SecurityConfig`); los mensajes de error de autenticación no revelan si
  el fallo fue por usuario inexistente o contraseña incorrecta.
- **Rationale**: Requisito explícito de FR-004 y del Principio II de la Constitución;
  BCrypt ya está configurado en el proyecto, por lo que la brecha real está en el diseño
  de las respuestas (DTOs), no en el cifrado de contraseñas.
- **Alternatives considered**: Exponer la entidad `Usuario` directamente en las
  respuestas (rechazado: expondría el hash de la contraseña y viola el Principio II).

## 9. `/error` debe estar en `permitAll()` — descubierto al implementar T012/T014

- **Decision**: Agregar `"/error"` a los `requestMatchers(...).permitAll()` de
  `SecurityConfig` (consolidado desde `specs/002`).
- **Rationale**: Al verificar en vivo (no solo con `@WithMockUser`/MockMvc) que un rol sin
  autorización recibía `403` en `PUT /api/productos/{id}`, se detectó que la app real
  respondía **401** en su lugar, con el cuerpo de `JwtAuthenticationEntryPoint`. Con
  `logging.level.org.springframework.security=DEBUG` se confirmó la causa exacta:
  `AccessDeniedHandlerImpl` sí decide responder 403, pero `response.sendError(403)`
  dispara el forward interno de Spring Boot a `/error`; en ese segundo paso por la cadena
  de filtros, `JwtAuthenticationFilter` (un `OncePerRequestFilter`) se salta por diseño
  los dispatch de tipo `ERROR` (`shouldNotFilterErrorDispatch()` es `true` por defecto),
  por lo que el contexto de seguridad queda anónimo; como `/error` no estaba en
  `permitAll()`, esa segunda pasada lo rechaza también, y termina ganando un 401 en vez
  del 403 original. Es un problema conocido de Spring Security + Boot, no exclusivo de
  este proyecto, pero **solo se detectó probando contra el servidor real**, no con
  `AutorizacionRolesTest` (MockMvc no reproduce el forward de contenedor a `/error` de la
  misma forma, por lo que el test unitario pasaba igual con o sin el bug).
- **Alternatives considered**: Registrar un `AccessDeniedHandler` personalizado en vez de
  tocar `permitAll()` (rechazado: no resuelve el problema de raíz — el filtro JWT seguiría
  sin ejecutarse en el dispatch `/error` y cualquier otro código de error pasaría por el
  mismo camino; agregar `/error` a `permitAll()` es la solución mínima y estándar).

## 10. `ReporteServiceImpl` necesita `@Transactional` para leer `Venta.detalles` (descubierto en T024)

- **Decision**: Anotar `ReporteServiceImpl.obtenerRotacion` con
  `@Transactional(readOnly = true)`.
- **Rationale**: `Venta.detalles` es una colección `@OneToMany` de carga perezosa (lazy).
  Al escribir la prueba de integración real de T024
  (`ReposicionAutomaticaIntegrationTest`, sin mocks, contra Spring Boot + H2), se obtuvo
  `LazyInitializationException: no Session` al iterar `venta.getDetalles()` dentro de
  `obtenerRotacion`. La aplicación real no manifiesta este error en una request HTTP
  porque `spring.jpa.open-in-view=true` (valor por defecto de Spring Boot, activo en este
  proyecto) mantiene la sesión de Hibernate abierta durante todo el ciclo de vida de la
  request — pero ese comportamiento es un efecto secundario implícito, no una garantía
  para cualquier invocador del servicio (por ejemplo, un futuro job programado o esta
  misma prueba de integración). Envolver el método en su propia transacción es la
  solución correcta y explícita, independiente de `open-in-view`.
- **Alternatives considered**: Cambiar la relación a carga `EAGER` (rechazado: penaliza el
  rendimiento de cualquier consulta de `Venta` que no necesite el detalle, y no es el
  problema real — el problema es la ausencia de una transacción explícita, no la
  estrategia de fetch); desactivar `spring.jpa.open-in-view` (rechazado para este alcance:
  tiene efectos más amplios sobre otros controladores que si dependen de ese
  comportamiento hoy, y su corrección integral excede el alcance de esta historia de
  usuario).

## 11. Tres ciclos de serialización JSON infinitos preexistentes (descubiertos al implementar HATEOAS, T044)

- **Decision**: Cortar el ciclo con `@JsonIgnore`/`@JsonIgnoreProperties` en el lado
  "inverso" de tres relaciones bidireccionales JPA:
  - `Rol.usuarios` (`@JsonIgnore`) — rompe `Usuario→roles→Rol→usuarios→Usuario→...`.
  - `DetalleVenta.venta` y `DetallePedido.pedido`
    (`@JsonIgnoreProperties("detalles")`) — rompe `Venta→detalles→DetalleVenta→venta→
    detalles→...` (y el mismo patrón en Pedido/DetallePedido).
  - `Categoria.productos` (`@JsonIgnore`) — rompe `Producto→categoria→productos→cada
    producto→categoria→...`.
- **Rationale**: Al implementar los assemblers HATEOAS (T037-T043) y la prueba real
  `HateoasLinksTest` (T044, `MockMvc` sobre el contexto completo, sin mocks), las
  respuestas de `Venta`, `Carrito`, `DetalleVenta`, `Pedido` y la colección de
  `Producto` fallaban con `HttpMessageNotWritableException: Document nesting depth
  (1001) exceeds the maximum allowed (1000)` — el límite de seguridad de Jackson ante una
  recursión infinita real, no un error de HATEOAS en sí. Estas tres relaciones
  bidireccionales (`Usuario`/`Rol`, `Venta`/`DetalleVenta`, `Pedido`/`DetallePedido`,
  `Categoria`/`Producto`) existen desde `feat/microservices-junit-s6` y **nunca se habían
  serializado juntas** en una respuesta real: los controladores CRUD simples anteriores
  siempre devolvían un solo lado de cada relación de forma aislada. Es la primera vez que
  el proyecto ejercita estos flujos con datos reales (no mocks) en un contexto HTTP
  completo — otro caso, como los de las Decisiones 9 y 10, en que la verificación en vivo
  encontró bugs que ninguna prueba con mocks podía detectar.
- **Alternatives considered**: `@JsonManagedReference`/`@JsonBackReference` (rechazado:
  requiere pares de anotaciones acopladas en ambos lados y es más frágil ante refactors
  que `@JsonIgnore` unidireccional); DTOs de respuesta independientes de las entidades
  (rechazado para este alcance: es la solución más robusta a largo plazo, pero reescribir
  todos los controladores como DTOs excede el alcance de esta historia de usuario y no lo
  exige ningún requisito funcional).

## 12. `Venta` directa (en tienda) no queda sucursal-consciente al conectarla a `registrarVenta` (T058, segunda pasada de `/speckit-converge`)

- **Decision**: Al corregir el hallazgo CRITICAL de que `VentaController.guardarVenta`
  llamaba a `VentaService.save()` en vez de `VentaService.registrarVenta()` (dejando la
  venta directa sin validar/descontar stock ni aplicar promociones, y de hecho fallando
  con `HTTP 500` por no vincular `DetalleVenta.venta` antes de persistir), se conectó el
  controlador al método real, se corrigió el bug de la referencia inversa faltante, y se
  agregó la aplicación del precio promocional vigente vía `PromocionService`. **No** se
  extendió `registrarVenta` para crear un movimiento de `Inventario` (con `Sucursal`)
  como sí hace `PedidoServiceImpl.confirmarPedido`, ni se agregó un campo `Sucursal` a la
  entidad `Venta`. `registrarVenta` sigue validando/descontando el campo `Producto.stock`
  directo, tal como lo hacía desde su consolidación original de S6.
- **Rationale**: La entidad `Venta` (y las 6 pruebas reales `VentaServiceTest`,
  consolidadas verbatim desde `feat/microservices-junit-s6`) nunca tuvieron una dimensión
  de `Sucursal`; agregarla habría exigido una migración de esquema (columna `sucursal_id`
  no nula) y reescribir esas 6 pruebas para fijar una sucursal en cada caso, un cambio
  bastante mayor al alcance concreto del hallazgo de convergencia, que era "la venta no
  valida nada en absoluto, ni siquiera con el modelo simple que ya tenía". Se prioriza
  cerrar el hallazgo CRITICAL real (cero validación, promociones no aplicadas, `HTTP 500`
  en uso normal) sin arriesgar una migración de esquema no solicitada ni romper pruebas ya
  validadas, seguiendo el mismo principio aplicado en otras decisiones de este documento
  (research.md Decisión 8: consolidar sin exceder el alcance salvo que sea
  estrictamente necesario).
- **Known limitation (no oculta)**: una venta registrada directamente en tienda
  (`POST /api/ventas`) no genera un movimiento de `Inventario` por sucursal y por lo tanto
  no puede disparar la reposición automática (FR-006) de la misma forma que un pedido en
  línea confirmado. Esto queda como una mejora de alcance futuro, no como parte de este
  hallazgo de convergencia — a diferencia de la S8 anterior, se documenta explícitamente
  en vez de omitirse.
- **Alternatives considered**: Agregar `Sucursal` a `Venta` y replicar el flujo completo
  de `PedidoServiceImpl.confirmarPedido` (rechazado para este alcance: exige migración de
  esquema y reescritura de pruebas existentes, fuera de lo que el hallazgo de convergencia
  pedía corregir; queda documentado aquí como mejora futura en vez de implementarse a
  medias o silenciarse).

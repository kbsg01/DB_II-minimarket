# Guion — Video de Presentación EFT (MiniMarket Plus)

**Duración objetivo**: 7 a 10 minutos (420-600 segundos) — SC-002. Suma actual: **540
segundos (9 min)**.
**Herramienta de grabación/publicación**: Kaltura (FR-008, instrucciones específicas —
"Presentación")
**Rama de referencia**: `feat/eft-s9` (única rama de entrega, FR-010)
**Requisito confirmado**: las instrucciones específicas exigen video obligatorio (Kaltura,
7-10 min, todos los integrantes participan) y la pauta de evaluación lo pondera en su
criterio 7 (15 puntos, "Presenta el proyecto en un video explicando el funcionamiento, la
implementación y las conclusiones del equipo").

> ⚠️ **Pendiente antes de grabar**: la columna "Responsable" aún no tiene nombres reales
> del equipo (no se han provisto en esta sesión). No grabar hasta completar la Fase 5 de
> `specs/002-guion-video-ejecucion/tasks.md` (US3) con los integrantes reales — ver sección
> al final de este documento.

## Checklist previo a grabar (bloqueante)

- [x] `./mvnw test` en `feat/eft-s9` termina en `BUILD SUCCESS` (SC-006) — verificado
      2026-07-18, **135/135** pruebas (`specs/001-minimarket-backend-spec` completa,
      incluyendo las Fases 8-11 de Convergencia: autorización granular, Promociones,
      corrección de la venta directa, `Sucursal`/`Proveedor` completados, y la corrección
      de dos defectos CRITICAL detectados contra el servidor real: reposición automática
      silenciosa y `HTTP 500` en `POST /api/pedidos`).
- [x] Cada capacidad a mostrar tiene un registro `CapacidadVerificada` con
      `estado = VERIFICADA` (ver `doc/consolidacion-s6/capacidades-verificadas.md`)
- [x] Ninguna capacidad con `estado != VERIFICADA` aparece en las secciones de abajo
      (FR-003) — HATEOAS y la autorización por rol/propiedad están verificadas en los 11
      recursos de negocio y en todos los controladores, no solo en los que se cubrieron
      en la primera pasada de convergencia.
- [x] La suma de "Duración estimada" de todos los segmentos está entre 420 y 600 segundos
      (540s)
- [ ] Responsables reales asignados a cada segmento (pendiente — ver nota arriba)

## Segmentos (orden obligatorio — FR-005)

Cubren los 4 puntos exigidos por las instrucciones específicas: funcionamiento del
backend, ejecución de pruebas unitarias, seguridad aplicada y documentación de la API, y
conclusiones del equipo.

| # | Segmento | Responsable | Duración estimada | Acción a ejecutar | Contingencia |
|---|----------|-------------|--------------------|--------------------|--------------|
| 1 | Introducción del equipo | *(pendiente)* | 30 s | Presentar al equipo y el caso de negocio MiniMarket Plus: cadena de minimarkets que digitaliza inventario centralizado, ventas, pedidos en línea con promociones, y seguridad de su backend. | n/a |
| 2 | Funcionamiento del backend | *(pendiente)* | 150 s | 1) `./mvnw spring-boot:run`. 2) Login JWT: `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}'` → mostrar el JSON con `token`/`username`/`roles`. 3) Con el token, crear una categoría y un producto (`POST /api/categorias`, `POST /api/productos`). 4) Consultar disponibilidad de ese producto en una sucursal (`GET /api/sucursales/{id}/productos/{id}/disponibilidad`) y generar un pedido en línea (`POST /api/pedidos`) mostrando que el stock se descuenta y, si hay una promoción vigente, el precio aplicado refleja el descuento. 5) Repetir la consulta de productos sin el header `Authorization` → `401`. | Si el arranque falla en vivo, mostrar la captura/log guardado de esta misma ejecución (`doc/consolidacion-s6/baseline-antes.md` tiene el procedimiento de referencia). |
| 3 | Ejecución de pruebas unitarias | *(pendiente)* | 90 s | Ejecutar `./mvnw test` en pantalla completa y mostrar el resumen final: `Tests run: 135, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`. Mencionar brevemente qué cubren (autorización por rol y por propiedad, stock por sucursal, reposición automática, pedidos con promoción, enlaces HATEOAS, y las pruebas de integración reales — sin mocks, incluida una contra `MockMvc`/JSON real — que detectaron varios bugs reales durante el desarrollo, incluidos dos defectos CRITICAL que solo aparecían al llamar la API real). | Mostrar la salida guardada de la última ejecución si el build tarda demasiado en vivo. |
| 4 | Seguridad aplicada y documentación de la API | *(pendiente)* | 180 s | 1) Mostrar de nuevo el login JWT (reutilizar token del segmento 2) y un `403` al intentar una operación de gestión (p. ej. `POST /api/productos` o `POST /api/inventario`) con el rol `cliente`. 2) Mostrar que un `cliente` solo puede ver sus propias ventas/carritos/pedidos, no los de otros usuarios (`GET /api/ventas` con distintos tokens), y que `POST /api/pedidos` siempre se atribuye al usuario autenticado aunque el body indique otro. 3) Abrir `http://localhost:8080/swagger-ui/index.html`, mostrar los 14 recursos documentados (incluyendo `Promociones`, y los nuevos `Sucursales`/`Proveedores`) y el botón *Authorize* con el token JWT. 4) Hacer `GET /api/productos/{id}` y mostrar el bloque `_links` real (`self`, `categoria`, `inventario`) navegando uno de esos enlaces en vivo. | Si Swagger UI no responde, repetir la llamada `GET /v3/api-docs` con Postman/curl y mostrar el JSON crudo, incluido el bloque `_links`. |
| 5 | Conclusiones del equipo | *(pendiente)* | 90 s | Cada integrante resume su aporte específico al desarrollo y una conclusión sobre el proceso: qué se aprendió, qué se corrigió respecto a semanas anteriores (bug de codificación, JWT real, HATEOAS real que faltó en la Semana 8) y qué se corrigió durante la propia auditoría de convergencia de esta entrega — incluyendo la lección más importante: dos defectos CRITICAL (`Sucursal`/`Proveedor` sin controlador, y `POST /api/pedidos` devolviendo `HTTP 500`) solo se detectaron al ejercitar el servidor real con `curl`, no por las 116 pruebas automatizadas que ya existían — la disciplina de verificar en vivo, no solo con pruebas, sigue siendo indispensable. | n/a |

**Total**: 30 + 150 + 90 + 180 + 90 = **540 segundos (9 minutos)** — dentro del rango 7-10 min.

## Referencias de capacidades verificadas usadas en este guion

(Fuente: `doc/consolidacion-s6/capacidades-verificadas.md`; todas con `estado = VERIFICADA`)

| Capacidad | Rama/fase origen | Estado | Evidencia |
|---|---|---|---|
| Autenticación JWT (login) | `feat/microservices-junit-s6`, consolidada en `feat/eft-s9` | VERIFICADA | `POST /api/auth/login` con `admin`/`admin123` → `200` + token JWT real (JJWT HS256). |
| Rechazo de credenciales inválidas / sin token | ídem | VERIFICADA | Contraseña incorrecta → `401`; sin header `Authorization` → `401`. |
| Autorización por rol y por propiedad (todos los controladores) | `specs/001-minimarket-backend-spec`, Fases 8 y 9 de Convergencia | VERIFICADA | `PUT /api/productos/{id}` con rol `CLIENTE` → `403`; `POST /api/inventario` con `CLIENTE` → `403`; `GET /api/ventas`/`GET /api/carrito` filtran al propio usuario salvo rol de gestión; `GET /api/detalle-ventas` restringido a `CAJERO`/gestión. |
| Gestión centralizada de promociones | `specs/001-minimarket-backend-spec` (T050) | VERIFICADA | `PromocionController` (`GET`/`POST`/`PUT`/`DELETE /api/promociones`), restringido a roles de gestión, con validación de rango de fechas. |
| Pedidos en línea con precio promocional | `specs/001-minimarket-backend-spec` | VERIFICADA | Pedido confirmado aplica el descuento vigente al precio final y descuenta stock de la sucursal correspondiente; venta directa en tienda también aplica el precio promocional desde la corrección de T058. |
| Ejecución de pruebas unitarias | `specs/001` + `specs/002` | VERIFICADA | `./mvnw test` → `BUILD SUCCESS`, **135/135** pruebas. |
| Documentación OpenAPI (Swagger UI) | `feat/microservices-junit-s6` + T053 | VERIFICADA | `GET /swagger-ui/index.html` → `200`; `GET /v3/api-docs` → `200`; 14 tags documentados en todos los controladores de negocio, incluyendo `Sucursales`/`Proveedores` (T062). |
| HATEOAS (enlaces `_links` reales, 11 recursos) | `specs/001-minimarket-backend-spec` (implementación nueva + T051/T062) | VERIFICADA | `GET /api/productos/{id}` → `_links.self`, `_links.categoria`, `_links.inventario` con hrefs navegables reales; los 11 recursos de negocio (incluyendo `OrdenDeCompra`, `Promocion`, `Sucursal` y `Proveedor`) tienen sus propios assemblers. Es exactamente la capacidad que `doc/grupo7.html` señaló como declarada sin código real en S8 — ahora tiene código real y pruebas (`HateoasLinksTest`, 9 pruebas). |
| Gestión de Sucursales y Proveedores vía API | `specs/001-minimarket-backend-spec` (T062, cuarta pasada de Convergencia) | VERIFICADA | `SucursalController`/`ProveedorController` creados: antes no existía ningún endpoint real pese a estar declarados en `contracts/openapi.yaml`; `DataInitializer` siembra los datos mínimos que `quickstart.md` siempre exigió. |
| Pedido en línea sin fallar en el servidor real | `specs/001-minimarket-backend-spec` (T064, cuarta pasada de Convergencia) | VERIFICADA | `POST /api/pedidos` con un cliente real (JSON mínimo `{"producto":{"id":X}}`) respondía `HTTP 500`; corregido recargando el `Producto` completo antes de calcular el precio. |
| Reposición automática disparada por la API real | `specs/001-minimarket-backend-spec` (T063, cuarta pasada de Convergencia) | VERIFICADA | `POST /api/inventario` real no generaba ninguna `OrdenDeCompra` al cruzar el stock mínimo, sin error visible; corregido de la misma forma que T064. |

## Publicación (FR-008)

1. Grabar con Kaltura siguiendo el instructivo oficial
   (`https://ava.duoc.cl/bbcswebdav/xid-3577107_1`).
2. Subir el video al mismo repositorio GitHub público del proyecto.
3. Generar el enlace del repositorio y subirlo al AVA junto con el informe (`doc/informe-eft.md` /
   su versión final en `doc/PBY2202_EFT_Plantilla_Informe_PDF.docx`).

## Pendiente (Fase 5 de `specs/002-guion-video-ejecucion/tasks.md`, US3)

- [ ] T026: Completar la columna "Responsable" con el nombre real de cada integrante del
      equipo (2-3 personas según las instrucciones específicas). **Se requiere esta
      información del usuario/equipo — no se puede completar sin datos reales.**
- [ ] T027: Validar con cada integrante que el segmento asignado corresponde a su aporte
      real durante el desarrollo.
- [ ] T028: Documentar en esta misma tabla quién cubre cada segmento como respaldo si el
      responsable asignado no puede grabar.
- [ ] T031: Grabar el video en Kaltura siguiendo este guion y subirlo al repositorio.

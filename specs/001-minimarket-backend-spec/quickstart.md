# Quickstart: Validación end-to-end — Backend MiniMarket Plus

Esta guía valida que las cuatro historias de usuario de `spec.md` funcionan de extremo a
extremo (Principio V de la Constitución), usando el perfil H2 ya configurado en
`src/main/resources/application.properties`.

## Prerrequisitos

- **La consolidación de `specs/002-guion-video-ejecucion` ya aplicada**: JWT, roles y
  OpenAPI reales de `feat/microservices-junit-s6` integrados en `feat/eft-s9`, y
  `./mvnw test` en `BUILD SUCCESS` (gate SC-006 de esa feature). Este quickstart asume esa
  base, no una implementación de JWT/OpenAPI desde cero.
- Java 17 y Maven Wrapper (`mvnw` / `mvnw.cmd`, ya incluidos en el repositorio).
- `spring-hateoas` agregado a `pom.xml` (única dependencia realmente nueva de esta feature;
  ver research.md → Decisión 4).
- Datos semilla mínimos: al menos una `Sucursal`, un `Proveedor`, un `Producto` con
  `stockMinimo` configurado, y un `Usuario` por cada rol relevante (`CLIENTE`,
  `GERENTE_SUCURSAL`).

## Arranque

```bash
./mvnw spring-boot:run
```

La aplicación debe iniciar sin errores en `http://localhost:8080`.

## Escenario 1 — Autenticación y control de acceso por roles (US1)

1. `POST /auth/login` con un usuario `CLIENTE` → **200** y un token JWT.
2. Con ese token, `PUT /productos/{id}` (actualizar precio) → **403** (rol sin
   autorización).
3. `POST /auth/login` con un usuario `GERENTE_SUCURSAL` → **200** y un token JWT.
4. Con ese token, `PUT /productos/{id}` → **200** (autorizado).
5. Repetir el paso 2 sin token (o con un token alterado) → **401**, sin detalles internos
   en el cuerpo de la respuesta.

**Resultado esperado**: confirma FR-001, FR-002, FR-003 y el criterio SC-002.

## Escenario 2 — Inventario en tiempo real y reposición automática (US2)

1. Como `GERENTE_SUCURSAL`, `GET /sucursales/{sucursalId}/productos/{productoId}/disponibilidad`
   → registra el stock inicial visible.
2. `POST /sucursales/{sucursalId}/inventario` con `tipoMovimiento=SALIDA` y una cantidad
   que deje el stock resultante en o por debajo de `stockMinimo` del producto.
3. `GET /ordenes-compra` → debe existir exactamente una orden nueva en estado
   `PENDIENTE` para ese producto y sucursal (no duplicada si se repite el mismo
   movimiento antes de que la orden sea recibida).
4. `GET /reportes/rotacion?desde=...&hasta=...` → el producto movido aparece reflejado
   según su volumen de movimiento en el rango de fechas de la prueba.

**Resultado esperado**: confirma FR-005, FR-006, FR-007 y los criterios SC-003, SC-004,
SC-008.

## Escenario 3 — Disponibilidad y pedido en línea (US3)

1. Como `CLIENTE`, `GET /sucursales/{sucursalId}/productos/{productoId}/disponibilidad`
   → confirma cantidad disponible.
2. `POST /pedidos` solicitando una cantidad menor o igual a la disponible, con
   `modoEntrega=RETIRO_TIENDA` o `DESPACHO_DOMICILIO` → **201**, y el stock de la
   sucursal se reduce en consecuencia.
3. Repetir el paso 2 solicitando una cantidad mayor a la disponible → **409**, con el
   máximo disponible informado en el mensaje.
4. Con una `Promocion` vigente sobre el producto, repetir el paso 2 → el
   `precioAplicado` del detalle del pedido refleja el descuento vigente.

**Resultado esperado**: confirma FR-008, FR-009, FR-010, FR-011 y los criterios SC-005,
SC-006.

## Escenario 4 — Documentación navegable de la API (US4)

1. Abrir `http://localhost:8080/swagger-ui.html` (o la ruta expuesta por
   springdoc-openapi) → se listan todas las operaciones de `contracts/openapi.yaml`
   con sus parámetros y respuestas.
2. `GET /productos/{productoId}` → la respuesta incluye enlaces (`_links`) hacia su
   inventario y su categoría, navegables sin conocer la URL de antemano.

**Resultado esperado**: confirma FR-012, FR-013 y el criterio SC-007.

## Evidencia para el informe

Cada paso anterior (capturas de pantalla, respuesta JSON, o salida de
`./mvnw test`) es la evidencia técnica que el Principio VI de la Constitución exige
trasladar a la plantilla oficial de informe (`doc/PBY2202_EFT_Plantilla_Informe_PDF.docx`):
configuración de seguridad, resultados de pruebas, y documentación de la API.

# MiniMarket Plus — Backend (EFT S9)

Sistema backend para la gestión de MiniMarket Plus (productos, categorías, carrito,
inventario, ventas y usuarios con roles), desarrollado en Spring Boot 3.4.1. Consolidado
para el EFT (Semana 9) a partir de la implementación real y probada de la Semana 6
(`feat/microservices-junit-s6`) — ver `specs/002-guion-video-ejecucion/` para el detalle
completo de esa consolidación y el porqué.

## Requisitos

- Java 17 (probado también en runtime Java 25; ver nota de compatibilidad más abajo)
- No requiere base de datos externa: usa H2 en memoria

## Ejecución

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

La aplicación queda disponible en `http://localhost:8080`.

## Pruebas

```bash
./mvnw test
```

Resultado esperado: `Tests run: 114, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`.

## Autenticación

La API usa JWT. Para autenticarse:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Usuarios de demostración (creados automáticamente al arrancar por `DataInitializer`):

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | ROLE_ADMIN |
| `cajero` | `cajero123` | ROLE_CAJERO |
| `cliente` | `cliente123` | ROLE_CLIENTE |

Incluir el token recibido en peticiones posteriores: `Authorization: Bearer <token>`.

**Limitación conocida — revocación de rol durante una sesión activa**: el token JWT es
autocontenido y expira a las 24 h (`JwtUtil.EXPIRATION_MS`); no existe una lista de
revocación (blacklist) en el servidor. Si el rol de un usuario cambia (por ejemplo, un
`ADMINISTRADOR` degrada a un `CLIENTE`) mientras ese usuario tiene un token vigente, el
token seguirá conservando los roles con los que fue emitido hasta que expire — el cambio
de rol no se refleja en la sesión activa. Mitigación recomendada para producción (fuera de
alcance de este EFT): invalidar tokens emitidos antes del cambio de rol comparando un
`tokenVersion`/timestamp por usuario en cada request, o reducir el tiempo de expiración y
aceptar la ventana de exposición resultante.

## Documentación de la API

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Contrato OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| Consola H2 | http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:testdb`, usuario `sa`, sin contraseña) |

Los endpoints `/public/**`, `/api/auth/**`, Swagger UI y `/v3/api-docs` son de acceso
público; el resto requiere un token JWT válido.

## Nota de compatibilidad (JDK 25)

El proyecto se dirige a Java 17 (`--release 17`), pero se compila y ejecuta correctamente
sobre JDK 25. La dependencia `lombok` fue retirada por no usarse en ningún archivo del
proyecto y por ser incompatible con el annotation processing de JDK 25; `maven-compiler-plugin`
está fijado en la versión `3.14.0` por el mismo motivo. Ver
`specs/002-guion-video-ejecucion/research.md` (Decisión 7) para el detalle.

## Estado de HATEOAS

La documentación OpenAPI (Swagger UI) y los enlaces HATEOAS (`_links`) están
implementados y verificados en todos los recursos (Producto, Categoría, Inventario,
Carrito, Venta, DetalleVenta, Pedido, OrdenDeCompra, Promoción) vía
`RepresentationModelAssembler` — ver `doc/consolidacion-s6/capacidades-verificadas.md` y
`specs/001-minimarket-backend-spec/spec.md` (Historia de Usuario 4) para el detalle.

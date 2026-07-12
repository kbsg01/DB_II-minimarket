# Minimarket Plus — Backend con documentación OpenAPI + HATEOAS

Sistema backend para la gestión de un minimarket (productos, categorías, carritos, inventario, ventas y usuarios con roles), desarrollado en Spring Boot 3.4.1 y documentado con **OpenAPI 3 (springdoc-openapi) + Swagger UI**, con navegación entre recursos mediante **HATEOAS (Spring HATEOAS)**.

Actividad Semana 8 — Desarrollo Backend II (PBY2202), Duoc UC. (Basado en la Semana 7: documentación OpenAPI).

## Requisitos

- Java 17 o superior (JDK)
- No requiere base de datos externa: usa H2 en memoria

## Ejecución

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

La aplicación queda disponible en `http://localhost:8080`.

## Documentación de la API

| Recurso | URL |
|---|---|
| Swagger UI (interfaz interactiva) | http://localhost:8080/swagger-ui.html |
| Contrato OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| JSON exportado (para Postman) | [`openapi/api-docs.json`](openapi/api-docs.json) |
| Consola H2 (desarrollo) | http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:testdb`, usuario `sa`, sin contraseña) |

## Autenticación

Todos los endpoints `/api/**` requieren **HTTP Basic**. En Swagger UI, presiona **Authorize** e ingresa una de las credenciales de demostración (creadas automáticamente al iniciar):

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | ROLE_ADMIN |
| `cajero1` | `cajero123` | ROLE_CAJERO |
| `cliente1` | `cliente123` | ROLE_CLIENTE |

Los endpoints `/public/**`, Swagger UI y `/v3/api-docs` son de acceso público.

## Enlaces HATEOAS y filtros de navegación

Las respuestas de recurso individual y de colección de los 7 recursos de negocio incluyen un bloque `_links` (Spring HATEOAS) con enlaces `self`, colección y relaciones entre recursos (p. ej. producto → categoría, venta → detalles). Ver el detalle completo por recurso en [`specs/001-hateoas-openapi-avanzado/contracts/hateoas-links.md`](specs/001-hateoas-openapi-avanzado/contracts/hateoas-links.md).

Los enlaces relacionados 1:N se apoyan en parámetros de consulta opcionales agregados a endpoints de colección ya existentes:

| Endpoint | Parámetro | Ejemplo |
|---|---|---|
| `GET /api/productos` | `categoriaId` | `/api/productos?categoriaId=1` |
| `GET /api/inventario` | `productoId` | `/api/inventario?productoId=1` |
| `GET /api/carritos` | `usuarioId` | `/api/carritos?usuarioId=3` |
| `GET /api/ventas` | `usuarioId` | `/api/ventas?usuarioId=3` |
| `GET /api/detalle-ventas` | `ventaId` | `/api/detalle-ventas?ventaId=1` |

## Endpoints documentados

| Recurso | Ruta base | Operaciones |
|---|---|---|
| Productos | `/api/productos` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Carritos | `/api/carritos` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Categorías | `/api/categorias` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Usuarios | `/api/usuarios` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Ventas | `/api/ventas` | GET, GET/{id}, POST |
| Detalles de venta | `/api/detalle-ventas` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Inventario | `/api/inventario` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Público | `/public/hola` | GET (sin autenticación) |

## Probar el contrato en Postman

1. Abrir Postman → **Import** → seleccionar `openapi/api-docs.json` (o la URL `http://localhost:8080/v3/api-docs`).
2. En la colección generada: pestaña **Authorization** → tipo **Basic Auth** → credenciales de demostración.
3. Ejecutar los requests y contrastar códigos de estado y cuerpos con lo documentado en Swagger UI.

## Notas técnicas

- El puerto puede cambiarse con la variable `SERVER_PORT` (ej. `SERVER_PORT=8081 ./mvnw spring-boot:run`) si el 8080 está ocupado.
- La contraseña de los usuarios nunca se retorna en las respuestas (`WRITE_ONLY`).
- Datos de demostración cargados por `com.minimarket.config.DataLoader`.

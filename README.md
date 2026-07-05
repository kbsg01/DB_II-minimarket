# Minimarket Plus — Backend con documentación OpenAPI

Sistema backend para la gestión de un minimarket (productos, categorías, carritos, inventario, ventas y usuarios con roles), desarrollado en Spring Boot 3.4.1 y documentado con **OpenAPI 3 (springdoc-openapi) + Swagger UI**.

Actividad Semana 7 — Desarrollo Backend II (PBY2202), Duoc UC.

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

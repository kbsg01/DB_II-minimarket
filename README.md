# Minimarket Plus — API REST

Sistema de gestión backend para un minimarket. Desarrollado con Spring Boot 3.4.1 como entregable académico de la asignatura Desarrollo Backend II (PBY2202), Semana 6.

## Tecnologías

- Java 17 (target) / Java 25 compatible en runtime
- Spring Boot 3.4.1 (Web, Security, Data JPA)
- JWT con JJWT 0.11.5 (HS256, 24 horas de vigencia)
- H2 in-memory (`jdbc:h2:mem:testdb`)
- JUnit 5 + Mockito para pruebas unitarias
- SpringDoc OpenAPI 2.7.0 (Swagger UI)

## Prerrequisitos

- Java 17 o superior
- Maven 3.8 o superior (el proyecto incluye `./mvnw`)

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/kbsg01/DB_II-minimarket.git
cd DB_II-minimarket
git checkout feat/microservices-junit-s6

# Compilar
./mvnw clean package -DskipTests

# Ejecutar la aplicación (puerto 8080)
./mvnw spring-boot:run
```

> **Nota para Java 25**: Si el entorno tiene Java 25 instalado, agregar `-Djacoco.skip=true` al comando de pruebas (JaCoCo 0.8.14 no soporta bytecode Java 25).

## Ejecución de pruebas

```bash
# Ejecutar todas las pruebas unitarias
./mvnw test

# Con Java 25 (evita error de JaCoCo)
./mvnw test -Djacoco.skip=true

# Ejecutar una clase específica
./mvnw test -Dtest=ProductoServiceTest

# Ejecutar un método específico
./mvnw test -Dtest=UsuarioServiceTest#puedeRegistrarVenta_rolValido_retornaTrue
```

Resultado esperado: `Tests run: 66, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`

Los reportes XML se generan en `target/surefire-reports/`.

## Estructura del proyecto

```
src/
├── main/java/com/minimarket/
│   ├── config/          # DataInitializer, OpenApiConfig
│   ├── controller/      # 9 RestControllers con anotaciones Swagger
│   ├── entity/          # Entidades JPA
│   ├── exception/       # DatosIncompletosException, StockInsuficienteException
│   ├── repository/      # Interfaces Spring Data JPA
│   ├── security/        # JWT filter, SecurityConfig, CustomUserDetails
│   └── service/         # Interfaces e implementaciones de servicio
└── test/java/com/minimarket/service/
    ├── CarritoServiceTest.java   (14 pruebas)
    ├── InventarioServiceTest.java (13 pruebas)
    ├── ProductoServiceTest.java   (12 pruebas)
    ├── UsuarioServiceTest.java    (13 pruebas)
    ├── UsuarioTest.java           (3 pruebas)
    └── VentaServiceTest.java      (11 pruebas)
```

## Endpoints disponibles

| Recurso | Ruta base | Roles requeridos |
|---------|-----------|-----------------|
| Autenticacion | `POST /api/auth/login` | Publico |
| Categorias | `/api/categorias` | Autenticado / ADMIN para escritura |
| Productos | `/api/productos` | Autenticado / ADMIN para escritura |
| Inventario | `/api/inventario` | CAJERO, ADMIN |
| Carrito | `/api/carrito` | Autenticado |
| Ventas | `/api/ventas` | CAJERO (crear), Autenticado (leer) |
| Detalle Venta | `/api/detalle-ventas` | Autenticado |
| Usuarios | `/api/usuarios` | ADMIN |
| Public | `GET /public/hola` | Sin autenticacion |

## Autenticacion con JWT

La API usa tokens JWT. Para autenticarse:

**1. Obtener token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Respuesta:
```json
{
  "token": "<JWT>",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

**2. Usar token en peticiones protegidas:**
```bash
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/productos
```

**Usuarios pre-cargados:**

| Usuario | Contrasena | Rol |
|---------|-----------|-----|
| admin | admin123 | ROLE_ADMIN |
| cajero | cajero123 | ROLE_CAJERO |
| cliente | cliente123 | ROLE_CLIENTE |

## Documentacion Swagger UI

Con la aplicacion en ejecucion, acceder a:

```
http://localhost:8080/swagger-ui/index.html
```

Hacer clic en "Authorize", ingresar el token JWT en el campo Bearer y explorar los 9 grupos de endpoints documentados.

## Repositorio GitHub

Rama activa: `feat/microservices-junit-s6`

URL: https://github.com/kbsg01/DB_II-minimarket

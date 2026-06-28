# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=UsuarioTest

# Run a single test method
./mvnw test -Dtest=UsuarioTest#testCrearUsuario
```

## Architecture

This is a Spring Boot 3.4.1 REST API (Java 17) for a minimarket management system. It uses H2 in-memory database (`jdbc:h2:mem:testdb`) with JPA/Hibernate (`ddl-auto=update`). Lombok is available but entities currently use manual getters/setters.

### Layer structure

`Controller → Service (interface + impl) → Repository → Entity`

- `entity/` — JPA entities
- `repository/` — Spring Data JPA repositories
- `service/` — interfaces; `service/impl/` — implementations
- `controller/` — `@RestController` classes under `/api/**`
- `security/` — Spring Security config, JWT stub, and custom `UserDetails`

### API endpoints

| Resource | Base path |
| --- | --- |
| Categorías | `GET/POST /api/categorias`, `GET/PUT/DELETE /api/categorias/{id}` |
| Productos | `GET/POST /api/productos`, `GET/PUT/DELETE /api/productos/{id}` |
| Inventario | `GET/POST /api/inventario`, `GET/PUT/DELETE /api/inventario/{id}` |
| Carrito | `GET/POST /api/carrito`, `GET/PUT/DELETE /api/carrito/{id}` |
| Ventas | `GET/POST /api/ventas`, `GET /api/ventas/{id}` |
| DetalleVenta | `GET/POST /api/detalle-ventas`, `GET /api/detalle-ventas/{id}` |
| Usuarios | `GET/POST /api/usuarios`, `GET/PUT/DELETE /api/usuarios/{id}` |
| Public | `GET /public/hola` (no auth required) |

All `/api/**` routes require authentication; only `/public/**` is open.

### Domain model relationships

```text
Categoria ←── Producto ──→ Inventario
                  │
     Carrito ─────┤
                  │
     Venta ───────┤
       └── DetalleVenta (captures precio snapshot at sale time)

Usuario ←── Carrito
Usuario ←── Venta
Usuario ↔── Rol (ManyToMany via usuario_roles join table)
```

`Producto` holds a live `stock` field; `DetalleVenta` captures `precio` at sale time (denormalized snapshot, not a reference to current product price).

### Security

`SecurityConfig` uses form login. Routes under `/public/**` are open; everything else requires authentication. `CustomUserDetailsService` loads users from the DB by username; `CustomUserDetails` wraps `Usuario` for Spring Security. `JwtUtil` is a placeholder with no implementation yet.

**Known gaps:**

- `UsuarioController.save()` does **not** BCrypt-encode the password before persisting — encoding must be added before storing users.
- The H2 console (`/h2-console`) is not in the permit list and Spring Security's default frame-options block will prevent it from rendering. To use it, either add `.requestMatchers("/h2-console/**").permitAll()` and `.headers(h -> h.frameOptions(f -> f.disable()))` to `SecurityConfig`, or access it after logging in via form login first.

### Key patterns

- Services are injected via `@Autowired` on the field (not constructor injection).
- No DTOs — controllers receive and return entity objects directly.
- No global exception handler — 404 handling is done per-controller. Some controllers use `Optional` (e.g. `UsuarioController`); others use null checks (e.g. `ProductoController`, `VentaController`).
- `UsuarioTest` is a plain unit test (no Spring context); `MinimarketApplicationTests` is a `@SpringBootTest` context load test.

<!-- SPECKIT START -->
## Active Feature Plan

Current feature: **S6 Pruebas Unitarias en Microservicios**
Plan: `specs/001-s6-pruebas-microservicios/plan.md`
Branch: `feat/microservices-junit-s6`
<!-- SPECKIT END -->

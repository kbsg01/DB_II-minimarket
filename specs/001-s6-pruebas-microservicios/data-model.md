# Data Model: Minimarket Plus

**Feature**: S6 Pruebas Unitarias en Microservicios
**Date**: 2026-06-28

---

## Entity Relationship Overview

```
Categoria (1) ──< (N) Producto (1) ──< (N) Inventario
                          │
                          │ (N)
                   Carrito (1) >── (N) Carrito_Items
                          │
                     Usuario (1)
                          │
                   Venta (1) >── (N) DetalleVenta
                          │
                     Usuario (N) >──< Rol  [usuario_roles join]
```

---

## Entities

### Categoria
| Field       | Type    | Constraints             | Notes                     |
|-------------|---------|-------------------------|---------------------------|
| id          | Long    | PK, auto-generated      |                           |
| nombre      | String  | NOT NULL                |                           |
| descripcion | String  | nullable                |                           |

**Relationships**:
- One-to-Many → Producto

---

### Producto
| Field      | Type     | Constraints        | Notes                              |
|------------|----------|--------------------|------------------------------------|
| id         | Long     | PK, auto-generated |                                    |
| nombre     | String   | NOT NULL           |                                    |
| precio     | Double   | NOT NULL           | Current sale price                 |
| stock      | Integer  | NOT NULL           | Live count; decremented on Venta   |
| categoria  | Categoria| FK, nullable       | Many-to-One                        |

**Relationships**:
- Many-to-One → Categoria
- One-to-Many → Inventario
- Referenced by Carrito and DetalleVenta

**Business rules**:
- Only ROLE_ADMIN may create, update, or delete Producto.
- `stock` must be >= 0; it is decremented when a Venta is confirmed and incremented on Inventario entry movement.

---

### Inventario
| Field      | Type    | Constraints        | Notes                                  |
|------------|---------|--------------------|----------------------------------------|
| id         | Long    | PK, auto-generated |                                        |
| producto   | Producto| FK, NOT NULL       | Many-to-One                            |
| cantidad   | Integer | NOT NULL           | Positive = entry, negative = exit      |
| tipo       | String  | NOT NULL           | "ENTRADA" or "SALIDA"                  |
| fecha      | Date    | NOT NULL           | Movement date                          |

**Relationships**:
- Many-to-One → Producto

**Business rules**:
- ROLE_ADMIN or ROLE_CAJERO may register movements.
- An ENTRADA movement must increase `Producto.stock`; a SALIDA must decrease it.
- A SALIDA movement is rejected if it would bring `Producto.stock` below 0.

---

### Usuario
| Field     | Type      | Constraints             | Notes                        |
|-----------|-----------|-------------------------|------------------------------|
| id        | Long      | PK, auto-generated      |                              |
| username  | String    | NOT NULL, UNIQUE        |                              |
| password  | String    | NOT NULL                | BCrypt-encoded; never plain  |
| nombre    | String    | nullable                | Required by `datosCompletos` |
| apellido  | String    | nullable                | Required by `datosCompletos` |
| email     | String    | nullable                | Required by `datosCompletos` |
| direccion | String    | nullable                | Required by `datosCompletos` |
| roles     | Set\<Rol\>| Many-to-Many            | Eager fetch; join via `usuario_roles` |

**Relationships**:
- Many-to-Many → Rol (join table: `usuario_roles(usuario_id, rol_id)`)
- One-to-Many → Carrito
- One-to-Many → Venta

**Business rules**:
- `datosCompletos(usuario)` returns true only when all of: username, nombre, apellido, email, direccion are non-null and non-empty.
- `registrar(usuario)` throws `DatosIncompletosException` if `datosCompletos` is false.
- `puedeRegistrarVenta(usuario)` returns true for ROLE_ADMIN or ROLE_VENDEDOR.
- Only ROLE_ADMIN may manage users via the API.

---

### Rol
| Field  | Type   | Constraints        | Notes                             |
|--------|--------|--------------------|-----------------------------------|
| id     | Long   | PK, auto-generated |                                   |
| nombre | String | NOT NULL           | e.g., "ROLE_ADMIN", "ROLE_CAJERO" |

**Recognized values**: ROLE_ADMIN, ROLE_CAJERO, ROLE_CLIENTE

---

### Carrito
| Field    | Type    | Constraints        | Notes                          |
|----------|---------|--------------------|--------------------------------|
| id       | Long    | PK, auto-generated |                                |
| usuario  | Usuario | FK, NOT NULL       | Many-to-One                    |
| producto | Producto| FK, NOT NULL       | Many-to-One                    |
| cantidad | Integer | NOT NULL, > 0      |                                |

**Repository method**: `findByUsuarioId(Long)`, `findByUsuarioIdAndProductoId(Long, Long)`

**Business rules**:
- `agregarProducto(usuarioId, productoId, cantidad)`: cantidad must be > 0 (`DatosIncompletosException` otherwise); `Producto.stock` must be >= cantidad (`StockInsuficienteException` otherwise).
- If an entry for the same (usuario, producto) already exists, the quantity is incremented (not duplicated).
- ROLE_CLIENTE may add/view their own cart; ROLE_CAJERO may view all carts.

---

### Venta
| Field         | Type      | Constraints        | Notes                         |
|---------------|-----------|--------------------|-------------------------------|
| id            | Long      | PK, auto-generated |                               |
| usuario       | Usuario   | FK, NOT NULL       | The cajero who registered it  |
| fecha         | Date      | NOT NULL           |                               |
| total         | Double    | NOT NULL           | Sum of all DetalleVenta totals|
| detalles      | List\<DetalleVenta\> | One-to-Many |                   |

**Business rules**:
- Only ROLE_CAJERO may create a Venta.
- `calcularTotal(venta)` sums `detalle.precio * detalle.cantidad` for each DetalleVenta.
- `registrarVenta(venta)` throws `StockInsuficienteException` if any product in the sale has insufficient stock.

---

### DetalleVenta
| Field    | Type    | Constraints        | Notes                                    |
|----------|---------|--------------------|------------------------------------------|
| id       | Long    | PK, auto-generated |                                          |
| venta    | Venta   | FK, NOT NULL       | Many-to-One                              |
| producto | Producto| FK, NOT NULL       | Many-to-One                              |
| cantidad | Integer | NOT NULL           |                                          |
| precio   | Double  | NOT NULL           | Snapshot of price at time of sale; never references current Producto.precio |

**Invariant**: `precio` is set at sale time and must never be updated after the Venta is confirmed.

---

## State Transitions

### Producto.stock

```
[initial: N]
    ├─── Inventario ENTRADA (cantidad=X) ──→ [N + X]
    ├─── Inventario SALIDA (cantidad=X)  ──→ [N - X]  (rejected if N - X < 0)
    └─── Venta confirmada (X units sold) ──→ [N - X]  (rejected if N < X)
```

### Carrito item

```
[no entry] ──→ agregarProducto ──→ [entry with cantidad=K]
                                        │
                              agregarProducto again ──→ [cantidad = K + M]
                                        │
                              Venta confirmada ──→ [entry removed or kept]
```

---

## Custom Exceptions

| Exception                   | Thrown by                        | HTTP mapping (when applicable) |
|-----------------------------|----------------------------------|-------------------------------|
| `DatosIncompletosException` | `UsuarioServiceImpl.registrar()`, `CarritoServiceImpl.agregarProducto()` | 400 Bad Request |
| `StockInsuficienteException`| `CarritoServiceImpl.agregarProducto()`, `VentaServiceImpl.registrarVenta()` | 409 Conflict |

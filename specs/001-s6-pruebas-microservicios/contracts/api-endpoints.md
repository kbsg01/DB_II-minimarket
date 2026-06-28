# API Contract: Minimarket Plus REST API

**Base URL**: `http://localhost:8080`
**Auth**: Bearer JWT — obtain token from `POST /api/auth/login`
**Content-Type**: `application/json`

---

## Authentication

### POST /api/auth/login *(public — no token required)*

Authenticate and obtain a JWT.

**Request body**:
```json
{ "username": "admin", "password": "admin123" }
```

**Response 200**:
```json
{
  "token": "<JWT string>",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

**Response 401** — invalid credentials:
```json
{ "error": "Credenciales inválidas" }
```

---

## Public

### GET /public/hola *(no auth required)*

Health-check endpoint.

**Response 200**: `"Hola Mundo desde el Minimarket!"`

---

## Categorias — `/api/categorias`

*Required role*: any authenticated user (read); ROLE_ADMIN (write)

| Method | Path                  | Description                   |
|--------|-----------------------|-------------------------------|
| GET    | /api/categorias       | List all categories            |
| POST   | /api/categorias       | Create category                |
| GET    | /api/categorias/{id}  | Get category by ID             |
| PUT    | /api/categorias/{id}  | Update category                |
| DELETE | /api/categorias/{id}  | Delete category                |

**Categoria schema**:
```json
{ "id": 1, "nombre": "Lácteos", "descripcion": "Productos lácteos" }
```

---

## Productos — `/api/productos`

*Required role*: any authenticated (GET); ROLE_ADMIN (POST, PUT, DELETE)

| Method | Path                 | Description              |
|--------|----------------------|--------------------------|
| GET    | /api/productos       | List all products         |
| POST   | /api/productos       | Create product            |
| GET    | /api/productos/{id}  | Get product by ID         |
| PUT    | /api/productos/{id}  | Update product            |
| DELETE | /api/productos/{id}  | Delete product            |

**Producto schema**:
```json
{ "id": 1, "nombre": "Leche 1L", "precio": 950.0, "stock": 100, "categoria": { "id": 1 } }
```

---

## Inventario — `/api/inventario`

*Required role*: ROLE_ADMIN or ROLE_CAJERO

| Method | Path                    | Description                  |
|--------|-------------------------|------------------------------|
| GET    | /api/inventario         | List all inventory movements  |
| POST   | /api/inventario         | Register movement             |
| GET    | /api/inventario/{id}    | Get movement by ID            |
| PUT    | /api/inventario/{id}    | Update movement               |
| DELETE | /api/inventario/{id}    | Delete movement               |

**Inventario schema**:
```json
{ "id": 1, "producto": { "id": 1 }, "cantidad": 50, "tipo": "ENTRADA", "fecha": "2026-06-28" }
```

---

## Carrito — `/api/carrito`

*Required role*: any authenticated user

| Method | Path              | Description           |
|--------|-------------------|-----------------------|
| GET    | /api/carrito      | List all cart items    |
| POST   | /api/carrito      | Add item to cart       |
| GET    | /api/carrito/{id} | Get cart item by ID    |
| PUT    | /api/carrito/{id} | Update cart item       |
| DELETE | /api/carrito/{id} | Remove item from cart  |

**Carrito schema**:
```json
{ "id": 1, "usuario": { "id": 2 }, "producto": { "id": 1 }, "cantidad": 3 }
```

**Errors**:
- `DatosIncompletosException` → 400 if `cantidad <= 0`
- `StockInsuficienteException` → 409 if `producto.stock < cantidad`

---

## Ventas — `/api/ventas`

*Required role*: ROLE_CAJERO (create); any authenticated (read)

| Method | Path              | Description          |
|--------|-------------------|----------------------|
| GET    | /api/ventas       | List all sales        |
| POST   | /api/ventas       | Register new sale     |
| GET    | /api/ventas/{id}  | Get sale by ID        |

**Venta schema**:
```json
{
  "id": 1,
  "usuario": { "id": 2 },
  "fecha": "2026-06-28",
  "total": 2850.0,
  "detalles": [
    { "producto": { "id": 1 }, "cantidad": 3, "precio": 950.0 }
  ]
}
```

**Errors**:
- `StockInsuficienteException` → 409 if any product has insufficient stock

---

## DetalleVenta — `/api/detalle-ventas`

*Required role*: any authenticated user

| Method | Path                    | Description                |
|--------|-------------------------|----------------------------|
| GET    | /api/detalle-ventas     | List all sale line items    |
| POST   | /api/detalle-ventas     | Create sale line item       |
| GET    | /api/detalle-ventas/{id}| Get line item by ID         |

**DetalleVenta schema**:
```json
{ "id": 1, "venta": { "id": 1 }, "producto": { "id": 1 }, "cantidad": 3, "precio": 950.0 }
```

---

## Usuarios — `/api/usuarios`

*Required role*: ROLE_ADMIN

| Method | Path                | Description        |
|--------|---------------------|--------------------|
| GET    | /api/usuarios       | List all users      |
| POST   | /api/usuarios       | Create user         |
| GET    | /api/usuarios/{id}  | Get user by ID      |
| PUT    | /api/usuarios/{id}  | Update user         |
| DELETE | /api/usuarios/{id}  | Delete user         |

**Usuario schema** (password not returned in GET responses):
```json
{
  "id": 1,
  "username": "admin",
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana@minimarket.cl",
  "direccion": "Av. Principal 123",
  "roles": [{ "id": 1, "nombre": "ROLE_ADMIN" }]
}
```

**Notes**:
- Password is BCrypt-encoded before persistence; never stored or returned in plain text.
- `DatosIncompletosException` thrown if any required field (username, nombre, apellido, email, direccion) is missing.

---

## Error Response Format

Currently errors are returned per-controller (no global handler). Common patterns:

| Status | Scenario                                 |
|--------|------------------------------------------|
| 200    | Success                                  |
| 201    | Resource created (some controllers)      |
| 400    | Incomplete data (`DatosIncompletosException`) |
| 401    | Missing or invalid JWT                   |
| 403    | Authenticated but insufficient role      |
| 404    | Resource not found (returns `null` or `404 Not Found`) |
| 409    | Insufficient stock (`StockInsuficienteException`) |

---

## Swagger UI

Accessible at: `http://localhost:8080/swagger-ui/index.html`

To test protected endpoints in Swagger UI:
1. Expand `POST /api/auth/login` and execute with valid credentials.
2. Copy the `token` value from the response.
3. Click the "Authorize" button (lock icon) at the top of the page.
4. In the "BearerAuth" field, paste the token value (without "Bearer " prefix — Swagger adds it).
5. Click "Authorize" and "Close".
6. All subsequent requests from the UI will include the Authorization header.

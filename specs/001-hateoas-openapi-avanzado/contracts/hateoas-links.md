# Contrato de enlaces HATEOAS por recurso

Todas las respuestas de los endpoints `GET` de recurso individual y colección de los 7 controladores de negocio incluyen un bloque `_links` (formato estándar de Spring HATEOAS). Las respuestas siguen requiriendo autenticación HTTP Basic tal como hoy (sin cambios en `SecurityConfig`).

## Producto — `/api/productos`

**GET /api/productos/{id}** → `EntityModel<Producto>`
```json
{
  "id": 1,
  "nombre": "Coca-Cola 1.5L",
  "precio": 1890,
  "stock": 24,
  "categoria": { "id": 1, "nombre": "Bebidas" },
  "_links": {
    "self": { "href": "http://localhost:8080/api/productos/1" },
    "productos": { "href": "http://localhost:8080/api/productos" },
    "categoria": { "href": "http://localhost:8080/api/categorias/1" },
    "inventario": { "href": "http://localhost:8080/api/inventario?productoId=1" }
  }
}
```

**GET /api/productos** → `CollectionModel<EntityModel<Producto>>` (soporta `?categoriaId=`)
- `_links.self` → `/api/productos` (o `/api/productos?categoriaId={id}` si se filtró)
- Cada elemento embebido conserva su propio `_links.self`

## Categoria — `/api/categorias`

**GET /api/categorias/{id}**
- `_links.self` → `/api/categorias/{id}`
- `_links.categorias` → `/api/categorias`
- `_links.productos` → `/api/productos?categoriaId={id}`

## Carrito — `/api/carritos`

**GET /api/carritos/{id}**
- `_links.self` → `/api/carritos/{id}`
- `_links.carritos` → `/api/carritos`
- `_links.producto` → `/api/productos/{productoId}`
- `_links.usuario` → `/api/usuarios/{usuarioId}`

**GET /api/carritos** soporta `?usuarioId=`

## Inventario — `/api/inventario`

**GET /api/inventario/{id}**
- `_links.self` → `/api/inventario/{id}`
- `_links.inventario` → `/api/inventario`
- `_links.producto` → `/api/productos/{productoId}`

**GET /api/inventario** soporta `?productoId=`

## Usuario — `/api/usuarios`

**GET /api/usuarios/{id}**
- `_links.self` → `/api/usuarios/{id}`
- `_links.usuarios` → `/api/usuarios`
- `_links.carritos` → `/api/carritos?usuarioId={id}`
- `_links.ventas` → `/api/ventas?usuarioId={id}`

## Venta — `/api/ventas`

**GET /api/ventas/{id}**
- `_links.self` → `/api/ventas/{id}`
- `_links.ventas` → `/api/ventas`
- `_links.usuario` → `/api/usuarios/{usuarioId}`
- `_links.detalles` → `/api/detalle-ventas?ventaId={id}`

**GET /api/ventas** soporta `?usuarioId=`

## DetalleVenta — `/api/detalle-ventas`

**GET /api/detalle-ventas/{id}**
- `_links.self` → `/api/detalle-ventas/{id}`
- `_links.detalle-ventas` → `/api/detalle-ventas`
- `_links.venta` → `/api/ventas/{ventaId}`
- `_links.producto` → `/api/productos/{productoId}`

**GET /api/detalle-ventas** soporta `?ventaId=`

## Reglas transversales

1. Los endpoints `POST`/`PUT` también devuelven el recurso envuelto en `EntityModel` con sus `_links`, para que el cliente pueda seguir navegando inmediatamente tras crear/actualizar.
2. `DELETE` sigue devolviendo `204 No Content` sin cuerpo (sin cambios).
3. Todas las respuestas `404`/`401` documentadas en OpenAPI se mantienen sin cambios de contrato.
4. El JSON exportado desde `/v3/api-docs` debe reflejar el nuevo `schema` de cada operación `GET`/`POST`/`PUT` como el modelo envolvente correspondiente.

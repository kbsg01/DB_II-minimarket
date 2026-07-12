# Data Model: Documentación Avanzada OpenAPI y HATEOAS

No se agregan ni modifican entidades JPA — el modelo de datos persistente ya existe (S1–S7) y no cambia. Esta feature agrega una capa de **modelos de representación** (`EntityModel`/`CollectionModel`) sobre las entidades ya existentes, y expone parámetros de filtro opcionales sobre relaciones ya presentes como claves foráneas.

## Entidades existentes (sin cambios de esquema)

| Entidad | Atributos clave | Relaciones (FK) |
|---|---|---|
| `Producto` | id, nombre, precio, stock | `categoria` (N:1 → Categoria) |
| `Categoria` | id, nombre | `productos` (1:N ← Producto, `@JsonIgnore`) |
| `Carrito` | id, cantidad | `usuario` (N:1 → Usuario), `producto` (N:1 → Producto) |
| `Inventario` | id, cantidad, tipoMovimiento, fechaMovimiento | `producto` (N:1 → Producto) |
| `Usuario` | id, username, password (write-only) | `roles` (N:N → Rol) |
| `Venta` | id, fecha | `usuario` (N:1 → Usuario), `detalles` (1:N → DetalleVenta) |
| `DetalleVenta` | id, cantidad, precio | `venta` (N:1 → Venta), `producto` (N:1 → Producto) |

## Nuevos parámetros de filtro sobre endpoints de colección existentes

Ninguno requiere cambios de persistencia: reutilizan métodos de repositorio ya existentes.

| Endpoint | Parámetro nuevo (opcional) | Repositorio reutilizado | Comportamiento sin parámetro |
|---|---|---|---|
| `GET /api/productos` | `categoriaId` | `ProductoRepository.findByCategoriaId` | Lista completa (sin cambios) |
| `GET /api/inventario` | `productoId` | `InventarioRepository.findByProductoId` | Lista completa (sin cambios) |
| `GET /api/detalle-ventas` | `ventaId` | `DetalleVentaRepository.findByVentaId` | Lista completa (sin cambios) |
| `GET /api/carritos` | `usuarioId` | `CarritoRepository.findByUsuarioId` | Lista completa (sin cambios) |
| `GET /api/ventas` | `usuarioId` | `VentaRepository.findByUsuarioId` | Lista completa (sin cambios) |

## Modelos de representación (nuevos, no persistentes)

Cada uno es un `EntityModel<T>` de la entidad correspondiente, sin campos adicionales propios más allá de los enlaces `_links` estándar de Spring HATEOAS. Ver [contracts/hateoas-links.md](./contracts/hateoas-links.md) para el detalle de relaciones (`rel`) y URIs por entidad.

- `EntityModel<Producto>`, `CollectionModel<EntityModel<Producto>>`
- `EntityModel<Categoria>`, `CollectionModel<EntityModel<Categoria>>`
- `EntityModel<Carrito>`, `CollectionModel<EntityModel<Carrito>>`
- `EntityModel<Inventario>`, `CollectionModel<EntityModel<Inventario>>`
- `EntityModel<Usuario>`, `CollectionModel<EntityModel<Usuario>>`
- `EntityModel<Venta>`, `CollectionModel<EntityModel<Venta>>`
- `EntityModel<DetalleVenta>`, `CollectionModel<EntityModel<DetalleVenta>>`

## Reglas de inclusión de enlaces (derivadas de FR-005/FR-006 del spec)

- Un enlace de relación N:1 (p. ej. `producto → categoria`) se agrega siempre que la FK no sea nula (ya es `nullable = false` en todas las entidades relevantes, por lo que en la práctica siempre está presente).
- Un enlace de relación 1:N filtrada (p. ej. `categoria → productos`) se agrega siempre como URI de colección filtrada; no depende de que existan resultados (una colección vacía es una respuesta válida, no un enlace roto).
- No se agregan enlaces hacia recursos que representen una relación inexistente en el modelo de datos actual (p. ej. no hay enlace `carrito → venta`, porque el modelo no relaciona directamente carrito con venta).

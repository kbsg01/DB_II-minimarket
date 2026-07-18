# Data Model: Backend MiniMarket Plus (EFT S9)

Entidades derivadas de `spec.md` → Key Entities, contrastadas contra el modelo JPA ya
existente en `src/main/java/com/minimarket/entity/`. Se indica para cada una si ya existe,
si requiere cambios, o si es nueva.

## Usuario *(existente — requiere ajuste)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK autogenerado |
| username | String | único, not null |
| password | String | hash BCrypt, not null; **MUST NOT** aparecer en respuestas de la API (FR-004) |
| roles | Set\<Rol\> | relación many-to-many existente |

**Cambio requerido**: ninguno en el esquema; el cambio está en la capa de presentación
(DTO/EntityModel) para excluir `password`.

## Rol *(existente)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| nombre | String | único; valores esperados: `CLIENTE`, `CAJERO`, `JEFE_TURNO`, `REPONEDOR`, `ASISTENTE_SERVICIO_CLIENTE`, `GERENTE_SUCURSAL`, `ADMINISTRADOR` |

## Sucursal *(nueva)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK autogenerado |
| nombre | String | not null |
| direccion | String | not null |
| region | String | opcional, para reportes agregados por región |

**Relaciones**: referenciada por `Inventario`, `OrdenDeCompra` y `Pedido`.

## Categoria *(existente)*

Sin cambios: `id`, `nombre`.

## Producto *(existente — requiere ajuste)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| nombre | String | not null |
| precio | Double | not null; precio base antes de promociones |
| categoria | Categoria | not null |
| stockMinimo | Integer | **nuevo campo**; nivel mínimo que dispara `OrdenDeCompra` (FR-006) |
| proveedor | Proveedor | **nuevo campo**; proveedor por defecto para reposición |

**Cambio requerido**: el campo `stock` actual deja de ser la fuente de verdad del stock
(pasa a calcularse desde `Inventario` por sucursal, ver Decisión 5 en research.md); se
recomienda mantenerlo solo como caché opcional o retirarlo para evitar inconsistencias.

## Inventario *(existente — requiere ajuste)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| producto | Producto | not null |
| sucursal | Sucursal | **nuevo campo**, not null |
| cantidad | Integer | not null |
| tipoMovimiento | String | `ENTRADA` \| `SALIDA` |
| fechaMovimiento | Date | not null |

**Regla de negocio**: el stock vigente de un producto en una sucursal es la suma de
`cantidad` de movimientos `ENTRADA` menos `SALIDA` para ese par producto+sucursal.

## Proveedor *(nueva)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| nombre | String | not null |
| contacto | String | teléfono o correo de contacto |

## OrdenDeCompra *(nueva)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| producto | Producto | not null |
| sucursal | Sucursal | not null |
| proveedor | Proveedor | not null |
| cantidadSolicitada | Integer | not null |
| estado | String | `PENDIENTE` \| `RECIBIDA` \| `CANCELADA` |
| fechaGeneracion | Date | not null, generada automáticamente por el sistema |

**Regla de negocio**: no se genera una nueva orden si ya existe una en estado
`PENDIENTE` para el mismo producto+sucursal+proveedor (evita duplicados ante movimientos
concurrentes, ver Edge Cases en spec.md).

## Promocion *(nueva)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| producto | Producto | not null |
| descuentoPorcentaje | Double | 0 < valor <= 100 |
| fechaInicio | Date | not null |
| fechaFin | Date | not null, MUST ser posterior a `fechaInicio` |

**Regla de negocio**: una promoción está vigente si la fecha actual está entre
`fechaInicio` y `fechaFin`; el precio final aplicado en `Pedido`/`Venta` MUST reflejar el
descuento vigente más reciente si existe más de una promoción activa para el mismo
producto.

## Pedido *(nueva)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| usuario | Usuario | cliente que genera el pedido, not null |
| sucursal | Sucursal | origen del stock, not null |
| modoEntrega | String | `RETIRO_TIENDA` \| `DESPACHO_DOMICILIO` |
| estado | String | `PENDIENTE` \| `CONFIRMADO` \| `RECHAZADO_SIN_STOCK` \| `ENTREGADO` |
| fechaCreacion | Date | not null |
| detalles | List\<DetallePedido\> | productos y cantidades solicitadas |

**Regla de negocio**: al confirmar, se revalida disponibilidad de stock en la `Sucursal`
indicada (FR-010); si no alcanza, el pedido pasa a `RECHAZADO_SIN_STOCK` y no descuenta
inventario.

## DetallePedido *(nueva, análoga a DetalleVenta)*

| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| pedido | Pedido | not null |
| producto | Producto | not null |
| cantidad | Integer | not null, > 0 |
| precioAplicado | Double | precio final tras promoción vigente |

## Venta *(existente)*

Sin cambios estructurales: `id`, `usuario`, `fecha`, `detalles` (`List<DetalleVenta>`).
Puede originarse directamente en tienda o a partir de un `Pedido` confirmado.

## DetalleVenta *(existente)*

Sin cambios: `id`, `venta`, `producto`, `cantidad`, `precio`.

## Carrito *(existente)*

Sin cambios: `id`, `usuario`, `producto`, `cantidad`. Representa la selección temporal
previa a convertirse en `Pedido` o `Venta`.

## Diagrama de relaciones (resumen textual)

```text
Usuario *-* Rol
Usuario 1-* Pedido
Usuario 1-* Venta
Usuario 1-* Carrito

Sucursal 1-* Inventario
Sucursal 1-* OrdenDeCompra
Sucursal 1-* Pedido

Categoria 1-* Producto
Producto 1-* Inventario
Producto 1-* OrdenDeCompra
Producto 1-* Promocion
Producto 1-* Carrito
Producto 1-* DetalleVenta
Producto 1-* DetallePedido
Producto *-1 Proveedor (proveedor por defecto)

Proveedor 1-* OrdenDeCompra

Pedido 1-* DetallePedido
Venta 1-* DetalleVenta
```

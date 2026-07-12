# Phase 0 Research: Documentación Avanzada OpenAPI y HATEOAS

## 1. Librería para implementar HATEOAS

**Decision**: Usar Spring HATEOAS (`org.springframework.boot:spring-boot-starter-hateoas`), con `EntityModel<T>` para recursos individuales y `CollectionModel<EntityModel<T>>` para colecciones, construidos mediante `RepresentationModelAssembler<T, EntityModel<T>>` (uno por entidad), y enlaces generados con `WebMvcLinkBuilder.linkTo(methodOn(...))`.

**Rationale**: Es la implementación de referencia de Spring para HATEOAS, se integra de forma nativa con Spring MVC y con springdoc-openapi (que documenta automáticamente el bloque `_links` cuando detecta `RepresentationModel`), evitando construir a mano un formato de hipermedia propio. Además `linkTo(methodOn(...))` genera URIs type-safe a partir de los propios métodos del controlador, reduciendo el riesgo de enlaces rotos si cambian las rutas.

**Alternatives considered**:
- Construir manualmente un `Map<String, String>` de enlaces dentro del DTO de respuesta: rechazado porque no sigue un formato estándar de hipermedia, complica el testing de consistencia y no se integra con la documentación automática de OpenAPI.
- Usar JSON:API o HAL-FORMS completo con afordances de formulario: rechazado por ser más complejo de lo que pide la pauta (que solo exige enlaces dinámicos de navegación, no formularios auto-descriptivos).

## 2. Patrón de ensamblado de enlaces por recurso

**Decision**: Un `RepresentationModelAssembler` dedicado por entidad (`ProductoModelAssembler`, `CategoriaModelAssembler`, etc.) inyectado en el controlador correspondiente, responsable de envolver la entidad y agregar tanto el enlace `self` (heredado de `SimpleRepresentationModelAssembler`) como los enlaces de relación específicos de esa entidad.

**Rationale**: Mantiene los controladores delgados (ya tienen ~130 líneas cada uno con la documentación OpenAPI existente), concentra la lógica de construcción de enlaces en un solo lugar por entidad, y es el patrón recomendado por la documentación oficial de Spring HATEOAS para evitar duplicar código de enlaces en cada método de controlador.

**Alternatives considered**: Construir los enlaces inline en cada método de controlador — rechazado por duplicación (cada entidad tiene métodos GET individual, GET colección, POST, PUT que necesitarían el mismo enlace `self`).

## 3. Enlaces relacionados entre recursos

**Decision**: Reutilizar los métodos de repositorio "finder" que ya existen en el código pero no se usan (`ProductoRepository.findByCategoriaId`, `InventarioRepository.findByProductoId`, `DetalleVentaRepository.findByVentaId`, `CarritoRepository.findByUsuarioId`, `VentaRepository.findByUsuarioId`), exponiéndolos como parámetro de consulta **opcional** en el endpoint de colección ya existente del recurso relacionado (p. ej. `GET /api/inventario?productoId={id}`), en vez de crear rutas anidadas nuevas (`/api/productos/{id}/inventario`).

**Rationale**: Mantiene el espacio de URIs plano y consistente con el resto del proyecto (todas las rutas ya son `/api/<recurso>`), no introduce endpoints duplicados para lo mismo, y aprovecha código que ya existe en los repositorios sin usar — reduciendo el riesgo de tener que tocar la capa de persistencia.

**Alternatives considered**: Rutas anidadas tipo `/api/productos/{id}/inventario` — rechazado por duplicar funcionalidad con el `GET /api/inventario` existente y requerir mantener dos formas de obtener lo mismo.

## 4. Relaciones incluidas por entidad

**Decision**:
- **Producto** → `self`, `productos` (colección), `categoria` (`/api/categorias/{id}`), `inventario` (`/api/inventario?productoId={id}`)
- **Categoria** → `self`, `categorias` (colección), `productos` (`/api/productos?categoriaId={id}`)
- **Carrito** (ítem) → `self`, `carritos` (colección), `producto` (`/api/productos/{id}`), `usuario` (`/api/usuarios/{id}`)
- **Inventario** (movimiento) → `self`, `inventario` (colección), `producto` (`/api/productos/{id}`)
- **Usuario** → `self`, `usuarios` (colección), `carritos` (`/api/carritos?usuarioId={id}`), `ventas` (`/api/ventas?usuarioId={id}`)
- **Venta** → `self`, `ventas` (colección), `usuario` (`/api/usuarios/{id}`), `detalles` (`/api/detalle-ventas?ventaId={id}`)
- **DetalleVenta** → `self`, `detalle-ventas` (colección), `venta` (`/api/ventas/{id}`), `producto` (`/api/productos/{id}`)

**Rationale**: Cubre todas las relaciones de negocio reales del modelo de datos (FK explícitas en las entidades JPA), cumpliendo FR-005 del spec sin inventar relaciones artificiales. Las relaciones N:1 (p. ej. Producto→Categoria) enlazan directo al recurso padre por ID; las relaciones 1:N (p. ej. Categoria→Productos) enlazan a la colección filtrada.

**Alternatives considered**: Incluir además enlaces de acciones (`crear-venta`, `agregar-al-carrito`) tipo HAL-FORMS con método HTTP y esquema — rechazado por exceder el alcance pedido por la pauta (solo pide navegabilidad entre recursos, criterio 3, no formularios auto-descriptivos).

## 5. Impacto en la documentación OpenAPI existente

**Decision**: No se reemplaza la documentación `@Operation`/`@ApiResponses` ya existente (criterio 1, ya cumplido en S7); se actualiza el `schema` referenciado en las respuestas para reflejar el nuevo tipo envolvente (`EntityModel<Producto>` / `CollectionModel<EntityModel<Producto>>`), de forma que Swagger UI muestre el bloque `_links` en los ejemplos de respuesta generados automáticamente por springdoc.

**Rationale**: springdoc-openapi-starter-webmvc-ui detecta automáticamente `RepresentationModel` y genera el `$ref` correcto para `_links` sin configuración adicional una vez que el tipo de retorno del método cambia; no se requiere una librería adicional de integración.

**Alternatives considered**: Mantener el `@Schema(implementation = Producto.class)` original y anotar manualmente los enlaces como propiedades extra — rechazado porque generaría una documentación inconsistente con la respuesta real (`_links` no aparecería documentado).

## 6. Seguridad existente (HTTP Basic)

**Decision**: No se modifica `SecurityConfig`; se preserva `.anyRequest().authenticated()` con HTTP Basic tal como está en `feat/openapi-docs-s7`. Los nuevos parámetros de consulta (`?productoId=`, `?categoriaId=`, etc.) sobre los mismos endpoints `GET` heredan automáticamente la misma regla de autenticación que ya aplica a esa ruta.

**Rationale**: Está fuera del alcance de la pauta S8 modificar el modelo de autorización; el spec (ver Assumptions) documenta explícitamente que `JwtUtil` es un stub sin uso y que no se introduce autorización por rol nueva en esta iteración.

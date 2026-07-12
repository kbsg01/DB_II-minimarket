# Feature Specification: Documentación Avanzada OpenAPI y HATEOAS

**Feature Branch**: `feat/hateoas-s8`

**Created**: 2026-07-11

**Status**: Draft

**Input**: User description: "S8 - Implementación avanzada de documentación en microservicios con OpenAPI y HATEOAS sobre el backend Minimarket Plus, manteniendo JWT/roles ya integrados de semanas anteriores, para cumplir RA3 IL9 (documentación OAS) e IL10 (HATEOAS)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Desarrollador integrador explora la API vía Swagger UI (Priority: P1)

Un desarrollador externo que se integra por primera vez con el backend de Minimarket Plus abre Swagger UI y, para cada recurso (productos, carritos, categorías, usuarios, ventas, detalle de ventas, inventario), encuentra una descripción clara del propósito del endpoint, los parámetros esperados, ejemplos de cuerpo de petición/respuesta y los códigos de estado posibles (200, 201, 400, 401, 403, 404), sin necesidad de leer el código fuente.

**Why this priority**: Es la base de todo lo demás — si la documentación OAS no está completa y alineada a estándares, ni HATEOAS ni Swagger UI aportan valor. Es además el criterio de mayor ponderación de la pauta (25 pts).

**Independent Test**: Se puede probar de forma independiente abriendo `/swagger-ui.html`, expandiendo cada operación y verificando que existan descripción, ejemplos y respuestas documentadas — sin necesidad de que HATEOAS esté implementado.

**Acceptance Scenarios**:

1. **Given** el backend está corriendo, **When** el desarrollador abre Swagger UI, **Then** ve los 7 recursos agrupados con nombre y descripción de cada controlador.
2. **Given** una operación específica (p.ej. `POST /api/productos`), **When** el desarrollador la expande, **Then** ve un resumen, descripción, ejemplo de cuerpo de la petición y todas las respuestas posibles documentadas con su código HTTP y descripción.
3. **Given** un endpoint protegido por rol, **When** el desarrollador consulta su documentación, **Then** la respuesta `403` está documentada junto con qué rol se requiere.

---

### User Story 2 - Cliente de la API navega recursos relacionados mediante enlaces HATEOAS (Priority: P2)

Un cliente de la API (aplicación externa o Postman) consulta un recurso individual o una colección y recibe, además de los datos, enlaces (`_links`) que le permiten descubrir sin documentación adicional cómo obtener el propio recurso (`self`), volver a la colección, y navegar a recursos relacionados relevantes (por ejemplo, desde un producto a su categoría e inventario asociado, desde una venta a sus detalles de venta, desde un carrito a los productos que contiene).

**Why this priority**: Es el objetivo central de la semana (IL10, 20 pts) y depende de que los recursos y sus relaciones ya estén correctamente documentados (Historia 1), pero es una capa adicional sobre el modelo de datos existente.

**Independent Test**: Se puede probar de forma independiente llamando a los endpoints con un cliente HTTP (curl/Postman) autenticado y verificando que la respuesta JSON incluya un bloque `_links` con URIs válidas y resolubles.

**Acceptance Scenarios**:

1. **Given** un usuario autenticado con permisos de lectura, **When** consulta `GET /api/productos/{id}`, **Then** la respuesta incluye enlaces `self`, `productos` (colección) y, si aplica, `categoria` e `inventario` del producto.
2. **Given** un usuario autenticado, **When** consulta `GET /api/productos` (colección), **Then** cada elemento de la colección incluye su propio enlace `self` y la respuesta general incluye un enlace a la colección.
3. **Given** un usuario autenticado, **When** consulta `GET /api/ventas/{id}`, **Then** la respuesta incluye un enlace hacia sus detalles de venta relacionados (`detalle-ventas`).
4. **Given** un cliente HTTP sin credenciales válidas, **When** intenta acceder a un endpoint o seguir un enlace HATEOAS de la respuesta, **Then** recibe `401 Unauthorized` igual que si accediera directamente a la ruta (los enlaces no eluden la autenticación existente).

---

### User Story 3 - Equipo docente evalúa el proceso técnico y la reflexión del grupo (Priority: P3)

El equipo de evaluación revisa un informe técnico que resume los cambios realizados respecto a semanas anteriores, explica qué endpoints se documentaron con más detalle, qué dificultades surgieron al integrar HATEOAS y cómo se resolvieron, y reflexiona sobre el aporte de OpenAPI + HATEOAS a la calidad, mantenibilidad y navegabilidad del backend — evidenciado con capturas de Swagger UI, el JSON exportado y pruebas en Postman.

**Why this priority**: Es un entregable documental que depende de que las Historias 1 y 2 estén implementadas para poder evidenciarlas; no bloquea el desarrollo técnico pero sí la entrega final (10 pts análisis + 10 pts entrega en GitHub).

**Independent Test**: Se puede verificar de forma independiente revisando que el informe exista, cubra los puntos pedidos por la guía (resumen técnico, análisis, evidencia, reflexión, preguntas de apoyo) y enlace evidencia verificable (capturas, JSON, pruebas).

**Acceptance Scenarios**:

1. **Given** la implementación técnica está completa, **When** se redacta el informe, **Then** incluye resumen técnico del avance, análisis de la documentación, evidencia de ejecución y reflexión técnica según el Paso 5 de la guía.
2. **Given** el informe está listo, **When** se sube el proyecto a GitHub, **Then** el repositorio contiene el código funcional, el informe y el JSON de `/v3/api-docs` actualizado.

### Edge Cases

- ¿Qué ocurre cuando un recurso referenciado por un enlace HATEOAS ya no existe (p.ej. producto con categoría eliminada)? El enlace relacionado debe omitirse en vez de apuntar a un recurso inexistente.
- ¿Cómo se comporta la documentación OpenAPI para operaciones que no requieren autenticación (`/public/hola`)? Debe quedar explícitamente marcada como pública, sin requisito de esquema de seguridad.
- ¿Qué pasa si el cliente de la API no envía credenciales? Debe recibir `401 Unauthorized` tanto al acceder directamente a un endpoint como al seguir cualquier enlace de una respuesta previa, de forma consistente.
- ¿Cómo se documentan los errores de validación (400) y de datos no encontrados (404) de forma consistente en todos los controladores?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE documentar con OpenAPI (resumen, descripción, parámetros, ejemplos de petición/respuesta y códigos de estado) todas las operaciones de los controladores de Producto, Carrito, Categoría, Usuario, Venta, Detalle de Venta e Inventario.
- **FR-002**: El sistema DEBE documentar en cada operación protegida el esquema de seguridad requerido (HTTP Basic) y la respuesta `401` asociada a falta de autenticación.
- **FR-003**: El sistema DEBE devolver cada recurso individual envuelto en un modelo con enlaces de hipermedia (`_links`) que incluya como mínimo un enlace `self`.
- **FR-004**: El sistema DEBE devolver cada colección de recursos envuelta en un modelo con enlaces de hipermedia que incluya un enlace a la colección y, dentro de cada elemento, su enlace `self`.
- **FR-005**: El sistema DEBE incluir, cuando exista una relación significativa entre entidades (producto↔categoría, producto↔inventario, carrito↔productos, venta↔detalle de venta), un enlace de hipermedia hacia el recurso relacionado.
- **FR-006**: El sistema NO DEBE incluir en la respuesta un enlace de hipermedia hacia un recurso relacionado que no existe (p. ej. producto sin movimientos de inventario registrados).
- **FR-007**: El sistema DEBE mantener sin regresiones el comportamiento de autenticación HTTP Basic ya existente en todos los endpoints, con o sin HATEOAS habilitado.
- **FR-008**: El sistema DEBE exponer una interfaz Swagger UI navegable que muestre las operaciones documentadas junto con ejemplos de las respuestas con enlaces HATEOAS.
- **FR-009**: El sistema DEBE permitir exportar el contrato actualizado desde `/v3/api-docs` de forma que pueda importarse en Postman y ejecutar los endpoints documentados.
- **FR-010**: El proyecto DEBE incluir un informe técnico que documente el avance respecto a semanas anteriores, el análisis de la documentación generada, evidencia de ejecución y una reflexión técnica sobre calidad, navegabilidad y mantenibilidad.

### Key Entities

- **Producto**: artículo del minimarket; se relaciona con Categoría (clasificación) e Inventario (existencias).
- **Categoría**: agrupación de productos.
- **Carrito**: conjunto de productos seleccionados por un cliente antes de concretar una venta.
- **Inventario**: registro de movimientos/existencias de stock asociado a un producto.
- **Usuario**: cuenta con un rol (ADMIN, CAJERO, CLIENTE) que determina qué recursos y enlaces puede ver u operar.
- **Venta**: transacción registrada, compuesta por uno o más Detalles de Venta.
- **Detalle de Venta**: línea de una venta que referencia un producto y una cantidad.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de las operaciones de los 7 controladores de negocio quedan documentadas en Swagger UI con descripción, ejemplos y códigos de respuesta (alineado al criterio 1 de la pauta, 25 pts).
- **SC-002**: El 100% de los endpoints de consulta de recurso individual y colección de las 7 entidades de negocio devuelven enlaces de hipermedia funcionales y resolubles (criterio 3, 20 pts).
- **SC-003**: Un desarrollador que solo cuenta con Swagger UI y sin leer el código puede identificar en menos de 2 minutos cómo obtener un recurso relacionado a partir de uno dado, siguiendo únicamente los enlaces de la respuesta.
- **SC-004**: El 100% de las pruebas manuales en Postman sobre los endpoints documentados devuelven respuestas coherentes con lo mostrado en Swagger UI, sin errores de contrato (criterio 4, 20 pts).
- **SC-005**: Ningún endpoint protegido pierde su requisito de autenticación tras incorporar HATEOAS (0 regresiones de seguridad detectadas).
- **SC-006**: El informe técnico entregado cubre el 100% de los puntos solicitados en el Paso 5 de la guía de aprendizaje (resumen, análisis, evidencia, reflexión, preguntas de apoyo).

## Assumptions

- Se reutiliza el backend ya integrado en la rama `feat/openapi-docs-s7` (JUnit/JaCoCo, OpenAPI base, entidades y roles definidos) como punto de partida, en lugar del proyecto base sin modificar entregado en `S8/minimarket/`, ya que este último no incluye el trabajo de semanas previas.
- El modelo de seguridad vigente en esa rama es HTTP Basic con autenticación requerida para todo `/api/**` (sin diferenciación de permisos por rol a nivel de endpoint); esta iteración no agrega autorización por rol nueva, solo preserva el requisito de autenticación existente al incorporar HATEOAS. La clase `JwtUtil` presente en el código es un stub sin uso activo, heredado de una integración anterior, y no forma parte del alcance de esta feature.
- Los enlaces HATEOAS se implementan sobre los 7 controladores de negocio existentes (Producto, Carrito, Categoría, Usuario, Venta, Detalle de Venta, Inventario); el controlador público de saludo (`/public/hola`) queda fuera del alcance de HATEOAS por no representar un recurso de negocio.
- No se requiere versionado de la API (v1, v2) en esta iteración; el contrato OpenAPI expuesto en `/v3/api-docs` sigue siendo el único vigente.
- La entrega final se realiza subiendo el repositorio a GitHub tal como se ha hecho en semanas anteriores, sin necesidad de desplegar el backend en un ambiente externo.

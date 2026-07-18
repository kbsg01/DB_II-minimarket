# Feature Specification: Backend MiniMarket Plus (EFT S9)

**Feature Branch**: `001-minimarket-backend-spec`

**Created**: 2026-07-16

**Status**: Draft

**Input**: User description: "revisa los indicadores de logro e instrucciones especificas
nuevamente y genera las especificaciones con las que debe contar el proyecto"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Autenticación segura y autorización por roles (Priority: P1)

Un cliente o un empleado (cajero, jefe de turno, gerente de sucursal, reponedor,
asistente de servicio al cliente o administrador) inicia sesión con sus credenciales y
obtiene acceso únicamente a las funciones permitidas para su rol. Un cliente no puede
modificar precios ni stock; un cajero no puede generar reportes gerenciales; solo el
gerente/administrador gestiona precios, reportes y usuarios.

**Why this priority**: Ninguna otra capacidad del sistema (inventario, ventas, pedidos,
reportes) puede exponerse de forma segura sin saber primero quién solicita la operación y
qué le está permitido hacer. Es además el criterio de seguridad explícitamente exigido
por el caso de negocio (autenticación JWT y autorización por roles).

**Independent Test**: Se puede probar de forma aislada registrando un usuario por cada
rol, iniciando sesión, y verificando que cada uno accede solo a las operaciones
permitidas para su rol (por ejemplo, un cliente que intenta actualizar un precio recibe
un rechazo, mientras un gerente lo logra).

**Acceptance Scenarios**:

1. **Given** un usuario registrado con rol "Cliente", **When** inicia sesión con
   credenciales válidas, **Then** recibe acceso autenticado limitado a consulta de
   catálogo, disponibilidad y gestión de sus propios pedidos/carrito.
2. **Given** un usuario con rol "Cajero", **When** intenta consultar reportes de
   rotación de productos reservados a gerencia, **Then** el sistema rechaza la operación
   por falta de autorización.
3. **Given** un usuario con rol "Gerente de sucursal", **When** actualiza el precio de un
   producto o consulta reportes de rotación, **Then** la operación se ejecuta
   correctamente.
4. **Given** credenciales inválidas o un token expirado/alterado, **When** se intenta
   acceder a cualquier operación protegida, **Then** el acceso se rechaza sin exponer
   información sensible del sistema.

---

### User Story 2 - Gestión de inventario centralizado en tiempo real (Priority: P1)

El personal autorizado necesita ver el stock de cada producto por sucursal actualizado en
tiempo real, recibir la generación automática de una orden de compra hacia el proveedor
correspondiente cuando el stock de un producto llega a su nivel mínimo, y obtener reportes
de rotación que identifiquen los productos más y menos vendidos.

**Why this priority**: Es la necesidad tecnológica principal descrita en el caso de
negocio "MiniMarket Plus" y la base operativa de la que dependen las ventas y los pedidos
en línea (no se puede vender ni prometer disponibilidad sobre un stock que no se conoce
con precisión).

**Independent Test**: Se puede probar de forma aislada registrando movimientos de
inventario (entradas/salidas) para un producto en una sucursal, verificando que el stock
resultante es correcto, que al cruzar el umbral mínimo se genera una orden de compra
automática, y que un reporte de rotación refleja correctamente los productos con mayor y
menor movimiento en un rango de fechas.

**Acceptance Scenarios**:

1. **Given** un producto con stock por sobre su nivel mínimo en una sucursal, **When**
   se registra una salida de inventario (venta o despacho) que reduce el stock por debajo
   del nivel mínimo configurado, **Then** el sistema genera automáticamente una orden de
   compra hacia el proveedor asociado a ese producto.
2. **Given** movimientos de inventario registrados en un período, **When** un gerente
   solicita el reporte de rotación de productos, **Then** el sistema entrega el listado de
   productos más vendidos y menos vendidos ordenado por volumen de movimiento.
3. **Given** múltiples sucursales con el mismo producto, **When** se consulta el stock de
   dicho producto, **Then** el sistema muestra el stock específico de cada sucursal, no un
   total agregado que oculte diferencias entre ellas.

---

### User Story 3 - Consulta de disponibilidad y pedidos en línea (Priority: P2)

Un cliente consulta si un producto está disponible en una sucursal específica y, si lo
está, genera un pedido en línea indicando si lo retirará en tienda o si requiere despacho
a domicilio. El cliente puede además beneficiarse de ofertas y promociones vigentes que la
cadena gestiona de forma centralizada para todas las sucursales.

**Why this priority**: Es el canal de venta digital que el caso de negocio identifica
como necesidad de modernización, pero depende funcionalmente de que la autenticación
(US1) y el inventario (US2) ya existan, por lo que se entrega después de esas dos
capacidades base.

**Independent Test**: Se puede probar de forma aislada creando un pedido para un producto
con stock disponible en una sucursal, seleccionando retiro en tienda o despacho a
domicilio, y verificando que el pedido descuenta el stock de esa sucursal y refleja
cualquier promoción vigente en el precio final.

**Acceptance Scenarios**:

1. **Given** un producto con stock disponible en una sucursal, **When** un cliente
   autenticado consulta su disponibilidad, **Then** el sistema confirma la cantidad
   disponible en esa sucursal específica.
2. **Given** un producto disponible, **When** el cliente genera un pedido en línea
   seleccionando "retiro en tienda" o "despacho a domicilio", **Then** el pedido queda
   registrado con el modo de entrega elegido y el stock de la sucursal se reduce en
   consecuencia.
3. **Given** una promoción vigente sobre un producto, **When** el cliente agrega ese
   producto a su pedido, **Then** el precio final del pedido refleja el descuento de la
   promoción activa.
4. **Given** un cliente intenta pedir una cantidad mayor a la disponible en la sucursal
   seleccionada, **When** confirma el pedido, **Then** el sistema rechaza la operación e
   informa la cantidad máxima disponible.

---

### User Story 4 - Documentación técnica navegable de la API (Priority: P1)

Un integrador técnico (por ejemplo, un desarrollador de una futura app móvil de
MiniMarket Plus, o el propio equipo evaluador del EFT) necesita explorar la API sin
acceso al código fuente: qué operaciones existen, qué datos requieren, y cómo un recurso
se relaciona con otros (por ejemplo, desde un producto llegar a su inventario o desde una
venta llegar a su detalle) navegando enlaces expuestos por la propia respuesta de la API.

**Why this priority**: Reclasificada de P3 a P1 (ver
[Clarifications de specs/002-guion-video-ejecucion](../002-guion-video-ejecucion/spec.md#clarifications),
sesión 2026-07-16): el feedback real del profesor sobre la entrega de la Semana 8 bajó la
calificación del grupo a "Medianamente Logrado" precisamente porque el informe declaraba
HATEOAS sin código real que lo respaldara (sin `EntityModel`/`CollectionModel`/`linkTo`).
Por eso HATEOAS pasa a ser bloqueante para esta entrega: ni el informe ni el video pueden
declararlo logrado sin la implementación real, así que no puede quedar como la historia de
menor prioridad.

**Independent Test**: Se puede probar de forma aislada abriendo la documentación
autogenerada de la API y verificando que cada operación principal (autenticación,
inventario, ventas, pedidos) está descrita con sus parámetros y respuestas, y que las
respuestas de recursos relacionados incluyen enlaces a los recursos con los que se
relacionan.

**Acceptance Scenarios**:

1. **Given** la API en ejecución, **When** un integrador abre la documentación técnica,
   **Then** encuentra todas las operaciones principales descritas con sus parámetros,
   respuestas y códigos de error posibles.
2. **Given** la respuesta de un recurso (por ejemplo, un producto), **When** el
   integrador la inspecciona, **Then** encuentra enlaces hacia los recursos relacionados
   (su inventario, su categoría) sin necesidad de conocer de antemano la estructura de
   URLs del sistema.

---

### Edge Cases

- ¿Qué ocurre si dos empleados de sucursales distintas registran simultáneamente una
  salida de stock que cruza el nivel mínimo del mismo producto? El sistema no debe
  generar órdenes de compra duplicadas para el mismo evento de reposición.
- ¿Qué ocurre si un cliente intenta pagar/generar un pedido cuando el stock cambió entre
  la consulta de disponibilidad y la confirmación del pedido? El sistema debe revalidar
  disponibilidad al momento de confirmar, no solo al momento de consultar.
- ¿Cómo maneja el sistema un intento de acceso con un rol que fue revocado mientras el
  usuario tenía una sesión activa?
- ¿Qué ocurre si una promoción vigente y un cambio manual de precio ocurren sobre el
  mismo producto al mismo tiempo? Debe quedar definido cuál prevalece para evitar precios
  inconsistentes en el pedido del cliente.
- ¿Qué ocurre si se solicita un reporte de rotación para un rango de fechas sin
  movimientos registrados? El sistema debe responder con un resultado vacío explícito, no
  con un error.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir el registro y autenticación de usuarios (clientes
  y empleados) mediante credenciales propias, emitiendo un token de sesión (JWT) tras un
  inicio de sesión exitoso.
- **FR-002**: El sistema MUST asignar uno o más roles a cada usuario (cliente, cajero,
  jefe de turno, reponedor, asistente de servicio al cliente, gerente de sucursal,
  administrador) y MUST restringir cada operación según el rol del solicitante.
- **FR-003**: El sistema MUST rechazar cualquier operación protegida cuando el token de
  sesión es inválido, está expirado o ha sido alterado, sin revelar detalles internos del
  sistema en el mensaje de error.
- **FR-004**: El sistema MUST registrar y proteger los datos personales y financieros de
  los clientes conforme a la Ley de Protección de Datos Personales vigente en Chile
  (por ejemplo, no exponer contraseñas ni datos financieros completos en respuestas de la
  API).
- **FR-005**: El sistema MUST mantener el stock de cada producto diferenciado por
  sucursal y MUST reflejar en tiempo real los movimientos de entrada y salida de
  inventario.
- **FR-006**: El sistema MUST generar automáticamente una orden de compra hacia el
  proveedor asociado a un producto cuando el stock de dicho producto en una sucursal
  alcanza o cae por debajo de su nivel mínimo configurado.
- **FR-007**: El sistema MUST generar reportes de rotación de productos que identifiquen
  los productos más vendidos y menos vendidos en un rango de fechas dado.
- **FR-008**: El sistema MUST permitir a un cliente autenticado consultar la
  disponibilidad de un producto en una sucursal específica.
- **FR-009**: El sistema MUST permitir a un cliente autenticado generar un pedido en
  línea especificando el modo de entrega: retiro en tienda o despacho a domicilio.
- **FR-010**: El sistema MUST revalidar la disponibilidad de stock al momento de
  confirmar un pedido, rechazando la operación si el stock ya no es suficiente.
- **FR-011**: El sistema MUST permitir la gestión centralizada de ofertas y promociones
  aplicables a productos, y MUST reflejar el precio promocional vigente al calcular el
  total de un pedido o venta.
- **FR-012**: El sistema MUST exponer documentación técnica de la API basada en OpenAPI
  Specification (OAS), describiendo cada operación disponible, sus parámetros y sus
  posibles respuestas.
- **FR-013**: El sistema MUST incluir enlaces HATEOAS en las respuestas de la API que
  permitan navegar desde un recurso hacia sus recursos relacionados (por ejemplo, de un
  producto a su inventario y su categoría; de una venta a su detalle).
- **FR-014**: El sistema MUST permitir a los roles de gestión (gerente de sucursal,
  administrador) actualizar precios de productos y consultar reportes de ventas e
  inventario, restringiendo esas operaciones a clientes y roles operativos (cajero,
  reponedor).
- **FR-015**: El sistema MUST registrar cada venta con el detalle de los productos,
  cantidades y precios aplicados (incluyendo promociones vigentes), asociada al usuario
  que la generó.

### Key Entities

- **Usuario**: persona que interactúa con el sistema (cliente o empleado); tiene
  credenciales propias y uno o más roles asociados.
- **Rol**: nivel de acceso que determina qué operaciones puede ejecutar un usuario
  (cliente, cajero, jefe de turno, reponedor, asistente de servicio al cliente, gerente
  de sucursal, administrador).
- **Sucursal**: punto físico de la cadena MiniMarket Plus; el stock, las órdenes de
  compra y los pedidos se asocian a una sucursal específica.
- **Producto**: artículo de consumo diario ofrecido por la cadena, con precio,
  categoría y nivel de stock mínimo configurado por sucursal.
- **Categoria**: agrupación de productos (abarrotes, bebidas, lácteos y congelados,
  artículos de limpieza, cuidado personal).
- **Inventario**: movimiento de entrada o salida de stock de un producto en una
  sucursal, con fecha y cantidad, usado para calcular el stock vigente y disparar
  reposición automática.
- **Proveedor**: entidad externa a la que se dirige una orden de compra automática
  cuando el stock de un producto cae bajo su nivel mínimo.
- **OrdenDeCompra**: solicitud de reposición generada automáticamente hacia un
  proveedor cuando corresponde, asociada a un producto y una sucursal.
- **Promocion**: oferta vigente sobre uno o más productos, con vigencia temporal y
  descuento aplicable, gestionada de forma centralizada para todas las sucursales.
- **Pedido**: solicitud de compra en línea de un cliente, con productos, cantidades,
  modo de entrega (retiro o despacho) y sucursal de origen del stock.
- **Venta**: transacción de venta concretada (en tienda o proveniente de un pedido en
  línea), con el detalle de productos, cantidades y precios aplicados.
- **DetalleVenta**: línea individual de una venta, referida a un producto, cantidad y
  precio unitario aplicado.
- **Carrito**: selección temporal de productos de un cliente previa a convertirse en un
  pedido o venta.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un usuario de cualquier rol puede iniciar sesión y comenzar a operar
  dentro de las funciones permitidas para su rol en menos de 5 segundos desde el envío de
  credenciales válidas.
- **SC-002**: El 100% de los intentos de acceso a una operación no permitida para el rol
  del usuario son rechazados, verificado sobre el total de combinaciones rol-operación
  definidas.
- **SC-003**: El stock mostrado para un producto en una sucursal coincide con la suma de
  sus movimientos de inventario registrados, sin desfases, en el 100% de las consultas de
  prueba.
- **SC-004**: El 100% de los casos en que el stock de un producto cruza su nivel mínimo
  configurado resultan en la generación de exactamente una orden de compra hacia el
  proveedor correspondiente.
- **SC-005**: Un cliente puede completar la consulta de disponibilidad y la generación de
  un pedido en línea (retiro o despacho) en menos de 2 minutos en un flujo sin
  interrupciones.
- **SC-006**: El 100% de los pedidos confirmados con stock insuficiente al momento de la
  confirmación son rechazados con un mensaje que indica la cantidad máxima disponible.
- **SC-007**: Un integrador técnico sin acceso al código fuente puede identificar, solo a
  partir de la documentación expuesta por la API, todas las operaciones principales
  disponibles y cómo navegar entre recursos relacionados.
- **SC-008**: Los reportes de rotación de productos generados para un rango de fechas
  coinciden, en el 100% de los casos de prueba, con el orden real de ventas registradas en
  ese rango.

## Assumptions

- El proyecto ya cuenta con una base parcial (usuarios/roles, productos, categorías,
  inventario simple, ventas, carrito) que esta especificación extiende para cubrir la
  totalidad del caso de negocio "MiniMarket Plus"; no se asume una reescritura desde
  cero.
- El pago de un pedido en línea se resuelve en tienda o contra entrega; esta
  especificación no asume integración con una pasarela de pago externa, ya que las
  instrucciones específicas no la mencionan como requisito.
- La generación automática de una orden de compra implica su registro interno (para
  gestión y seguimiento por parte de la sucursal/administración); no se asume una
  integración externa real con sistemas de proveedores, ya que no se especifica en el
  caso de negocio.
- Las 10 sucursales mencionadas en el caso de negocio se modelan como una entidad
  "Sucursal" que dimensiona el stock, las órdenes de compra y los pedidos; no se asume
  que deban existir 10 registros reales de sucursales para efectos de esta evaluación,
  sino que el modelo debe soportar múltiples sucursales.
- Los roles empleados (cajero, jefe de turno, reponedor, asistente de servicio al
  cliente) comparten en esta especificación un nivel de acceso operativo común frente a
  gerencia/administración, salvo que una historia de usuario futura requiera diferenciar
  permisos entre ellos con mayor detalle.

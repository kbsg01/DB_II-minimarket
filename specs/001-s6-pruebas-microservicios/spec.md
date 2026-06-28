# Feature Specification: S6 Pruebas Unitarias en Microservicios

**Feature Branch**: `feat/microservices-junit-s6`

**Created**: 2026-06-28

**Status**: Draft

**Input**: Semana 6 — PBY2202 Desarrollo Backend II — Grupo 7. Actividad sumativa: "Integrando, Aplicando y Analizando Resultados de Pruebas Unitarias en Microservicios con JUnit". Integrar trabajo de S5, implementar autenticación JWT, documentar API con Swagger, diseñar y ejecutar pruebas unitarias para entidades clave, producir informe técnico y publicar en GitHub.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Verificar control de acceso con pruebas unitarias (Priority: P1)

Un desarrollador backend diseña y ejecuta pruebas unitarias que validan que las restricciones de acceso por rol se cumplen correctamente en las cuatro entidades clave: Producto, Inventario, Venta y Usuario.

**Why this priority**: Es el núcleo de la actividad (criterios 1, 2, 3 y 4 de la pauta, 55 puntos de 100). Sin pruebas unitarias que cubran éxito y error para cada entidad, los demás entregables no tienen base.

**Independent Test**: Se puede probar independientemente ejecutando `mvn test` y verificando que los reportes Surefire muestran 0 fallos y cobertura de los cuatro dominios.

**Acceptance Scenarios**:

1. **Given** el proyecto configurado con JUnit 5 y Mockito, **When** se ejecuta `mvn test -Djacoco.skip=true`, **Then** los 66 tests pasan sin fallos ni errores (0 failures, 0 errors).
2. **Given** un test de Producto, **When** se intenta modificar un producto como ROLE_CLIENTE, **Then** el servicio lanza una excepción de acceso denegado.
3. **Given** un test de Inventario, **When** se registra un movimiento de entrada válido como ROLE_CAJERO, **Then** el stock del producto aumenta correctamente.
4. **Given** un test de Venta, **When** se intenta crear una venta sin stock suficiente, **Then** el servicio lanza `StockInsuficienteException`.
5. **Given** un test de Usuario, **When** se intenta registrar un usuario con campos incompletos, **Then** el servicio lanza `DatosIncompletosException`.
6. **Given** un test de Usuario, **When** se verifica si un usuario con ROLE_ADMIN puede registrar ventas, **Then** `puedeRegistrarVenta` retorna `true`.

---

### User Story 2 - Autenticar y autorizar solicitudes de API con JWT (Priority: P1)

Un cliente de la API (cajero, administrador o cliente) envía credenciales al endpoint de login y recibe un token JWT que le permite acceder a los endpoints protegidos según su rol.

**Why this priority**: La autenticación JWT es el mecanismo de seguridad central de la semana. Sin él, los endpoints protegidos no tienen sentido en contexto de microservicios.

**Independent Test**: Se puede probar independientemente levantando la aplicación, haciendo POST a `/api/auth/login` con credenciales válidas, y usando el token retornado en un header `Authorization: Bearer <token>` para acceder a `/api/productos`.

**Acceptance Scenarios**:

1. **Given** credenciales válidas de un usuario con ROLE_ADMIN, **When** se hace POST a `/api/auth/login`, **Then** la respuesta contiene un campo `token` con un JWT válido (HS256, 24h de vigencia).
2. **Given** un JWT válido con ROLE_CAJERO, **When** se hace POST a `/api/ventas`, **Then** la respuesta es 200 OK.
3. **Given** un JWT válido con ROLE_CLIENTE, **When** se intenta hacer DELETE a `/api/productos/{id}`, **Then** la respuesta es 403 Forbidden.
4. **Given** ningún token, **When** se hace GET a `/api/productos`, **Then** la respuesta es 401 Unauthorized.
5. **Given** una solicitud a `/public/hola`, **When** se hace sin token, **Then** la respuesta es 200 OK (ruta pública).

---

### User Story 3 - Consultar la API documentada con Swagger UI (Priority: P2)

Un desarrollador o evaluador accede a Swagger UI, se autentica con un token JWT y puede ver y probar todos los endpoints de la API con sus esquemas de seguridad documentados.

**Why this priority**: La documentación Swagger es un requisito explícito de la actividad y del feedback del profesor. Facilita la verificación de los endpoints por el evaluador.

**Independent Test**: Se puede probar levantando la aplicación, obteniendo un token de `/api/auth/login`, abriendo `/swagger-ui/index.html`, haciendo clic en "Authorize" e ingresando el token, y ejecutando cualquier endpoint de la UI.

**Acceptance Scenarios**:

1. **Given** la aplicación en ejecución, **When** se accede a `/swagger-ui/index.html`, **Then** Swagger UI carga correctamente con el título "Minimarket Plus API".
2. **Given** Swagger UI abierto, **When** se hace clic en "Authorize" y se ingresa un Bearer token, **Then** los endpoints subsiguientes incluyen el header de autorización.
3. **Given** la especificación OpenAPI, **When** se revisan los endpoints, **Then** cada endpoint muestra su descripción, parámetros, cuerpo esperado y códigos de respuesta posibles.
4. **Given** un endpoint protegido en Swagger UI, **When** se ejecuta sin token, **Then** la respuesta documenta el error 401.

---

### User Story 4 - Evaluar la calidad con informe técnico y propuestas de mejora (Priority: P2)

El evaluador del curso lee el informe técnico incluido en el documento de entrega y puede verificar: la metodología de pruebas, los resultados de ejecución con evidencias, el análisis de cobertura y las propuestas de mejora justificadas con los datos de los tests.

**Why this priority**: Los criterios 5, 7 y 8 de la pauta suman 35 puntos y son puramente documentales; no requieren código adicional, pero sí análisis fundamentado en los resultados reales.

**Independent Test**: Se puede verificar leyendo el archivo `doc/md/PBY2202_Exp2_S6_Grupo7.md` y comprobando que las cinco secciones de entregables están completas con evidencias.

**Acceptance Scenarios**:

1. **Given** el informe técnico, **When** se lee la sección de metodología, **Then** describe el ciclo Red-Green-Refactor aplicado en al menos un caso concreto con nombre de test y clase.
2. **Given** el informe técnico, **When** se revisa la sección de resultados, **Then** incluye reporte de ejecución con conteo de tests (66 pasados, 0 fallos) y captura o transcripción del output de consola.
3. **Given** el informe técnico, **When** se revisan las propuestas de mejora, **Then** cada propuesta cita el test o métrica que la originó y describe el cambio de código o cobertura que resolvería el problema.
4. **Given** el informe técnico, **When** el evaluador verifica la sección de endpoints protegidos, **Then** al menos 3 endpoints están documentados con su rol requerido y el resultado de la prueba de acceso.

---

### User Story 5 - Publicar código en GitHub con README claro (Priority: P2)

Un desarrollador nuevo (o el evaluador) clona el repositorio, lee el README y puede levantar la aplicación y ejecutar las pruebas sin instrucciones adicionales.

**Why this priority**: Criterio 6 de la pauta (10 puntos). La publicación en GitHub con documentación clara es requisito explícito de entrega.

**Independent Test**: Se puede verificar clonando el repositorio en una máquina limpia y siguiendo únicamente las instrucciones del README para compilar, ejecutar y probar.

**Acceptance Scenarios**:

1. **Given** el README del repositorio, **When** un nuevo desarrollador sigue las instrucciones, **Then** puede ejecutar `mvn test` y obtener BUILD SUCCESS sin configuración adicional.
2. **Given** el README, **When** se revisa la sección de endpoints, **Then** lista todos los recursos con sus rutas base y los roles requeridos.
3. **Given** el repositorio en GitHub, **When** se revisa la estructura de ramas, **Then** existe la rama `feat/microservices-junit-s6` con todos los cambios de S6 commiteados.

---

### Edge Cases

- Un usuario sin roles asignados intenta autenticarse: el sistema genera el JWT pero las rutas protegidas retornan 403.
- Se intenta agregar al carrito una cantidad mayor al stock disponible: `StockInsuficienteException` con mensaje descriptivo.
- Se intenta registrar un usuario con `username` ya existente: la base de datos lanza `ConstraintViolationException` (H2); el controlador responde 400 o 409.
- Un token JWT expirado (más de 24h) intenta acceder a un endpoint: el filtro retorna 401 con mensaje "Token expired".
- Se intenta crear una venta con el carrito vacío: el servicio de ventas debe manejar el caso y retornar error descriptivo.
- Swagger UI recibe un token con firma inválida (manipulado): el filtro rechaza la solicitud con 401.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE tener pruebas unitarias para Producto que verifiquen: agregar, actualizar, eliminar (éxito con ROLE_ADMIN) y acceso no autorizado (ROLE_CLIENTE recibe excepción).
- **FR-002**: El sistema DEBE tener pruebas unitarias para Inventario que verifiquen: registro de movimiento de entrada, registro de movimiento de salida, asociación correcta con producto, y rechazo por permisos insuficientes.
- **FR-003**: El sistema DEBE tener pruebas unitarias para Venta que verifiquen: confirmación de venta con stock suficiente (ROLE_CAJERO), rechazo de venta por stock insuficiente, y detalle de venta con captura de precio.
- **FR-004**: El sistema DEBE tener pruebas unitarias para Usuario que verifiquen: registro con datos completos (éxito), registro con datos incompletos (falla), autenticación válida, autenticación inválida, y verificación de roles para registro de ventas.
- **FR-005**: Todas las pruebas DEBEN ejecutarse con `mvn test` sin errores de compilación ni fallos de ejecución.
- **FR-006**: El sistema DEBE implementar autenticación JWT: el endpoint `/api/auth/login` recibe username/password y retorna un token firmado con HS256, válido por 24 horas, que incluye los roles del usuario.
- **FR-007**: El sistema DEBE aplicar autorización basada en roles: ROLE_ADMIN para gestión de productos y usuarios; ROLE_CAJERO para inventario y ventas; ROLE_CLIENTE para solo lectura.
- **FR-008**: Las contraseñas DEBEN almacenarse con BCrypt antes de persistirlas; nunca en texto plano.
- **FR-009**: Todos los endpoints bajo `/api/**` DEBEN estar documentados con SpringDoc OpenAPI (Swagger UI en `/swagger-ui/index.html`), incluyendo el esquema de autenticación Bearer JWT.
- **FR-010**: Los controladores DEBEN incluir anotaciones `@Tag` y `@Operation` para describir cada endpoint con su propósito y roles requeridos.
- **FR-011**: El informe técnico (`doc/md/PBY2202_Exp2_S6_Grupo7.md`) DEBE incluir: resumen técnico del avance S4-S5-S6, análisis de resultados de pruebas, evidencias de ejecución, propuestas de mejora basadas en los resultados, y descripción de endpoints protegidos.
- **FR-012**: El repositorio DEBE tener un README en la raíz con instrucciones de build, ejecución, endpoints disponibles y enlace al informe técnico.
- **FR-013**: El sistema DEBE generar reportes Surefire (XML) en `target/surefire-reports/` como evidencia para el criterio 4.

### Key Entities

- **Producto**: Representa un artículo comercializable. Tiene categoria, precio, stock en tiempo real. Solo ADMIN puede modificarlo.
- **Inventario**: Registra movimientos de entrada y salida de stock asociados a un Producto. ADMIN y CAJERO pueden registrar movimientos.
- **Venta**: Captura una transacción de compra completa. Tiene usuario cajero, fecha y lista de DetalleVenta. Solo CAJERO puede crearla.
- **DetalleVenta**: Línea de una Venta; captura el precio del producto en el momento de la venta (snapshot), no una referencia al precio actual.
- **Carrito**: Asocia un Usuario con Productos y cantidades, validando disponibilidad de stock en tiempo real.
- **Usuario**: Tiene username, password (BCrypt), nombre, apellido, email, direccion y roles. Se autentica via JWT.
- **Rol**: Etiqueta de autorización (ROLE_ADMIN, ROLE_CAJERO, ROLE_CLIENTE) asignada a usuarios mediante relación ManyToMany.

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Los 8 criterios de evaluación de la pauta alcanzan el nivel "Completamente Logrado" (100%) al momento de la entrega.
- **SC-002**: El comando `mvn test` completa con 66 pruebas ejecutadas, 0 fallos y 0 errores en menos de 3 minutos.
- **SC-003**: Cada una de las 4 entidades clave (Producto, Inventario, Venta, Usuario) tiene al menos 1 escenario de éxito y 1 escenario de error cubiertos por pruebas distintas.
- **SC-004**: Swagger UI lista los 9 recursos de la API (`/api/categorias`, `/api/productos`, `/api/inventario`, `/api/carrito`, `/api/ventas`, `/api/detalle-ventas`, `/api/usuarios`, `/api/auth`, `/public`) con sus endpoints y descripciones.
- **SC-005**: El informe técnico cubre las 5 secciones de entregables definidas en la actividad: diseño y justificación de pruebas, configuración del entorno, resultados y análisis de cobertura, contribución a la calidad del sistema, recomendaciones de mejora.
- **SC-006**: El README permite que un evaluador ejecute el proyecto desde cero siguiendo únicamente sus instrucciones, sin conocimiento previo del proyecto.
- **SC-007**: Al menos 3 propuestas de mejora en el informe técnico citan métricas específicas (nombre de test, porcentaje de cobertura, o conteo de casos) que las justifican.
- **SC-008**: El repositorio GitHub en la rama `feat/microservices-junit-s6` contiene todos los cambios de S6 en un único commit descriptivo (ya cumplido: commit `65bee4b`).

---

## Assumptions

- El evaluador usará la rama `feat/microservices-junit-s6` para revisar el código; no se requiere merge a `main` para la entrega.
- La base de datos H2 en memoria es suficiente para el entorno académico; no se requiere persistencia entre reinicios.
- El entorno del evaluador tiene Java 17+ y Maven 3.8+ instalados; no se requiere Docker ni scripts adicionales.
- Los screenshots de evidencia pueden ser capturas del output de terminal en lugar de imágenes adjuntas, dado que el informe es un documento Markdown.
- JaCoCo no puede generar reporte en Java 25 (bytecode incompatibilidad); la evidencia de cobertura es el reporte Surefire XML junto con el análisis manual del conjunto de tests.
- Las anotaciones `@Tag` y `@Operation` en controladores son parte del criterio 3 (alta cobertura de implementación) y del criterio 9 (Swagger), no un entregable separado.
- El informe técnico se escribe directamente en `doc/md/PBY2202_Exp2_S6_Grupo7.md` (archivo ya existente con estructura parcial provista por el docente).
- Las propuestas de mejora del criterio 8 se derivan de los resultados reales de los 66 tests ya ejecutados, identificando casos límite no cubiertos y posibles refactorizaciones de los servicios.

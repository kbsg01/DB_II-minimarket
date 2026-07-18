<!--
Sync Impact Report
==================
Version change: TEMPLATE (unfilled) → 1.0.0
Rationale: Initial ratification of the project constitution — MINOR/MAJOR bump rules do
not apply yet; first concrete version is set to 1.0.0.

Modified principles: N/A (template placeholders replaced with concrete first-time content)

Added principles:
- I. Arquitectura de Microservicios por Dominio
- II. Seguridad No Negociable: Autenticación JWT y Autorización por Roles
- III. Pruebas Unitarias Obligatorias (NON-NEGOTIABLE)
- IV. Documentación de API con OpenAPI y HATEOAS
- V. Integración Funcional de Extremo a Extremo
- VI. Trazabilidad hacia el Informe y la Evidencia
- VII. Calidad y Mantenibilidad del Código

Added sections:
- Entregables y Estándares Técnicos
- Flujo de Trabajo y Calidad
- Governance (versioning + compliance rules)

Source documents (doc/):
- PBY2202_EFT_S9_Instrucciones_específicas (forma C).docx — scope, deliverables, context
- PBY2202_EFT_S9_Pauta_evaluación_EFT.docx — 8 graded criteria (100 pts) mapped 1:1 to
  Core Principles I–VII plus the report criterion folded into Principle VI
- PBY2202_EFT_Plantilla_Informe_PDF.docx — official report template referenced in
  Principle VI and in the Entregables section

Templates requiring updates:
- ✅ .specify/templates/plan-template.md — generic "Constitution Check" gate already
  reads from this file; no hardcoded principle names to update
- ✅ .specify/templates/spec-template.md — no constitution-specific references found
- ✅ .specify/templates/tasks-template.md — no constitution-specific references found
- ⚠ pending — pom.xml does not yet declare springdoc-openapi / spring-hateoas
  dependencies required by Principle IV; first feature plan touching API docs must add
  them (tracked here, not a constitution defect)

Follow-up TODOs: none deferred — all placeholders resolved with concrete values.
-->

# MiniMarket Plus Backend Constitution

## Core Principles

### I. Arquitectura de Microservicios por Dominio
Cada dominio funcional del negocio (inventario, ventas/pedidos, catálogo de productos,
usuarios y carrito) MUST exponerse como un conjunto cohesionado de controlador, servicio
e interfaz de repositorio independiente, sin lógica de negocio cruzada entre dominios.
Toda operación descrita en el caso "MiniMarket Plus" (control de stock en tiempo real,
generación automática de órdenes de compra al alcanzar el stock mínimo, reportes de
rotación de productos, consulta de disponibilidad por sucursal, pedidos en línea para
retiro o despacho, y gestión centralizada de ofertas) MUST quedar implementada y ser
ejecutable de extremo a extremo antes de considerarse completa.
**Razón**: El indicador 1 de la pauta de evaluación (15 puntos) exige que los
microservicios cubran la totalidad de las operaciones requeridas con ejecución correcta;
una cobertura parcial o con lógica mezclada entre dominios reduce el puntaje y dificulta
la evaluación de la integración (indicador 5).

### II. Seguridad No Negociable: Autenticación JWT y Autorización por Roles
Toda ruta que exponga datos de clientes, precios, stock o transacciones MUST exigir
autenticación mediante JWT y MUST validar el rol del usuario (cliente, cajero, jefe de
turno, gerente de sucursal, administrador) antes de autorizar la operación. Las
credenciales y tokens NUNCA se registran en texto plano en logs. El manejo de datos
personales y financieros de clientes MUST cumplir la Ley de Protección de Datos
Personales vigente en Chile (minimización de datos expuestos en respuestas, cifrado de
contraseñas, control de acceso a información sensible).
**Razón**: El indicador 2 de la pauta (15 puntos) evalúa explícitamente autenticación JWT
y autorización basada en roles; las instrucciones específicas exigen además cumplimiento
normativo de protección de datos como requisito del caso de negocio.

### III. Pruebas Unitarias Obligatorias (NON-NEGOTIABLE)
Cada servicio de negocio MUST contar con pruebas unitarias que cubran sus operaciones
principales (creación, actualización, reglas de stock mínimo, cálculo de totales de
venta, control de acceso) antes de dar por cerrada una tarea. Las pruebas MUST
ejecutarse y su resultado (verde/rojo, cobertura relevante) MUST quedar registrado como
evidencia reproducible (salida de `mvn test` o reporte equivalente). No se aceptan
tareas marcadas como completas con pruebas rotas o ausentes en los servicios que
tocaron.
**Razón**: El indicador 3 de la pauta (10 puntos) exige configurar y ejecutar pruebas
unitarias con evidencia de resultados; sin esta disciplina el informe (indicador 6) no
puede documentar evidencia real de testing.

### IV. Documentación de API con OpenAPI y HATEOAS
Todo endpoint REST público MUST estar documentado bajo OpenAPI Specification (OAS) y
MUST incluir enlaces HATEOAS relevantes para la navegación de recursos relacionados
(por ejemplo, un producto enlaza a su inventario y categoría; una venta enlaza a su
detalle y al cliente). La documentación generada MUST mantenerse sincronizada con el
código: un cambio de contrato de API sin actualizar su documentación se considera una
tarea incompleta.
**Razón**: El indicador 4 de la pauta (10 puntos) y las instrucciones específicas exigen
ambos estándares de forma explícita; la desactualización de la documentación es la causa
más común de pérdida de puntos en este criterio.

### V. Integración Funcional de Extremo a Extremo
Antes de cerrar cualquier incremento de trabajo, el sistema completo (seguridad +
microservicios + persistencia + documentación) MUST arrancar y responder correctamente
a un flujo real (por ejemplo: login → consulta de catálogo → creación de pedido →
descuento de stock → registro de venta) usando el perfil de base de datos configurado
para pruebas (H2). Los componentes no se integran "al final"; cada tarea que agregue un
componente MUST validar que no rompe el arranque ni los flujos ya existentes.
**Razón**: El indicador 5 de la pauta (15 puntos) evalúa el funcionamiento global del
sistema, no solo de piezas aisladas; verificarlo de forma incremental evita
descubrir fallas de integración recién al final del proyecto.

### VI. Trazabilidad hacia el Informe y la Evidencia
Todo cambio relevante (configuración de seguridad, decisión de diseño, mejora aplicada,
resultado de pruebas) MUST quedar en condiciones de ser documentado en el informe oficial
(plantilla `doc/PBY2202_EFT_Plantilla_Informe_PDF.docx`) con evidencia verificable:
capturas de pantalla, fragmentos de configuración, resultados de ejecución de pruebas y
justificación de decisiones. El código y los commits MUST ser suficientemente claros
como para reconstruir esa evidencia sin depender de la memoria del equipo.
**Razón**: El indicador 6 (10 puntos) exige un informe detallado con justificación de
decisiones y evidencia real; sin trazabilidad durante el desarrollo, el informe se
redacta de memoria y pierde precisión.

### VII. Calidad y Mantenibilidad del Código
El código MUST seguir las convenciones ya establecidas en el proyecto (paquetes
`controller`, `service`, `service.impl`, `repository`, `entity`, `security`), con
nombres en español consistentes con el dominio de negocio ya existente, sin lógica de
negocio en los controladores, y sin código muerto o de prueba (`HolaMundoController` y
similares) en la rama de entrega final. Los métodos MUST ser legibles sin comentarios
explicativos extensos; un comentario solo se justifica para una decisión no obvia.
**Razón**: El indicador 8 de la pauta (10 puntos) evalúa explícitamente buenas prácticas,
legibilidad y mantenibilidad; mantener la convención ya existente evita inconsistencias
entre integrantes del equipo.

## Entregables y Estándares Técnicos

El proyecto se entrega como una aplicación backend Spring Boot 3.x (Java 17) para el caso
"MiniMarket Plus", con los siguientes componentes obligatorios verificables:

- **Seguridad**: Spring Security + JWT (`spring-security` ya presente en `pom.xml`),
  autorización por roles sobre endpoints de stock, precios, ventas y reportes.
- **Persistencia**: Spring Data JPA sobre H2 (u otra base relacional configurada) para
  entidades ya definidas (`Producto`, `Categoria`, `Inventario`, `Venta`, `DetalleVenta`,
  `Carrito`, `Usuario`, `Rol`).
- **Documentación de API**: OpenAPI Specification (OAS) y HATEOAS; toda dependencia
  necesaria para generarlos (por ejemplo `springdoc-openapi`, `spring-hateoas`) MUST
  añadirse a `pom.xml` antes de que una tarea de documentación se considere completa.
- **Pruebas**: `spring-boot-starter-test` y `spring-security-test` (ya presentes) MUST
  usarse para cubrir servicios y, cuando corresponda, seguridad de endpoints.
- **Entregables finales del EFT**: informe PDF basado en la plantilla oficial, video de
  presentación (Kaltura, 7 a 10 minutos, con participación de todos los integrantes) y
  repositorio público en GitHub correctamente estructurado con el proyecto y el video.

## Flujo de Trabajo y Calidad

- El equipo (2 a 3 integrantes) MUST definir un jefe de proyecto por cada paso y llevar
  un cronograma de avance, según lo exigido por las instrucciones específicas.
- Cada incremento de trabajo (feature, fix o mejora) sigue el ciclo: especificar →
  planificar → generar tareas → implementar, dejando evidencia (pruebas, capturas,
  commits descriptivos) reutilizable para el informe.
- Antes de marcar una tarea como terminada, MUST verificarse: (a) el proyecto compila
  (`mvn -q compile` o `test`), (b) las pruebas unitarias asociadas pasan, (c) la
  documentación OpenAPI/HATEOAS del endpoint tocado sigue siendo correcta.
- Las revisiones de código y de plan MUST verificar cumplimiento de los ocho principios
  anteriores, ya que estos corresponden uno a uno con los ocho criterios de la pauta de
  evaluación EFT (100 puntos totales).

## Governance

Esta constitución prevalece sobre cualquier otra convención informal del equipo para
este proyecto. Cualquier enmienda MUST documentarse en este archivo, indicando el
principio afectado y la razón del cambio, y MUST propagarse a `plan-template.md`,
`spec-template.md` y `tasks-template.md` si estos hacen referencia a un principio
modificado.

**Versionado**: se usa versionado semántico (MAYOR.MENOR.PARCHE) para esta constitución.
MAYOR ante eliminación o redefinición incompatible de un principio; MENOR ante adición de
un principio o expansión material de una guía existente; PARCHE ante aclaraciones o
correcciones de redacción sin cambio de sentido.

**Cumplimiento**: todo plan (`/speckit-plan`) MUST incluir una verificación explícita
contra los ocho principios en su sección "Constitution Check" antes de proceder al
diseño detallado, y nuevamente antes de la implementación. Cualquier desviación MUST
justificarse en la tabla de "Complexity Tracking" del plan.

**Version**: 1.0.0 | **Ratified**: 2026-07-16 | **Last Amended**: 2026-07-16

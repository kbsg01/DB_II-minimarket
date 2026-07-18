# Feature Specification: Recuperación de Implementación Previa y Guion del Video EFT

**Feature Branch**: `002-guion-video-ejecucion`

**Created**: 2026-07-16

**Status**: Draft

**Input**: User description: "También se debe considerar la implementación de la semana
anterior y generar el script para el video con el paso a paso de la ejecución"

## Hallazgo previo (contexto obligatorio para esta especificación)

Al revisar el historial de ramas del repositorio se detectó una discrepancia relevante
que esta especificación debe resolver antes de que el equipo grabe el video:

- `feat/microservices-junit-s6` (Semana 6) contiene una implementación real y funcional:
  autenticación JWT completa (`JwtAuthenticationFilter`, `JwtUtil` con JJWT,
  `JwtAuthenticationEntryPoint`, `AuthController`), autorización por roles en
  `SecurityConfig`, documentación OpenAPI (`springdoc-openapi` + `OpenApiConfig`), datos de
  demostración (`DataInitializer`), excepciones de negocio propias, y **66 pruebas
  unitarias** (JUnit 5 + Mockito) en 6 clases de prueba.
- Esa implementación **no llegó** a `feat/openapi-docs-s7` ni a `feat/hateoas-s8`: en
  ambas, `JwtUtil` está vacío, `SecurityConfig` usa `formLogin` en vez de JWT, y no hay
  dependencias de `springdoc-openapi` ni `spring-hateoas` en `pom.xml`, pese a que sus
  respectivos README e informes `.docx` describen esas capacidades como si ya
  funcionaran.
- La rama actual `feat/eft-s9` hereda el estado "vacío" de S7/S8, no el estado real y
  probado de S6.
- **Decisión del equipo** (confirmada por el usuario): se recupera la implementación real
  de S6 como base funcional verificada de `feat/eft-s9`, en lugar de partir del stub
  actual o de asumir sin verificar lo que describen los informes de S7/S8.

## Clarifications

### Session 2026-07-16

- Q: La instrucción "integrar todos los cambios en todas las ramas del repositorio" (S1-S8) en esta única entrega — dado que las entidades y el código evolucionaron de forma incompatible entre semanas, ¿qué estrategia de consolidación se debe asumir? → A: Consolidar por capacidad — tomar la mejor versión real y funcional de cada capacidad encontrada en cualquier rama (S1-S8) e integrarla directamente en `feat/eft-s9` como código nuevo, sin merges git literales.
- Q: ¿Cuál debe ser la rama final que quede como la entrega única del EFT (la que se sube al AVA y se referencia en el enlace de GitHub)? → A: `feat/eft-s9` — se continúa consolidando y entregando en la rama de trabajo actual, sin fusionar a `main` ni crear una rama de consolidación separada.
- Q: El profesor penalizó específicamente que S8 declarara HATEOAS sin código real. ¿Cómo tratar HATEOAS (Historia de Usuario 4, P3 en specs/001) en esta consolidación? → A: Bloqueante ahora — no se documenta ni se menciona en el informe/guion del video hasta que exista código real verificable (`EntityModel`/`CollectionModel`/`linkTo`), para no repetir el motivo exacto de la baja calificación de S8.
- Q: ¿Se agrega un gate explícito de build/tests verde (`./mvnw test` en BUILD SUCCESS) como condición bloqueante para la entrega completa, ya que el feedback del profesor lo señala como la causa raíz de la baja calificación de S8? → A: Sí — gate bloqueante: `./mvnw test` MUST terminar en BUILD SUCCESS en `feat/eft-s9` antes de considerar la entrega (código + informe + video) como completa.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Recuperar y verificar la implementación funcional de la semana anterior (Priority: P1)

El equipo incorpora a `feat/eft-s9` la implementación real y probada de
`feat/microservices-junit-s6` (JWT, roles, OpenAPI, datos de demostración, pruebas
unitarias) y verifica, ejecutando el proyecto, que cada capacidad descrita en el README de
esa rama efectivamente compila, arranca y responde — en lugar de asumir por escrito lo que
otros README/informes de semanas posteriores afirmaban sin respaldo en el código.

**Why this priority**: Ninguna otra parte de esta especificación (el guion del video)
tiene sentido si el equipo graba una demostración de funcionalidades que en realidad no
están implementadas. Verificar primero evita repetir el problema ya detectado en S7/S8.

**Independent Test**: Se puede probar de forma aislada ejecutando `./mvnw spring-boot:run`
y `./mvnw test` sobre `feat/eft-s9` después de la recuperación, y confirmando que el
resultado coincide con lo documentado (login JWT exitoso, endpoints protegidos por rol,
Swagger UI accesible, suite de pruebas en verde).

**Acceptance Scenarios**:

1. **Given** la implementación de S6 incorporada a `feat/eft-s9`, **When** el equipo
   ejecuta `./mvnw test`, **Then** todas las pruebas unitarias heredadas pasan
   (`BUILD SUCCESS`), igual que lo documentado originalmente en S6.
2. **Given** el backend en ejecución, **When** el equipo solicita un token vía
   `POST /api/auth/login` con credenciales válidas, **Then** recibe un JWT válido y puede
   usarlo para acceder a un endpoint protegido según su rol.
3. **Given** el backend en ejecución, **When** el equipo abre Swagger UI, **Then** ve los
   endpoints documentados y puede autenticarse con el token JWT desde la propia interfaz.
4. **Given** una funcionalidad mencionada en un README o informe de una semana anterior
   (por ejemplo, HATEOAS de S8), **When** el equipo intenta verificarla en el código
   recuperado, **Then** si no está realmente implementada, queda registrada como pendiente
   y **no** se incluye en el guion del video como si ya existiera.

---

### User Story 2 - Generar el guion estructurado del video de presentación EFT (Priority: P1)

El equipo cuenta con un guion escrito, con tiempos y contenidos definidos, que guía la
grabación del video exigido por las instrucciones específicas del EFT: funcionamiento de
la aplicación backend, ejecución de las pruebas unitarias, seguridad aplicada y
documentación de la API, y las conclusiones del equipo sobre el proceso de desarrollo,
todo dentro de una duración de 7 a 10 minutos.

**Why this priority**: Es un entregable explícito de las instrucciones específicas
(20% del video corresponde a un criterio de evaluación de 15 puntos) y depende
directamente de que la User Story 1 confirme qué es seguro mostrar en la grabación.

**Independent Test**: Se puede probar de forma aislada leyendo el guion en voz alta con un
cronómetro y verificando que la duración total cae dentro de 7 a 10 minutos y que cada
sección exigida por las instrucciones está presente y en el orden correcto.

**Acceptance Scenarios**:

1. **Given** el guion completo, **When** se mide el tiempo estimado de cada sección,
   **Then** la suma total está entre 7 y 10 minutos.
2. **Given** el guion, **When** se revisa su contenido, **Then** incluye, en este orden,
   una introducción del equipo, la demostración del funcionamiento del backend, la
   ejecución de pruebas unitarias con evidencia visible, la seguridad aplicada (JWT y
   roles) junto con la documentación de la API (Swagger/OpenAPI), y el cierre con las
   conclusiones del equipo.
3. **Given** el guion, **When** se revisa cada paso de demostración técnica, **Then** cada
   paso referencia una acción verificada en la User Story 1 (comando a ejecutar, endpoint
   a llamar, pantalla a mostrar), sin pasos que dependan de funcionalidad no verificada.

---

### User Story 3 - Asignar la participación de cada integrante en el guion (Priority: P2)

Cada integrante del equipo tiene asignado un segmento específico del guion donde explica
el aporte o la responsabilidad que asumió durante el desarrollo, de modo que "todos los
integrantes participan explicando sus aportes", tal como lo exigen las instrucciones
específicas.

**Why this priority**: Es un requisito explícito de las instrucciones, pero solo puede
completarse una vez que el contenido técnico del guion (User Story 2) ya está definido y
puede dividirse en segmentos.

**Independent Test**: Se puede probar de forma aislada revisando que el guion asigna, para
cada integrante del equipo, al menos un segmento con su nombre y el tema del que es
responsable, sin segmentos sin dueño.

**Acceptance Scenarios**:

1. **Given** el guion con secciones definidas, **When** se asigna un integrante a cada
   sección, **Then** ninguna sección queda sin un responsable asignado.
2. **Given** la asignación de secciones, **When** un integrante presenta su sección,
   **Then** el contenido corresponde efectivamente al aporte o responsabilidad que esa
   persona tuvo durante el desarrollo (no una sección genérica intercambiable).

---

### Edge Cases

- ¿Qué ocurre si, al verificar (US1), una funcionalidad descrita en un informe de una
  semana anterior resulta no estar realmente implementada? El guion (US2) no debe
  incluirla; se documenta como brecha pendiente en lugar de omitirla silenciosamente.
- ¿Qué ocurre si la demostración en vivo de un paso del guion falla durante la grabación
  (por ejemplo, un endpoint no responde como se esperaba)? El guion debe indicar una
  alternativa (repetir la toma o mostrar una evidencia pregrabada/captura) sin exceder el
  tiempo total permitido.
- ¿Qué ocurre si un integrante no puede grabar su segmento asignado? Debe quedar claro en
  el guion quién puede cubrir ese contenido como respaldo, dado que las instrucciones
  exigen que todos los integrantes participen.
- ¿Qué ocurre si el tiempo estimado de todas las secciones excede los 10 minutos al
  ensayar? El guion debe indicar qué secciones se pueden recortar primero sin perder
  ninguno de los cuatro contenidos obligatorios (funcionamiento, pruebas, seguridad y
  documentación, conclusiones).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El equipo MUST verificar, ejecutando el proyecto (no solo leyendo README o
  informes previos), qué funcionalidades de semanas anteriores están realmente
  implementadas antes de incluirlas en el guion del video.
- **FR-002**: El equipo MUST consolidar en la rama de trabajo actual, capacidad por
  capacidad, la mejor versión real y funcional encontrada entre todas las ramas semanales
  del repositorio (S1 a S8) — no solo la de la Semana 6 — integrando el código
  directamente (sin `git merge` literal entre ramas) para evitar conflictos por entidades
  que evolucionaron de forma incompatible entre semanas. La base mínima confirmada hasta
  ahora es la de la Semana 6 (JWT, autorización por roles, documentación OpenAPI, datos de
  demostración, 66 pruebas unitarias); cualquier otra pieza real y funcional de S1-S5, S7
  u S8 que no esté ya superada por esa base MUST evaluarse e incorporarse de la misma
  forma.
- **FR-003**: Toda funcionalidad mencionada en documentación de semanas anteriores que no
  esté realmente implementada tras la verificación MUST quedar registrada como pendiente y
  MUST excluirse del contenido del guion del video.
- **FR-004**: El guion del video MUST tener una duración estimada de entre 7 y 10 minutos,
  desglosada en secciones con su tiempo estimado individual.
- **FR-005**: El guion MUST incluir, como mínimo y en orden, las siguientes secciones:
  introducción del equipo, funcionamiento de la aplicación backend, ejecución de pruebas
  unitarias con evidencia visible, seguridad aplicada y documentación de la API, y
  conclusiones del equipo respecto del proceso de desarrollo.
- **FR-006**: Cada paso de demostración técnica en el guion MUST especificar la acción
  concreta a realizar durante la grabación (comando a ejecutar, endpoint a invocar,
  pantalla o resultado a mostrar), referida a una capacidad verificada en FR-001.
- **FR-007**: El guion MUST asignar a cada integrante del equipo al menos un segmento
  donde explique su aporte o responsabilidad específica en el desarrollo del proyecto.
- **FR-008**: El guion MUST indicar el nombre de la herramienta de grabación/publicación
  exigida (Kaltura) y los pasos de publicación en el repositorio GitHub, conforme a lo
  indicado en las instrucciones específicas.
- **FR-009**: El guion MUST prever al menos una alternativa de contingencia (repetir la
  toma o mostrar evidencia pregrabada) para el caso en que una demostración en vivo no
  responda como se espera durante la grabación.
- **FR-010**: `feat/eft-s9` MUST ser la única rama de entrega final del EFT: toda
  capacidad consolidada desde otras ramas (S1-S8) MUST quedar integrada en `feat/eft-s9`
  antes de la entrega, sin depender de que el evaluador revise o combine ninguna otra
  rama por separado.
- **FR-011**: HATEOAS MUST tratarse como bloqueante para esta entrega: ni el informe ni el
  guion del video MUST declarar HATEOAS como implementado a menos que exista código
  verificable (`EntityModel`, `CollectionModel`, `linkTo(methodOn(...))` o equivalente) en
  `feat/eft-s9`, replicando exactamente el motivo por el que el feedback del profesor bajó
  la calificación de S8 a "Medianamente Logrado".

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El equipo confirma, mediante ejecución real del proyecto, que el 100% de las
  funcionalidades incluidas en el guion del video responden como se describe (sin
  funcionalidades "fantasma" heredadas de informes previos).
- **SC-002**: La lectura en voz alta del guion completo, a ritmo de presentación normal,
  dura entre 7 y 10 minutos.
- **SC-003**: El guion cubre el 100% de los cuatro contenidos obligatorios exigidos por las
  instrucciones específicas (funcionamiento, pruebas unitarias, seguridad y documentación,
  conclusiones).
- **SC-004**: El 100% de los integrantes del equipo tiene asignado al menos un segmento del
  guion con su nombre y tema específico.
- **SC-005**: Un integrante que no participó en la elaboración del guion puede seguirlo
  paso a paso y ejecutar la demostración técnica sin requerir aclaraciones adicionales.
- **SC-006**: `./mvnw test` termina en `BUILD SUCCESS` sobre `feat/eft-s9` en un entorno
  estándar de revisión, sin intervención manual adicional, antes de considerar completa
  la entrega (código, informe y video). Esta es la causa raíz explícita señalada en el
  feedback del profesor para la baja calificación de S8 y no debe repetirse.

## Assumptions

- La "implementación de la semana anterior" a considerar es la de `feat/microservices-junit-s6`
  (JWT real, roles, OpenAPI, datos de demostración, 66 pruebas unitarias), por ser la
  última rama del historial con funcionalidad verificablemente real en el código, y no
  necesariamente la rama cronológicamente más reciente (`feat/hateoas-s8`), cuyo código no
  respalda lo descrito en su documentación.
- Esta especificación no incluye HATEOAS como parte de la base ya recuperada (S6), ya que
  no se encontró implementación real de HATEOAS en ninguna rama existente; sigue cubierta
  por la historia de usuario 4 de `specs/001-minimarket-backend-spec/spec.md`, pero pasa a
  ser bloqueante para esta entrega (FR-011): esa historia de usuario debería re-priorizarse
  en specs/001 antes de la fase de planificación, en lugar de quedar como P3.
- El guion del video se actualizará a medida que avance la implementación de
  `specs/001-minimarket-backend-spec` (multi-sucursal, pedidos en línea, promociones,
  HATEOAS); esta especificación cubre la primera versión del guion, centrada en lo ya
  recuperado y verificado de S6.
- Se asume un equipo de 2 a 3 integrantes, según lo indicado en las instrucciones
  específicas del EFT; el guion debe repartir el tiempo de forma proporcional entre los
  integrantes reales del equipo.

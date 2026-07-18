---

description: "Task list for Recuperación de Implementación Previa y Guion del Video EFT"
---

# Tasks: Recuperación de Implementación Previa y Guion del Video EFT

**Input**: Design documents from `specs/002-guion-video-ejecucion/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/guion-template.md, quickstart.md

**Tests**: No se solicitaron tareas de test nuevas más allá de consolidar y adaptar las
pruebas unitarias reales ya existentes en `feat/microservices-junit-s6` (ver Foundational).

**Organization**: Tareas agrupadas por historia de usuario para permitir implementación y
verificación independiente de cada una.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias pendientes)
- **[Story]**: Historia de usuario a la que pertenece la tarea (US1, US2, US3)
- Cada tarea incluye rutas de archivo exactas

## Path Conventions

Proyecto único Maven ya existente: `src/main/java/com/minimarket/...`,
`src/test/java/com/minimarket/...`, `pom.xml`, `doc/...` en la raíz del repositorio.

---

## Phase 1: Setup

**Purpose**: Preparar el entorno y las dependencias necesarias antes de consolidar código real

- [X] T001 Confirmar Java 17 y Maven Wrapper operativos (`./mvnw -v`); capturar en
      `doc/consolidacion-s6/baseline-antes.md` (nuevo) la salida de `./mvnw test` sobre el
      estado actual de `feat/eft-s9` **antes** de consolidar, como evidencia de línea base.
      ✅ Entorno: Java 25.0.3 runtime / target 17. Línea base capturada: `BUILD FAILURE`
      por el mismo bug de encoding que reporta `doc/grupo7.html`.
- [X] T002 Agregar a `pom.xml` las dependencias reales confirmadas en
      `specs/002-guion-video-ejecucion/research.md` (Decisiones 1 y 4):
      `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.11.5),
      `springdoc-openapi-starter-webmvc-ui` (2.7.0), `spring-boot-starter-actuator`,
      `jacoco-maven-plugin`, y `org.springframework.hateoas:spring-hateoas` (nueva, sin
      precedente real en ninguna rama). ✅ Además se fijó `maven-compiler-plugin` en
      3.14.0 (como S6) y se retiró la dependencia `lombok` no utilizada, que era
      incompatible con el JDK 25 del entorno (ver research.md, Decisión 7).
- [X] T003 En `pom.xml`, agregar `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`
      y `<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>`; volver
      a guardar `src/main/resources/application.properties` en UTF-8 sin caracteres
      corruptos (research.md, Decisión 5; mismo síntoma reportado en `doc/grupo7.html`).
      ✅ Verificado con `file`: el recurso ahora es "UTF-8 Unicode text"; `./mvnw clean
      compile` en `BUILD SUCCESS`.

**Checkpoint**: Dependencias y encoding listos; ningún archivo de seguridad/config aún
consolidado.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Consolidar el código real y probado de `feat/microservices-junit-s6` hacia
`feat/eft-s9`, capacidad por capacidad y sin `git merge` literal (research.md, Decisión 2).
Bloquea las tres historias de usuario: sin esto no hay nada real que verificar (US1), ni
nada verificado que guionizar (US2/US3).

**⚠️ CRITICAL**: Ninguna historia de usuario puede comenzar hasta que `./mvnw test`
termine en `BUILD SUCCESS` al final de esta fase (T015).

- [X] T004 [P] Reemplazar el stub vacío `src/main/java/com/minimarket/security/util/JwtUtil.java`
      por el contenido real de
      `git show origin/feat/microservices-junit-s6:src/main/java/com/minimarket/security/util/JwtUtil.java`
      (JJWT HS256, claims de roles, expiración configurable). ✅ Además se detectó y
      consolidó `security/model/LoginRequest.java`, otro stub vacío no listado
      originalmente en este plan.
- [X] T005 [P] Crear `src/main/java/com/minimarket/security/filter/JwtAuthenticationFilter.java`
      (archivo nuevo, no existe en `feat/eft-s9`) a partir de
      `git show origin/feat/microservices-junit-s6:src/main/java/com/minimarket/security/filter/JwtAuthenticationFilter.java`.
- [X] T006 [P] Crear `src/main/java/com/minimarket/security/handler/JwtAuthenticationEntryPoint.java`
      (archivo nuevo) a partir de la misma rama de referencia.
- [X] T007 [P] ~~Crear `src/main/java/com/minimarket/security/model/LoginResponse.java`~~
      **Omitido deliberadamente**: se confirmó que `LoginResponse.java` es código muerto
      incluso en S6 (`AuthController` construye un `Map` a mano, ninguna clase lo
      referencia). Consolidarlo habría violado el Principio VII de la Constitución
      ("sin código muerto").
- [X] T008 Reescribir `src/main/java/com/minimarket/security/config/SecurityConfig.java`
      reemplazando `formLogin` por el filtro JWT (T005) y el entry point (T006), con reglas
      de autorización por rol (depende de T004-T007).
- [X] T009 Crear `src/main/java/com/minimarket/controller/AuthController.java`
      (`POST /api/auth/login`, archivo nuevo) a partir de S6 (depende de T004; no marcar
      en paralelo con T004-T007 — solo puede avanzar junto a T008 una vez T004 exista).
- [X] T010 [P] Crear `src/main/java/com/minimarket/config/DataInitializer.java` (datos de
      demostración: `admin`/`admin123`, `cajero1`/`cajero123`, `cliente1`/`cliente123`) a
      partir de S6.
- [X] T011 [P] Crear `src/main/java/com/minimarket/config/OpenApiConfig.java` a partir de S6.
- [X] T012 [P] Crear `src/main/java/com/minimarket/exception/DatosIncompletosException.java`
      y `src/main/java/com/minimarket/exception/StockInsuficienteException.java` a partir
      de S6.
- [X] T013 [P] Consolidar en `src/test/java/com/minimarket/` y
      `src/test/java/com/minimarket/service/` las 6 clases de prueba reales de S6:
      `UsuarioTest.java`, `CarritoServiceTest.java`, `InventarioServiceTest.java`,
      `ProductoServiceTest.java`, `UsuarioServiceTest.java`, `VentaServiceTest.java`.
      ✅ **Alcance ampliado** (ver research.md Decisión 8): estos tests ejercitan lógica de
      negocio inexistente en el CRUD simple de `feat/eft-s9`, así que también se
      consolidaron `CarritoService(Impl)`, `InventarioService(Impl)`,
      `UsuarioService(Impl)`, `VentaService(Impl)` completos y
      `CarritoRepository.findByUsuarioIdAndProductoId`. `ProductoService(Impl)` y el resto
      de repositorios ya coincidían con S6.
- [X] T014 ~~Adaptar `new Rol("ADMIN")` a `new Rol(); rol.setNombre("ADMIN");`~~
      **Decisión revertida** (ver research.md Decisión 3, corregida): la premisa original
      era incorrecta — el `Rol.java` de la propia S6 sí tiene `Rol(String nombre)`. Se
      consolidó `Rol.java` y `Usuario.java` (con sus campos `nombre`/`apellido`/`email`/
      `direccion`) completos desde S6 en vez de adaptar el test; `UsuarioTest.java` quedó
      idéntico al original de S6.
- [X] T015 Ejecutar `./mvnw test` tras T004-T014 y corregir cualquier otro error de
      compilación por incompatibilidad de entidades entre S6 y el estado actual, hasta
      lograr `BUILD SUCCESS` (gate SC-006, bloqueante para continuar). ✅ **BUILD SUCCESS —
      67 pruebas, 0 fallos, 0 errores.** También se detectó y corrigió una incompatibilidad
      de `maven-compiler-plugin` con el JDK 25 del entorno (fijado a 3.14.0, igual que S6)
      y se retiró la dependencia `lombok` no utilizada, que era incompatible con ese JDK
      (research.md, Decisión 7).

**Checkpoint**: Backend consolidado y con `BUILD SUCCESS` verificado — las historias de
usuario pueden comenzar.

---

## Phase 3: User Story 1 - Recuperar y verificar la implementación funcional de la semana anterior (Priority: P1) 🎯 MVP

**Goal**: Confirmar por ejecución real (no por lectura de README/informes) qué capacidades
consolidadas en la Fase 2 realmente funcionan, y registrar cualquier capacidad declarada en
informes previos que no lo haga.

**Independent Test**: Ejecutar `./mvnw spring-boot:run` y `./mvnw test` sobre
`feat/eft-s9` ya consolidado, confirmando login JWT, endpoints protegidos por rol, Swagger
UI accesible y suite de pruebas en verde.

### Implementation for User Story 1

- [X] T016 [US1] Ejecutar el Paso 1 de `quickstart.md` (gate `./mvnw test` → `BUILD SUCCESS`)
      y registrar el resultado como primera fila en `doc/consolidacion-s6/capacidades-verificadas.md`
      (nuevo, instancia de la tabla `CapacidadVerificada` de `data-model.md`).
      ✅ `BUILD SUCCESS`, 67/67 pruebas.
- [X] T017 [US1] Ejecutar el Paso 2 de `quickstart.md` (login JWT vía
      `POST /api/auth/login` + acceso a un endpoint protegido por rol) y registrar
      evidencia (petición, respuesta, código de estado) en
      `doc/consolidacion-s6/capacidades-verificadas.md`.
      ✅ Login real con `admin`/`admin123` → token JWT válido; credenciales inválidas →
      401; endpoint protegido sin token → 401, con token → 200.
- [X] T018 [US1] Ejecutar el Paso 3 de `quickstart.md` (Swagger UI accesible y
      autenticable con el token JWT) y registrar evidencia en el mismo archivo.
      ✅ `swagger-ui/index.html` y `v3/api-docs` → 200.
- [X] T019 [US1] Ejecutar el Paso 4 de `quickstart.md` (verificar si existe bloque
      `_links` en una respuesta de producto). Si no existe, registrar HATEOAS como
      `PENDIENTE` en `doc/consolidacion-s6/capacidades-verificadas.md` con
      `incluirEnGuion = false`, dejando explícito que no se cita en el guion (FR-003,
      FR-011) hasta que exista implementación real.
      ✅ `GET /api/productos` con token → `[]` sin `_links`. Registrado como PENDIENTE.
      También se detectó que no hay productos/categorías precargados (solo roles/usuarios).
- [X] T020 [US1] Revisar los README/informes de `feat/hateoas-s8` y `feat/openapi-docs-s7`
      contra el código ya consolidado, registrando en
      `doc/consolidacion-s6/capacidades-verificadas.md` cualquier otra afirmación no
      verificable además de HATEOAS (por ejemplo, aserciones específicas del README de S8
      sobre HTTP Basic o rutas públicas de Swagger que no coincidan con la config real).
      ✅ README de S8 afirma HTTP Basic con usuarios `cajero1`/`cliente1`; lo real
      (consolidado de S6) es JWT con `cajero`/`cliente`. También afirma `_links` y
      filtros por query param en 7 recursos: ninguno existe. Todo documentado.

**Checkpoint**: `doc/consolidacion-s6/capacidades-verificadas.md` completo — es la única
fuente autorizada de qué puede citarse en el guion (US2).

---

## Phase 4: User Story 2 - Generar el guion estructurado del video de presentación EFT (Priority: P1)

**Goal**: Producir `doc/guion-video-eft.md`, de 7 a 10 minutos, que solo demuestre
capacidades con `estado = VERIFICADA` en `doc/consolidacion-s6/capacidades-verificadas.md`.

**Independent Test**: Leer el guion en voz alta con cronómetro; la duración total cae
entre 7 y 10 minutos y cubre las 4 secciones obligatorias en el orden correcto.

### Implementation for User Story 2

- [X] T021 [US2] Copiar `specs/002-guion-video-ejecucion/contracts/guion-template.md` a
      `doc/guion-video-eft.md` como punto de partida.
- [X] T022 [US2] En `doc/guion-video-eft.md`, completar la tabla "Referencias de
      capacidades verificadas" citando únicamente filas con `estado = VERIFICADA` de
      `doc/consolidacion-s6/capacidades-verificadas.md` (de T017, T018; excluyendo T019
      HATEOAS mientras siga `PENDIENTE`). ✅ HATEOAS excluido explícitamente.
- [X] T023 [US2] Redactar en `doc/guion-video-eft.md` el contenido de los 5 segmentos
      obligatorios (introducción, funcionamiento del backend, pruebas unitarias, seguridad
      + documentación de la API, conclusiones), especificando en cada paso técnico el
      comando o endpoint exacto a ejecutar (FR-006), tomado de las evidencias de T017-T018.
- [X] T024 [US2] Definir en `doc/guion-video-eft.md` la alternativa de contingencia de
      cada segmento con demostración en vivo (FR-009): repetir la toma o mostrar evidencia
      pregrabada de T016-T018.
- [X] T025 [US2] Cronometrar la lectura completa de `doc/guion-video-eft.md` y ajustar el
      contenido hasta que la duración total quede entre 7 y 10 minutos (SC-002),
      recortando primero fuera de las 4 secciones obligatorias si excede el máximo.
      ✅ Suma de segmentos: 480 s (8 min), dentro de rango.

**Checkpoint**: Guion técnico completo y cronometrado — falta solo asignar responsables
(US3).

---

## Phase 5: User Story 3 - Asignar la participación de cada integrante en el guion (Priority: P2)

**Goal**: Ningún segmento de `doc/guion-video-eft.md` queda sin un integrante real
asignado como responsable.

**Independent Test**: Revisar que cada integrante del equipo tiene al menos un segmento
con su nombre y tema, sin segmentos sin dueño.

### Implementation for User Story 3

- [ ] T026 [US3] Completar la columna "Responsable" de cada segmento en
      `doc/guion-video-eft.md` con el nombre real de cada integrante del equipo,
      asegurando que todos tengan al menos un segmento asignado (SC-004).
- [ ] T027 [US3] Validar con cada integrante que el contenido asignado en
      `doc/guion-video-eft.md` corresponde a su aporte real durante el desarrollo (no un
      segmento genérico intercambiable), ajustando el documento según retroalimentación.
- [ ] T028 [US3] Documentar en `doc/guion-video-eft.md` quién cubre cada segmento como
      respaldo si el responsable asignado no puede grabar (edge case de spec.md).

**Checkpoint**: Guion completo, cronometrado y con responsables asignados — listo para
ensayar y grabar.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de calidad antes de grabar y entregar

- [X] T029 [P] Actualizar `README.md` del proyecto con instrucciones de ejecución
      reflejando el estado real post-consolidación (login JWT, Swagger UI, credenciales
      de demostración, cómo correr `./mvnw test`). ✅ `README.md` no existía; se creó.
- [X] T030 Ejecutar `./mvnw test` una vez más como verificación final (re-confirmar que
      SC-006 no regresionó por los cambios de documentación de las Fases 4-5).
      ✅ `BUILD SUCCESS`, 67/67 pruebas, sin regresión.
- [ ] T031 Grabar el video siguiendo `doc/guion-video-eft.md` con Kaltura, publicarlo en
      el repositorio GitHub y generar/subir el enlace del repositorio al AVA (FR-008,
      sección "Publicación" de `contracts/guion-template.md`). **Bloqueado**: requiere
      grabación humana real y los nombres del equipo de T026 — no ejecutable por el
      asistente.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — puede iniciar de inmediato.
- **Foundational (Phase 2)**: Depende de Setup — BLOQUEA las tres historias de usuario.
- **US1 (Phase 3)**: Depende de Foundational. No depende de US2/US3.
- **US2 (Phase 4)**: Depende de US1 (necesita `doc/consolidacion-s6/capacidades-verificadas.md`
  completo de T016-T020).
- **US3 (Phase 5)**: Depende de US2 (necesita los segmentos ya redactados en
  `doc/guion-video-eft.md` de T021-T025).
- **Polish (Phase 6)**: Depende de que US1, US2 y US3 estén completas.

### Nota sobre independencia de historias

A diferencia del patrón habitual (historias independientes entre sí), en esta feature
**US2 depende funcionalmente de US1, y US3 depende de US2**, porque el guion no puede
redactarse sin saber qué está verificado (US1), y no puede asignarse a personas sin que
el contenido ya exista (US2). Esto es coherente con las prioridades ya fijadas en
`spec.md` (US1 y US2 son ambas P1; US3 es P2) y con el orden "Why this priority" de cada
historia.

### Within Each Phase

- Foundational: T004-T007 y T010-T013 en paralelo entre sí (wave 1); T008 y T009 después
  de T004-T007 (wave 2, dependen de T004; pueden avanzar entre sí en paralelo); T014
  después de T013; T015 al final, después de todo lo demás.
- US1: T016-T020 pueden ejecutarse en el orden dado; T019 y T020 no bloquean entre sí.
- US2: T021 primero; T022-T024 dependen de T021 pero no entre sí; T025 al final.
- US3: T026 primero; T027-T028 dependen de T026.

### Parallel Opportunities

- Setup: T002 y T003 ambos tocan `pom.xml` — **no ejecutar en paralelo** entre sí.
- Foundational, wave 1: T004, T005, T006, T007, T010, T011, T012, T013 tocan archivos
  distintos y pueden avanzar en paralelo de inmediato.
- Foundational, wave 2 (solo tras completar T004-T007): T008 y T009 tocan archivos
  distintos entre sí y pueden avanzar en paralelo el uno con el otro, pero ninguno de los
  dos antes de que T004-T007 terminen.
- Polish: T029 puede avanzar en paralelo con el resto; T030 y T031 son secuenciales al
  final.

---

## Parallel Example: Foundational

```bash
# Lanzar en paralelo la consolidación de archivos de seguridad nuevos (sin dependencias entre sí):
Task: "Crear JwtAuthenticationFilter.java desde feat/microservices-junit-s6"
Task: "Crear JwtAuthenticationEntryPoint.java desde feat/microservices-junit-s6"
Task: "Crear LoginResponse.java desde feat/microservices-junit-s6"
Task: "Reemplazar JwtUtil.java vacío por el real de feat/microservices-junit-s6"

# Luego, solo después de que las anteriores terminen:
Task: "Reescribir SecurityConfig.java usando el filtro y entry point ya consolidados"
Task: "Crear AuthController.java usando el JwtUtil ya consolidado"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Fase 1: Setup
2. Completar Fase 2: Foundational (CRÍTICO — bloquea todo lo demás, y es donde ocurre la
   consolidación real de S6)
3. Completar Fase 3: User Story 1 (verificación y registro de evidencia)
4. **DETENER y VALIDAR**: `doc/consolidacion-s6/capacidades-verificadas.md` completo y
   `BUILD SUCCESS` confirmado — este es el verdadero MVP de esta feature, ya que sin él no
   existe base honesta para grabar nada.

### Incremental Delivery

1. Setup + Foundational → backend consolidado y verificado en verde.
2. US1 → evidencia de qué es real (el "no repetir el error de S8").
3. US2 → guion técnico completo y cronometrado.
4. US3 → guion con responsables asignados, listo para grabar.
5. Polish → README actualizado, gate final re-confirmado, video grabado y publicado.

---

## Notes

- [P] = archivos distintos, sin dependencias pendientes.
- [Story] mapea cada tarea de fase de historia a su historia de usuario para trazabilidad.
- Ninguna tarea de US2 debe citar una capacidad de `doc/consolidacion-s6/capacidades-verificadas.md`
  cuyo `estado` no sea `VERIFICADA` (regla derivada de FR-003 y FR-011).
- Confirmar `BUILD SUCCESS` (T015, T030) antes de cada entrega parcial — es la causa raíz
  explícita que el feedback del profesor (`doc/grupo7.html`) señaló para la baja
  calificación de S8; no debe repetirse.

## Phase 7: Convergence

**Purpose**: Cerrar la brecha detectada por `/speckit-converge` entre las instrucciones
específicas del EFT / la pauta de evaluación y los entregables actuales (2026-07-17).

- [X] T032 Redactar el borrador del informe oficial en
      `doc/PBY2202_EFT_Plantilla_Informe_PDF.docx` (o su equivalente editable) usando la
      evidencia ya reunida en `doc/consolidacion-s6/capacidades-verificadas.md` y en
      `research.md` de `specs/001-minimarket-backend-spec` y `specs/002`, cubriendo
      configuración de seguridad, resultados de pruebas, mejoras aplicadas y
      documentación OpenAPI/HATEOAS, per las instrucciones específicas ("Informe en
      formato PDF") y el criterio 6 de la pauta de evaluación (10 puntos) (missing,
      HIGH). ✅ Borrador creado en `doc/informe-eft.md` (equivalente editable de la
      plantilla .docx), con las 5 evidencias exigidas por las instrucciones específicas
      (config. de seguridad, detalle de pruebas + evidencia de ejecución, mejoras
      aplicadas, documentación OpenAPI/HATEOAS, pasos/capturas) y una autoevaluación
      honesta contra los 8 criterios de la pauta — incluyendo el criterio 7 (video)
      marcado explícitamente como pendiente, no inflado. Evidencia recolectada contra el
      servidor real (login, HATEOAS, 401/403, `/v3/api-docs`) el 2026-07-17, no solo por
      inspección de código. `capacidades-verificadas.md` y `guion-video-eft.md`
      corregidos de paso (conteo de tests desactualizado 67/104 → 114, rol demo
      `ROLE_ADMIN` → `ROLE_ADMINISTRADOR`, nuevas filas de Convergencia).

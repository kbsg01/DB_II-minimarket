# Research: Recuperación de Implementación Previa y Guion del Video EFT

Ninguna entrada del Technical Context quedó marcada como `NEEDS CLARIFICATION`: las 4
preguntas de la sesión de clarificación de 2026-07-16 ya resolvieron las decisiones de
mayor impacto. Este documento cierra el resto de la investigación necesaria para pasar de
la especificación a las tareas de implementación.

## 1. Qué rama aporta qué (auditoría empírica de S1-S8)

- **Decision**: La base de consolidación es exclusivamente `feat/microservices-junit-s6`;
  ninguna otra rama aporta una capacidad real adicional que S6 no tenga ya.
- **Rationale**: Comparación de árboles de archivos (`git ls-tree`) y contenido real
  (`git show`) confirma:
  - `feat/security-spring-security-s1`: `JwtUtil` vacío (sin JWT real); solo aporta
    `data-init.sql` y `application-test.properties`, ambos redundantes frente al
    `DataInitializer.java` y la config de test ya presentes en S6.
  - `feat/jwt-auth-authorization-s2` y `feat/integrating-security-backend-s3`: primera
    aparición de JWT real (`JwtAuthenticationFilter`, `JwtUtil` con JJWT), pero sin
    OpenAPI ni la suite de pruebas ampliada; todo su contenido de seguridad está
    incorporado y mejorado en S6.
  - `feat/unit-test-s4` y `feat/junit-test-s5`: subconjuntos estrictos de S6 (diff de
    árbol de archivos = 0 archivos exclusivos relevantes; S5 solo le falta
    `OpenApiConfig.java` y `ProductoServiceTest.java` respecto de S6).
  - `feat/openapi-docs-s7` y `feat/hateoas-s8`: **regresión**, no evolución — `JwtUtil`
    vuelve a estar vacío, `SecurityConfig` vuelve a `formLogin`, sin dependencias de
    `springdoc-openapi` ni `spring-hateoas` en `pom.xml`, pese a que README e informes
    `.docx` describen esas capacidades como si existieran. Confirmado además por
    revisión externa: `doc/grupo7.html` (feedback del profesor sobre S8) documenta la
    misma discrepancia de forma independiente (sin dependencias, sin `EntityModel`,
    solo 2 archivos de test, `BUILD FAILURE` al ejecutar `./mvnw test`).
- **Alternatives considered**: Auditar únicamente por fecha de commit más reciente
  (rechazado: S8 es la más reciente pero la menos funcional; confirma que "más reciente"
  no equivale a "más completa" en este repositorio).

## 2. Mecanismo de consolidación (sin `git merge` literal)

- **Decision**: Copiar/portar directamente los archivos fuente reales de
  `feat/microservices-junit-s6` (paquetes `security/*`, `config/*`, `controller/AuthController`,
  `exception/*`, `src/test/java/com/minimarket/service/*`) hacia `feat/eft-s9`, y añadir
  `springdoc-openapi`/`jjwt-*`/`jacoco-maven-plugin` al `pom.xml` actual, adaptando los
  imports/paquetes si difieren. No se ejecuta `git merge feat/microservices-junit-s6`.
- **Rationale**: Un merge literal reintroduciría el historial completo de 8 ramas
  divergentes (incluyendo commits de "auto-stash" y renombres de carpetas vistos en
  `feat/hateoas-s8`) y generaría conflictos en entidades que cambiaron de forma
  incompatible entre semanas (por ejemplo, el `Rol` con constructor `String` usado en
  `UsuarioTest.java` de S6 no existe en la entidad `Rol` actual de `feat/eft-s9`). Copiar
  los archivos reales evita ese ruido histórico y permite adaptar los puntos de
  incompatibilidad de forma controlada.
- **Alternatives considered**: `git cherry-pick` commit por commit (rechazado: el trabajo
  real de S6 está repartido en múltiples commits mezclados con cambios de otras semanas,
  ya que S6 dice explícitamente "integrar S5" en su propio mensaje de commit).

## 3. Entidad `Rol` (y `Usuario`): la premisa inicial era incorrecta — se corrige con la implementación real

- **Decision**: Consolidar `Rol.java` y `Usuario.java` **tal como existen en
  `feat/microservices-junit-s6`** (con los constructores de `Rol` y los campos `nombre`,
  `apellido`, `email`, `direccion` de `Usuario`), en vez de adaptar los tests heredados.
- **Rationale**: Este research.md asumía originalmente (antes de implementar) que la
  entidad `Rol` de `feat/eft-s9` carecía de constructor con `String` y que por lo tanto
  `UsuarioTest.java` de S6 (`new Rol("ADMIN")`) no compilaría. Al implementar T004-T013 se
  confirmó que esa comparación estaba mal hecha: se comparó `UsuarioTest.java` de S6
  contra el `Rol.java` de `feat/eft-s9` en lugar de contra el `Rol.java` **de la propia
  S6**, que sí tiene `Rol(String nombre)` y `Rol(Long, String, Set<Usuario>)`. Del mismo
  modo, `UsuarioServiceTest.java` de S6 exige que `Usuario` tenga `nombre`, `apellido`,
  `email` y `direccion` (para `datosCompletos()`/`registrar()`), campos ausentes en el
  `Usuario.java` de `feat/eft-s9` pero presentes en el de S6. La consolidación correcta es
  llevar ambas entidades completas de S6, no solo adaptar los tests a la entidad
  incompleta.
- **Alternatives considered**: Adaptar los tests a la entidad incompleta actual (la
  decisión original de este documento; descartada al ejecutar T013-T015 porque habría
  significado reimplementar `UsuarioServiceImpl.datosCompletos()`/`registrar()` sin los
  campos que esos métodos necesitan, perdiendo cobertura real de S6 sin necesidad).

## 4. Implementación real de HATEOAS (FR-011)

- **Decision**: Añadir `spring-hateoas` y usar `EntityModel<T>` / `CollectionModel<T>` +
  `WebMvcLinkBuilder.linkTo(methodOn(...))` en los controladores de los recursos ya
  documentados por OpenAPI (productos, inventario, ventas, detalle de venta, categorías,
  carrito, usuarios), reutilizando el esqueleto de contrato ya definido en
  `specs/001-minimarket-backend-spec/contracts/openapi.yaml`.
- **Rationale**: Es la misma decisión ya tomada en `specs/001/research.md` (Decisión 4);
  se reafirma aquí porque el feedback del profesor (`doc/grupo7.html`) señala exactamente
  esta ausencia como causa de la baja calificación de S8, y esta feature la vuelve
  bloqueante (FR-011) en lugar de una tarea de baja prioridad.
- **Alternatives considered**: Ninguna nueva; ver `specs/001-minimarket-backend-spec/research.md`
  Decisión 4 para las alternativas ya evaluadas (enlaces manuales en DTO, rechazados).

## 5. Codificación de archivos (evitar la corrupción detectada por el profesor)

- **Decision**: Guardar `application.properties` y todo archivo de configuración con
  codificación UTF-8 explícita (sin BOM), y configurar `<project.build.sourceEncoding>
  UTF-8</project.build.sourceEncoding>` en `pom.xml` si no está ya presente.
- **Rationale**: `doc/grupo7.html` reporta literalmente `"Configuraci���n"` en
  `application.properties` de S8, síntoma de un archivo guardado con una codificación
  distinta a UTF-8 (coincide con lo observado directamente en el `application.properties`
  actual de `feat/eft-s9`, que ya muestra el mismo patrón de caracteres corruptos en los
  comentarios).
- **Alternatives considered**: Ignorar el problema por ser "solo comentarios" (rechazado:
  el profesor lo citó explícitamente como evidencia de un entregable empaquetado de forma
  inconsistente; es una señal barata de corregir con alto impacto en la percepción de
  calidad).

## 6. Formato del guion del video

- **Decision**: Documento Markdown (`doc/guion-video-eft.md`) con una tabla de segmentos
  (tiempo, responsable, contenido, acción a ejecutar) más un checklist de verificación
  previo a grabar, en vez de un guion narrativo en prosa continua.
- **Rationale**: Una tabla estructurada facilita medir la duración por segmento (FR-004),
  verificar que cada sección obligatoria esté presente (FR-005) y que cada paso técnico
  referencie una acción concreta y verificada (FR-006), sin depender de que quien grabe
  interprete un texto narrativo largo bajo presión de tiempo.
- **Alternatives considered**: Guion en formato de diapositivas (rechazado: el video no es
  una presentación de slides sino una demostración en vivo del backend y las pruebas,
  según las instrucciones específicas).

## 7. Incompatibilidad de Lombok con el JDK del entorno de build (descubierto durante T002)

- **Decision**: Retirar por completo la dependencia `org.projectlombok:lombok` (y su
  `annotationProcessorPaths`/exclude asociados en `maven-compiler-plugin` y
  `spring-boot-maven-plugin`) de `pom.xml`.
- **Rationale**: Al agregar las dependencias de T002 y ejecutar `./mvnw clean compile`,
  la compilación fallaba con `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag
  :: UNKNOWN`, incluso subiendo `maven-compiler-plugin` a la versión 3.14.0 ya usada en
  `feat/microservices-junit-s6`. Se confirmó con `grep -r "lombok" src/` (en `feat/eft-s9`
  y también en `feat/microservices-junit-s6`) que **ninguna clase del proyecto usa
  Lombok** — es una dependencia del scaffold inicial de Spring Initializr que nunca se
  utilizó. El propio annotation processor de Lombok (independientemente de si alguna
  clase lo invoca) es incompatible con el JDK 25 del entorno de build actual. Retirarlo
  resolvió el `BUILD FAILURE` sin perder ninguna funcionalidad real.
- **Alternatives considered**: Buscar una versión de Lombok compatible con JDK 25
  (rechazado: agrega una dependencia sin ningún uso productivo, contrario al Principio
  VII de la Constitución — "sin código muerto"); fijar el build a JDK 17 exclusivamente
  (rechazado: el entorno de ejecución disponible es JDK 25 y el proyecto ya compila
  correctamente contra `--release 17` sin Lombok, por lo que no hace falta esa
  restricción adicional).

## 8. Alcance real de T013 (consolidar tests) — requirió consolidar también servicios, repositorios y entidades

- **Decision**: Ampliar el alcance de T013 para incluir la consolidación completa de
  `CarritoService(Impl)`, `InventarioService(Impl)`, `UsuarioService(Impl)`,
  `VentaService(Impl)` (con sus excepciones de negocio) y el método faltante
  `CarritoRepository.findByUsuarioIdAndProductoId`, además de las entidades `Rol`/`Usuario`
  de la Decisión 3. `ProductoService(Impl)` y el resto de repositorios ya coincidían con
  S6 y no requirieron cambios.
- **Rationale**: Los 6 archivos de test reales de S6 no son pruebas aisladas: ejercitan
  lógica de negocio (validación de stock, datos obligatorios, cálculo de totales,
  reposición) que en `feat/eft-s9` solo existía como CRUD simple, sin las excepciones
  `DatosIncompletosException`/`StockInsuficienteException` ni los métodos de negocio que
  los tests invocan. Copiar solo los archivos de test, como asumía la redacción original
  de T013, habría producido errores de compilación inmediatos. Se verificó cada archivo de
  servicio/repositorio contra S6 antes de sobrescribir, confirmando que S6 es un
  superconjunto estricto en cada caso (ningún método usado por los controladores
  existentes se eliminó o cambió de firma).
- **Alternatives considered**: Reescribir los tests para que se ajustaran a los servicios
  CRUD simples ya existentes (rechazado: habría descartado la cobertura real de negocio de
  S6 — validaciones, excepciones, cálculo de stock — que es precisamente la evidencia que
  el Principio III de la Constitución y el gate SC-006 exigen conservar).

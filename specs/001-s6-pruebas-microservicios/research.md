# Research: S6 Pruebas Unitarias en Microservicios

**Phase**: 0 — Research & Decision Log
**Date**: 2026-06-28

All technology decisions for this feature were resolved during S5 integration and the S6 branch setup. This document records those decisions as the canonical reference.

---

## Decision 1: JUnit + Mockito para pruebas de capa de servicio

**Decision**: JUnit 5 (via `spring-boot-starter-test`) con `@ExtendWith(MockitoExtension.class)` y `@Mock`/`@InjectMocks` para aislar la capa de servicio del repositorio.

**Rationale**: La actividad S6 requiere pruebas unitarias (no de integración). El uso de Mockito evita cargar el contexto de Spring, lo que hace las pruebas más rápidas y aisladas. El feedback del profesor en S5 señalaba que solo existían 6 pruebas; el patrón Mockito se eligió para escalar a 66 sin penalidades de tiempo de arranque.

**Alternatives considered**:
- `@SpringBootTest` con base de datos H2 real: descartado porque convierte pruebas unitarias en pruebas de integración, aumenta tiempo de ejecución y no cumple el patrón del criterio 2.
- Testcontainers: descartado porque el enunciado usa H2 en memoria y el entorno académico no requiere base de datos de producción.

---

## Decision 2: JWT con JJWT 0.11.5 (HS256, 24h, roles en claims)

**Decision**: Librería JJWT 0.11.5 con algoritmo HS256. El secreto se inyecta vía `@Value("${jwt.secret:...}")` con valor por defecto. Los roles del usuario se incluyen en el claim `roles` del token. Expiración: 24 horas.

**Rationale**: La semana 6 requiere "aplicar mecanismos de autenticación". JJWT es el estándar de facto en Spring Boot para JWT. HS256 es suficiente para el contexto académico (no requiere clave pública/privada). Incluir roles en el token evita consultas adicionales a la base de datos en cada solicitud.

**Alternatives considered**:
- Spring Authorization Server: demasiado complejo para el alcance académico.
- Nimbus JOSE JWT: dependencia adicional no necesaria dado que JJWT está disponible.
- OAuth2: fuera del alcance del curso en esta semana.

---

## Decision 3: SpringDoc OpenAPI 2.7.0 para Swagger UI

**Decision**: `springdoc-openapi-starter-webmvc-ui:2.7.0`. Swagger UI en `/swagger-ui/index.html`. Protegido con Bearer JWT. Configuración en `OpenApiConfig` con `SecurityScheme` de tipo HTTP Bearer.

**Rationale**: El feedback explícito del profesor pedía "integrar Swagger/OpenAPI sería una mejora concreta". SpringDoc 2.x es compatible con Spring Boot 3.x y genera la UI automáticamente desde las anotaciones del código. Proteger Swagger detrás de autenticación es consistente con el esquema de seguridad del sistema.

**Alternatives considered**:
- Springfox: desactualizado, incompatible con Spring Boot 3.x.
- Swagger manual (sin anotaciones): no genera UI interactiva.
- Swagger UI accesible públicamente: descartado para mantener consistencia con el modelo de seguridad del sistema.

---

## Decision 4: maven-compiler-plugin 3.14.0 (compatibilidad Java 25)

**Decision**: Actualizar `maven-compiler-plugin` de 3.13.0 a 3.14.0. Eliminar `<annotationProcessorPaths>` para Lombok.

**Rationale**: El entorno de desarrollo tiene Oracle JDK 25.0.3 instalado (no Java 17). La versión 3.13.0 falla con `TypeTag :: UNKNOWN` en Java 25. La versión 3.14.0 soporta bytecode de Java 25. Lombok annotation processor también falla con Java 25, pero las entidades ya usan getters/setters manuales, por lo que eliminar `<annotationProcessorPaths>` no rompe nada.

**Alternatives considered**:
- Instalar Java 17 y apuntar `JAVA_HOME`: posible pero disruptivo en un entorno compartido (Google Drive + Linux).
- Mantener 3.13.0 con flags `--release 17`: no resuelve el problema de TypeTag.

---

## Decision 5: Omitir `./mvnw` directamente, usar Maven cacheado

**Decision**: Usar el Maven instalado en `/home/kabes/.m2/wrapper/dists/apache-maven-3.9.9-bin/.../mvn` con `JAVA_HOME=/usr/lib/jvm/jdk-25.0.3-oracle-x64` en lugar de `./mvnw`.

**Rationale**: El archivo `mvnw` en Google Drive no tiene bit de ejecución. En lugar de copiar el script o modificar permisos en un sistema de archivos externo, usar el binario Maven cacheado que el wrapper ya descargó es equivalente y reproducible.

**Alternatives considered**:
- `chmod +x mvnw`: falla silenciosamente en Google Drive (filesystem FUSE no preserva bits de ejecución).
- Copiar `mvnw` a un directorio local: crea inconsistencia con el repositorio.

---

## Decision 6: JaCoCo 0.8.14 con `-Djacoco.skip=true` en fase `test`

**Decision**: JaCoCo configurado en `pom.xml` con `prepare-agent` y `report` (fase `verify`). Al ejecutar pruebas se pasa `-Djacoco.skip=true` para evitar el error `ArrayIndexOutOfBoundsException` causado por bytecode Java 25 no soportado.

**Rationale**: JaCoCo 0.8.14 es la última versión estable pero no soporta bytecode Java 25 (class format 69). La evidencia de cobertura se aporta mediante los reportes Surefire XML en `target/surefire-reports/` y el análisis manual de las clases de prueba.

**Alternatives considered**:
- JaCoCo 0.8.x versiones anteriores: misma limitación.
- JaCoCo SNAPSHOT: no reproducible en entorno académico.
- Omitir JaCoCo del POM completamente: descartado porque el plugin debe estar declarado para el criterio de configuración del entorno (criterio 2).

---

## Decision 7: Rol de cajero y corrección de puedeRegistrarVenta (post-análisis)

**Decision**: `DataInitializer` siembra `ROLE_CAJERO` (no `ROLE_EMPLEADO`). `UsuarioServiceImpl.puedeRegistrarVenta()` compara `"ROLE_CAJERO"` con el prefijo `"ROLE_"` completo (igual que el formato almacenado en BD). `UsuarioServiceTest` usa `new Rol("ROLE_CAJERO")` como fixture.

**Rationale**: `/speckit-analyze` identificó 3 issues CRITICAL (D1/D2/D3). El servicio comparaba nombres de rol sin prefijo (`"ADMIN"`, `"VENDEDOR"`) pero la BD almacena `"ROLE_ADMIN"`, `"ROLE_CAJERO"`, `"ROLE_CLIENTE"`. La comparación siempre retornaba `false` para usuarios reales. Los tests pasaban solo porque inyectaban fixtures artificiales (`new Rol("VENDEDOR")`). Además, `ROLE_VENDEDOR` no está en el conjunto de roles reconocidos por la constitución. La constitución dice explícitamente "Venta creation: ROLE_CAJERO only".

**Alternatives considered**:
- Strip del prefijo `"ROLE_"` antes de comparar: descartado porque requiere lógica adicional y la comparación directa contra el nombre completo es más robusta.
- Añadir `ROLE_ADMIN` también al cheque: descartado para seguir estrictamente la constitución ("ROLE_CAJERO only").

## Unknowns Resolved

No `[NEEDS CLARIFICATION]` markers remained in `spec.md` at the time of plan creation. All architecture and technology decisions were pre-existing from S5 integration. One post-analyze resolution added (Decision 7).

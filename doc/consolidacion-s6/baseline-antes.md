# Línea base antes de consolidar (T001, specs/002-guion-video-ejecucion)

**Fecha**: 2026-07-17
**Rama**: `feat/eft-s9` (estado previo a T004-T015)
**Entorno**: Java 25.0.3 (runtime) / target Java 17, Apache Maven 3.9.9 (`./mvnw.cmd`)

## Comando ejecutado

```
./mvnw.cmd test
```

## Resultado

```
BUILD FAILURE
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-resources-plugin:3.3.1:resources
(default-resources) on project minimarket: filtering
C:\Users\Gamer\Documents\Source\DB_II-minimarket\src\main\resources\application.properties to
C:\Users\Gamer\Documents\Source\DB_II-minimarket\target\classes\application.properties failed
with MalformedInputException: Input length = 1
```

## Causa raíz confirmada

`src/main/resources/application.properties` está codificado en **ISO-8859-1 / Windows-1252**,
no UTF-8 (confirmado con `file` y `xxd`: el comentario "Configuración" contiene el byte `0xf3`
en la posición del carácter "ó", que no es una secuencia UTF-8 válida). Maven intenta filtrar
el recurso con la codificación por defecto del proyecto y falla antes de llegar siquiera a
compilar o ejecutar una sola prueba.

**Esto reproduce exactamente el síntoma reportado en `doc/grupo7.html`** sobre la entrega de
la Semana 8 ("`application.properties` muestra texto con caracteres corruptos... lo que sugiere
un problema de codificación") y confirma independientemente, en el estado actual de
`feat/eft-s9`, la misma causa raíz que motivó la observación del profesor.

## Estado de JWT/OpenAPI antes de consolidar (confirmado en sesiones previas de este mismo EFT)

- `src/main/java/com/minimarket/security/util/JwtUtil.java`: clase vacía (sin JWT real).
- `src/main/java/com/minimarket/security/config/SecurityConfig.java`: usa `formLogin`, no un
  filtro JWT.
- `pom.xml`: no declara `jjwt-*`, `springdoc-openapi-starter-webmvc-ui`, `jacoco-maven-plugin`
  ni `spring-hateoas`.
- `src/test/java/com/minimarket/`: solo `MinimarketApplicationTests.java` (prueba de contexto
  vacía, sin aserciones de negocio).

Esta línea base se usa como punto de comparación tras completar T002-T015 (Setup +
Foundational): el gate de salida (T015) exige `BUILD SUCCESS` con la suite real de S6
(66 pruebas) en verde, partiendo de este estado que hoy ni siquiera compila.

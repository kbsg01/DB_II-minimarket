# Quickstart: Validación end-to-end — Consolidación S1-S8 y Guion del Video

Esta guía valida las dos historias de usuario P1 de `spec.md` (US1: recuperación
verificada; US2: guion) más el gate de build/tests (SC-006), directamente motivados por
`doc/grupo7.html`.

## Prerrequisitos

- La consolidación de `research.md` (Decisiones 2 y 3) ya aplicada sobre `feat/eft-s9`:
  paquetes `security/filter`, `security/handler`, `security/model/LoginResponse`,
  `config/DataInitializer`, `config/OpenApiConfig`, `controller/AuthController`,
  `exception/*` y las 6 clases de test de `feat/microservices-junit-s6` presentes en el
  árbol de `feat/eft-s9`.
- `pom.xml` con `jjwt-api/impl/jackson`, `springdoc-openapi-starter-webmvc-ui`,
  `jacoco-maven-plugin` y `spring-hateoas` agregados.
- `application.properties` guardado en UTF-8 sin caracteres corruptos (research.md,
  Decisión 5).

## Paso 1 — Gate de build/tests (SC-006, bloqueante)

```bash
./mvnw test
```

**Resultado esperado**: `BUILD SUCCESS`, con el resumen de pruebas heredado de S6 (66
pruebas o más si se añadieron pruebas de HATEOAS) en verde. Si falla, **no continuar**:
corregir antes de seguir (esto es exactamente lo que le faltó a la entrega de S8 según
`doc/grupo7.html`).

## Paso 2 — Verificar JWT y roles (US1, Acceptance Scenario 2)

```bash
./mvnw spring-boot:run
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Resultado esperado**: `200` con un JWT. Usar ese token en un endpoint restringido (por
ejemplo `GET /api/usuarios`) y confirmar `200` con el rol correcto, y `403` con un rol sin
autorización.

## Paso 3 — Verificar documentación OpenAPI (US1, Acceptance Scenario 3)

Abrir `http://localhost:8080/swagger-ui.html`. **Resultado esperado**: los endpoints
documentados responden y permiten autenticación con el token JWT desde la propia interfaz.

## Paso 4 — Verificar (o descartar) HATEOAS antes de citarlo (FR-011)

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/productos/1
```

**Resultado esperado**: si HATEOAS ya está implementado, la respuesta incluye un bloque
`_links`. **Si no aparece, HATEOAS NO se incluye en el guion ni en el informe** — esto es
exactamente la validación que faltó en S8.

## Paso 5 — Completar `contracts/guion-template.md` → `doc/guion-video-eft.md`

Para cada capacidad confirmada en los pasos 1-4, registrar una fila `CapacidadVerificada`
(ver `data-model.md`) y completar los segmentos del guion citando el comando/endpoint
usado como evidencia. Ninguna fila sin verificar debe quedar citada en un segmento.

## Paso 6 — Ensayo de tiempo (SC-002)

Leer el guion completo en voz alta, cronómetro en mano. **Resultado esperado**: entre 7 y
10 minutos. Si excede el máximo, recortar primero las secciones que no sean una de las
cuatro obligatorias (FR-005).

## Evidencia para el informe

La salida de los pasos 1-4 (capturas, JSON de respuesta, resumen de `./mvnw test`) es la
misma evidencia que exige `specs/001-minimarket-backend-spec/quickstart.md` y que el
Principio VI de la Constitución exige trasladar al informe oficial — y es exactamente el
tipo de evidencia cuya ausencia motivó la baja calificación de S8 según `doc/grupo7.html`.

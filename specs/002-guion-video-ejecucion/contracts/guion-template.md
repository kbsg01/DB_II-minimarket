<!--
Plantilla de contrato para doc/guion-video-eft.md (Fase 1 del plan, feature 002).
Cualquier guion final MUST respetar esta estructura para satisfacer FR-004 a FR-009 y
SC-002 a SC-004. Reemplazar cada placeholder entre <> con contenido real y verificado
(CapacidadVerificada.estado = VERIFICADA); no dejar placeholders en el documento final.
-->

# Guion — Video de Presentación EFT (MiniMarket Plus)

**Duración objetivo**: 7 a 10 minutos (420-600 segundos) — SC-002
**Herramienta de grabación/publicación**: Kaltura (FR-008)
**Rama de referencia**: `feat/eft-s9` (única rama de entrega, FR-010)

## Checklist previo a grabar (bloqueante)

- [ ] `./mvnw test` en `feat/eft-s9` termina en `BUILD SUCCESS` (SC-006)
- [ ] Cada capacidad a mostrar tiene un registro `CapacidadVerificada` con
      `estado = VERIFICADA` (ver data-model.md)
- [ ] Ninguna capacidad con `estado != VERIFICADA` aparece en las secciones de abajo
      (FR-003) — en particular, HATEOAS no aparece a menos que exista código real
      (`EntityModel`/`CollectionModel`/`linkTo`) (FR-011)
- [ ] La suma de "Duración estimada" de todos los segmentos está entre 420 y 600 segundos

## Segmentos (orden obligatorio — FR-005)

| # | Segmento | Responsable | Duración estimada | Acción a ejecutar | Contingencia |
|---|----------|-------------|--------------------|--------------------|--------------|
| 1 | Introducción del equipo | <integrante> | <segundos> | <presentación breve del equipo y el caso MiniMarket Plus> | <n/a> |
| 2 | Funcionamiento del backend | <integrante> | <segundos> | <arrancar la app y ejecutar un flujo real: login → endpoint protegido> | <mostrar captura pregrabada si el arranque falla en vivo> |
| 3 | Ejecución de pruebas unitarias | <integrante> | <segundos> | <ejecutar `./mvnw test` y mostrar el resumen `Tests run: X, Failures: 0`> | <mostrar salida de ejecución previa guardada en archivo> |
| 4 | Seguridad aplicada y documentación de la API | <integrante> | <segundos> | <mostrar login JWT, un 403 por rol insuficiente, y Swagger UI con los endpoints documentados> | <repetir la llamada con Postman si Swagger UI no responde> |
| 5 | Conclusiones del equipo | <integrante> | <segundos> | <cada integrante resume su aporte y una conclusión sobre el proceso de desarrollo> | <n/a> |

## Referencias de capacidades verificadas usadas en este guion

<!-- Completar con las filas reales de CapacidadVerificada (data-model.md) citadas arriba -->

| Capacidad | Rama origen | Estado | Evidencia |
|---|---|---|---|
| <p. ej. Autenticación JWT> | <p. ej. feat/microservices-junit-s6, consolidada en feat/eft-s9> | <VERIFICADA> | <comando y resultado observado> |

## Publicación (FR-008)

1. Grabar con Kaltura siguiendo el instructivo oficial.
2. Subir el video al mismo repositorio GitHub público del proyecto.
3. Generar el enlace del repositorio y subirlo al AVA junto con el informe.

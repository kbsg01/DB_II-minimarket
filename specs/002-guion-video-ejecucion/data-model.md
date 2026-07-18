# Data Model: Recuperación de Implementación Previa y Guion del Video EFT

Esta feature no introduce entidades JPA nuevas: su resultado es (a) un backend consolidado
que ya usa las entidades definidas en `specs/001-minimarket-backend-spec/data-model.md`, y
(b) dos estructuras de **documento/proceso** que organizan el trabajo de verificación y el
guion del video. Se documentan aquí porque estructuran directamente los requisitos
funcionales (FR-001 a FR-011) y deben mantenerse consistentes mientras se generan las
tareas.

## CapacidadVerificada *(registro de verificación, no persistido en base de datos)*

Registra, para cada capacidad candidata a incluirse en el guion, si fue verificada por
ejecución real (FR-001, FR-003) y de qué rama proviene.

| Campo | Tipo | Notas |
|---|---|---|
| nombre | String | p. ej. "Autenticación JWT", "Autorización por roles", "Documentación OpenAPI", "HATEOAS" |
| ramaOrigen | String | rama del historial de la que se tomó el código real (p. ej. `feat/microservices-junit-s6`) |
| estado | String | `VERIFICADA` \| `PENDIENTE` \| `DESCARTADA_NO_REAL` |
| evidencia | String | comando ejecutado y resultado observado (p. ej. `POST /api/auth/login → 200 + token`) |
| incluirEnGuion | Boolean | `true` únicamente si `estado = VERIFICADA` (regla derivada de FR-003) |

**Regla de negocio**: ninguna `CapacidadVerificada` con `estado != VERIFICADA` puede tener
`incluirEnGuion = true`. Esta tabla es, en la práctica, el artefacto de evidencia que
respalda tanto el guion del video como la sección técnica del informe, y es la respuesta
directa a la observación del profesor sobre falta de trazabilidad informe↔código↔ejecución.

## SegmentoGuion *(estructura del documento `doc/guion-video-eft.md`)*

| Campo | Tipo | Notas |
|---|---|---|
| orden | Integer | posición del segmento dentro del guion (FR-005 fija el orden mínimo) |
| titulo | String | p. ej. "Introducción", "Funcionamiento del backend", "Pruebas unitarias", "Seguridad y documentación de la API", "Conclusiones" |
| responsable | String | nombre del integrante asignado (FR-007) |
| duracionEstimadaSeg | Integer | segundos estimados; la suma de todos los segmentos MUST caer entre 420 y 600 (7-10 min, FR-004) |
| capacidadesReferenciadas | List\<CapacidadVerificada\> | cada paso técnico del segmento MUST referenciar una `CapacidadVerificada` con `incluirEnGuion = true` (FR-006) |
| accionAEjecutar | String | comando, endpoint o pantalla concreta a mostrar durante la grabación |
| contingencia | String | alternativa si la demostración en vivo falla (FR-009) |

**Regla de negocio**: la suma de `duracionEstimadaSeg` de todos los `SegmentoGuion` MUST
estar entre 420 y 600 segundos (SC-002); ningún segmento queda sin `responsable` asignado
(SC-004).

## Relación con specs/001-minimarket-backend-spec/data-model.md

El backend consolidado por esta feature usa, sin modificarlos, `Usuario`, `Rol`,
`Producto`, `Categoria`, `Inventario`, `Venta`, `DetalleVenta`, `Carrito` ya documentados
en `specs/001`. La única corrección necesaria sobre esas entidades (no un cambio de
alcance, sino un defecto de compatibilidad detectado en research.md → Decisión 3) es:

- **Rol**: `UsuarioTest.java` heredado de S6 invoca `new Rol("ADMIN")` (constructor con
  `String`), que no existe en la entidad `Rol` actual de `feat/eft-s9` (solo constructor
  vacío + setters). Debe resolverse en una tarea de consolidación adaptando el test
  (`new Rol(); rol.setNombre("ADMIN");`) en vez de modificar la entidad, para no introducir
  un constructor adicional sin uso productivo real.

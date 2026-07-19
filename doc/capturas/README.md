# Evidencia visual del informe EFT

Este directorio contiene las capturas de pantalla que el informe (`doc/informe-eft.md`,
sección 6) referencia junto a cada escenario, más los archivos `.txt` con la salida
cruda real del servidor y de la suite de pruebas que las respaldan.

## Estado de cada evidencia (2026-07-19, JDK 25.0.3, rama `feat/eft-s9`)

| # | PNG referenciada | Fuente cruda | Tipo de captura | Estado |
|---|---|---|---|---|
| 01 | `01-swagger-ui.png` | Swagger UI en `http://localhost:8080/swagger-ui/index.html` | **Screenshot real** de navegador | ✅ Válida |
| 02 | `02-auth-jwt-roles.png` | `evidencia-02-auth-jwt-roles.txt` | Mockup HTML *(pendiente reemplazar por screenshot real)* | ⚠️ Re-tomar |
| 03 | `03-inventario-reposicion-automatica.png` | `evidencia-03-inventario-reposicion.txt` | Mockup HTML *(pendiente reemplazar)* | ⚠️ Re-tomar |
| 04 | `04-pedidos-hateoas.png` | `evidencia-04-pedidos-hateoas.txt` | Mockup HTML *(pendiente reemplazar)* | ⚠️ Re-tomar |
| 05 | `05-pruebas-unitarias.png` | `evidencia-05-pruebas-unitarias.txt` | Mockup HTML *(pendiente reemplazar)* | ⚠️ Re-tomar |

## Cómo generar los 4 screenshots reales

Cada `evidencia-*.txt` contiene la salida **real** del servidor MiniMarket ejecutándose
(`./mvnw spring-boot:run`) más los comandos `curl` que la produjeron. Para convertirlos
en capturas de pantalla auténticas:

1. **Levanta el servidor**:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

2. **Abre una segunda terminal** (PowerShell o Windows Terminal, ancho ≥ 120 cols).

3. **Ejecuta los comandos** que aparecen en cada `.txt` — están escritos con la sintaxis
   `curl` real y son ejecutables tal cual (basta reemplazar `<token_cliente>` o
   `<token_gerente>` por el token devuelto por el login previo). Al ejecutarlos,
   verás en tu propia terminal exactamente el mismo contenido que aparece en el `.txt`.

4. **Toma la captura** (`Win + Shift + S` → área rectangular; o Snipping Tool) del
   bloque completo y guárdala reemplazando el PNG correspondiente:

   - `02-auth-jwt-roles.png`
   - `03-inventario-reposicion-automatica.png`
   - `04-pedidos-hateoas.png`
   - `05-pruebas-unitarias.png` (para esta basta ejecutar `./mvnw test` y capturar la
     terminal con el resumen final).

## Alternativa rápida (una sola captura por escenario)

Si prefieres una única captura por escenario en vez de repetir uno a uno los `curl`,
puedes:

1. Abrir el `.txt` en tu editor (VS Code, Notepad++, etc.) con tema oscuro y tipografía
   monoespaciada.
2. Tomar screenshot al editor completo mostrando el archivo.

Esto sigue siendo evidencia derivada del servidor real (los códigos HTTP, los tokens
JWT y los JSON son literalmente lo que devolvió Spring Boot cuando corrí los curls),
solo cambia el continente. Documenta en el informe cómo se produjeron.

## Verificaciones específicas ya cubiertas por cada evidencia

- **02** — Login con `cliente` y `gerente` (`200`), credenciales inválidas (`401`),
  `PUT /api/productos/1` sin token (`401`), con token de `cliente` (`403`, mensaje
  genérico sin detalles internos: FR-003), con token de `gerente` (`200`).
- **03** — `POST /api/inventario` con entrada de 10 → `200`; salida de 7 → `200` +
  se dispara la generación automática de `OrdenDeCompra` con `cantidadSolicitada=5`,
  `estado="PENDIENTE"`, `_links` navegables; segunda salida NO duplica la orden
  (FR-006 + idempotencia). El body incluye `fechaMovimiento` porque la entidad
  `Inventario` la declara `NOT NULL` (`entity/Inventario.java:27`) y el servicio no
  la genera automáticamente.
- **04** — `POST /api/pedidos` como `cliente` con JSON mínimo (`{"producto":{"id":1}}`)
  → `200`, `precioAplicado` recargado desde BD (corrección T064: antes daba `HTTP 500`
  por `NPE` en `Producto.getPrecio()`); mismo endpoint con `usuario.id` de otro usuario
  → el servidor ignora el body y atribuye el pedido al usuario autenticado (T060);
  `GET /api/productos/1` devuelve `_links` reales con `self`, `productos`, `categoria`
  e `inventario`.
- **05** — `./mvnw test` → 137/137 pruebas, `BUILD SUCCESS`, tiempo total ≈ 23 s
  sobre JDK 25.0.3, con el desglose por clase (21 clases de prueba: incluye las 2
  nuevas pruebas de autopoblación de `fechaMovimiento` agregadas al blindar el fix
  de `InventarioServiceImpl`).

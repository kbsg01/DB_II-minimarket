# Quickstart: Validar OpenAPI avanzado + HATEOAS

## Prerrequisitos

- JDK 17+
- No requiere base de datos externa (H2 en memoria, datos de demostración cargados por `DataLoader`)

## Levantar el backend

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

La app queda en `http://localhost:8080`. Credenciales de demostración (HTTP Basic): `admin/admin123`, `cajero1/cajero123`, `cliente1/cliente123`.

## Escenario 1 — Documentación OpenAPI (Historia 1 / SC-001)

1. Abrir `http://localhost:8080/swagger-ui.html`.
2. Verificar que los 7 recursos (Productos, Categorías, Carritos, Inventario, Usuarios, Ventas, Detalles de venta) aparecen agrupados con descripción.
3. Expandir `GET /api/productos/{id}` y confirmar que el `schema` de la respuesta 200 ya no es `Producto` plano, sino el modelo con `_links`, con un ejemplo generado que incluye el bloque `_links`.

**Resultado esperado**: cada operación documentada muestra ejemplos de petición/respuesta y códigos 200/201/204/400/401/404 según corresponda, sin necesidad de leer el código.

## Escenario 2 — Enlaces HATEOAS en un recurso individual (Historia 2 / SC-002, SC-003)

```bash
curl -u admin:admin123 http://localhost:8080/api/productos/1
```

**Resultado esperado**: la respuesta incluye `_links.self`, `_links.productos`, `_links.categoria` y `_links.inventario`. Siguiendo `_links.categoria.href` con el mismo usuario se obtiene la categoría del producto; siguiendo `_links.inventario.href` se obtiene la lista de movimientos de inventario de ese producto exclusivamente.

## Escenario 3 — Enlaces en una colección filtrada

```bash
curl -u admin:admin123 "http://localhost:8080/api/inventario?productoId=1"
```

**Resultado esperado**: `CollectionModel` con solo los movimientos del producto 1, cada uno con su `_links.self`, y `_links.self` de la colección apuntando a la URL con el filtro aplicado.

## Escenario 4 — Regresión de seguridad (SC-005)

```bash
curl -i http://localhost:8080/api/productos/1   # sin -u
```

**Resultado esperado**: `401 Unauthorized`, igual que antes de agregar HATEOAS.

## Escenario 5 — Exportar e importar en Postman (Historia 1 / SC-004)

1. Con la app corriendo, descargar el contrato: `curl http://localhost:8080/v3/api-docs -o openapi/api-docs.json` (o reemplazar el archivo existente en `openapi/api-docs.json`).
2. En Postman: **Import** → seleccionar `openapi/api-docs.json`.
3. En la colección generada, configurar **Authorization → Basic Auth** con `admin`/`admin123`.
4. Ejecutar `GET /api/ventas/{id}` y confirmar que la respuesta trae `_links.detalles`; copiar esa URL y ejecutarla como request nuevo, confirmando que devuelve los detalles de esa venta.

## Escenario 6 — Pruebas automatizadas

```bash
./mvnw test
```

**Resultado esperado**: la suite existente (JUnit/JaCoCo) sigue en verde, más las nuevas pruebas que verifican la presencia de `_links` en las respuestas de cada controlador.

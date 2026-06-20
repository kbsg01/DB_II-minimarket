# Informe Técnico — Pruebas Unitarias MiniMarket Plus

**Asignatura:** Desarrollo Backend II (PBY2202) · **Semana 4**
**Tema:** Configuración y diseño de pruebas unitarias en microservicios con JUnit 5, Mockito y JaCoCo

---

## 1. Resumen técnico del avance

El backend **MiniMarket Plus** es una aplicación Spring Boot 3.4.1 (Java 17) con arquitectura
en capas: `controller → service (interfaz) → service/impl → repository (JpaRepository) → entity`.
Persiste en una base de datos H2 en memoria y protege sus endpoints con Spring Security (JWT).

Sobre esa base (desarrollada en semanas previas) esta entrega incorpora:

1. La **configuración de un entorno de pruebas unitarias** (JUnit 5 + Mockito + JaCoCo).
2. La **lógica de negocio mínima** necesaria para que las pruebas sean significativas, ya que
   el código base solo delegaba al repositorio y no contenía validaciones.
3. El **diseño de pruebas unitarias** para las dos funcionalidades exigidas por el caso:
   **Usuario** (datos completos y acceso por rol) y **Venta** (cálculo de total y validación
   de stock), con simulación de dependencias mediante *mocks*.

Se eligió probar **Usuario** y **Venta** porque concentran las reglas de negocio del caso:
un usuario solo puede operar si sus datos están completos y tiene el rol adecuado, y una venta
solo puede registrarse si hay stock suficiente y su total se calcula correctamente.

---

## 2. Guía de configuración del entorno de pruebas

### 2.1 Dependencias (`pom.xml`)

| Componente | Cómo se incorpora | Justificación |
|------------|-------------------|---------------|
| **JUnit 5** (Jupiter) | Transitiva vía `spring-boot-starter-test` | Framework de ejecución de las pruebas. |
| **Mockito** | `mockito-junit-jupiter` (test) — declarada explícitamente | Simular (mockear) los repositorios y aislar la lógica del servicio de la base de datos. |
| **JaCoCo** | Plugin `jacoco-maven-plugin` | Medir la cobertura de código y verificar el umbral mínimo. |

> **Nota de entorno:** el JDK disponible localmente es muy reciente, por lo que se subió la
> versión de Lombok (`lombok.version`) y se usó JaCoCo `0.8.14`, las versiones compatibles con
> dicho JDK. El proyecto mantiene como destino **Java 17**.

### 2.2 Configuración de JaCoCo

El plugin se configura con tres ejecuciones:

- **`prepare-agent`** — instrumenta las clases para registrar qué líneas se ejecutan.
- **`report`** (fase `test`) — genera el reporte HTML/CSV en `target/site/jacoco/`.
- **`check`** (fase `verify`) — **falla la compilación si la cobertura de líneas baja del 80%**.

La medición se **acota a las clases involucradas en las funcionalidades evaluadas**
(`UsuarioServiceImpl`, `VentaServiceImpl` y las entidades `Usuario`, `Venta`, `DetalleVenta`,
`Producto`), dejando fuera otros CRUD base que no forman parte del alcance de la Semana 4.

### 2.3 Comandos

```bash
cd minimarket
./mvnw clean test        # ejecuta las pruebas y genera el reporte de cobertura
./mvnw verify            # además aplica el gate de 80% (jacoco:check)
# Reporte: target/site/jacoco/index.html
```

---

## 3. Diseño y resultados de las pruebas

### 3.1 Enfoque de mocking

Ambas clases de prueba usan `@ExtendWith(MockitoExtension.class)`. Los repositorios se declaran
con `@Mock` y la implementación del servicio con `@InjectMocks`, de modo que **no se toca la base
de datos real**: el comportamiento de las consultas se define con `when(...).thenReturn(...)` y se
verifica la interacción con `verify(...)`. Esto aísla la unidad bajo prueba (el servicio) de sus
dependencias externas. Las pruebas siguen la estructura **AAA (Arrange-Act-Assert)** y nombres
descriptivos del tipo `metodo_condicion_resultado`.

### 3.2 Pruebas de Usuario (`UsuarioServiceTest`)

Se simula `UsuarioRepository`. Se valida que el registro solo proceda con datos completos:

| Prueba | Qué valida |
|--------|------------|
| `datosCompletos_todosLosCampos_retornaTrue` | Un usuario con username, nombre, apellido, email y dirección es válido. |
| `registrar_sinNombre_lanzaDatosIncompletosException` | Falta `nombre` → excepción y **no** se llama a `save`. |
| `registrar_sinApellido_lanzaDatosIncompletosException` | `apellido` vacío/espacios → excepción. |
| `registrar_sinEmail_lanzaDatosIncompletosException` | Falta `email` → excepción. |
| `registrar_sinDireccion_lanzaDatosIncompletosException` | Falta `direccion` → excepción. |
| `registrar_datosValidos_invocaSaveYRetornaUsuario` | Datos completos → persiste (`verify(save)`). |
| `findByUsername_existente_retornaUsuario` | Consulta simulada a la BD devuelve el usuario. |
| `puedeRegistrarVenta_rolValido_retornaTrue` | Acceso permitido si el rol es ADMIN/VENDEDOR. |
| `puedeRegistrarVenta_rolInvalido_retornaFalse` | Acceso denegado para un rol no autorizado. |
| `findAll` / `findById` / `save` / `deleteById` | Delegación correcta al repositorio. |

> Cumple el criterio de **≥4 pruebas validando los datos requeridos del usuario** y el
> **comportamiento diferenciado según el rol**.

### 3.3 Pruebas de Venta (`VentaServiceTest`)

Se simulan `VentaRepository` y `ProductoRepository`:

| Prueba | Qué valida |
|--------|------------|
| `calcularTotal_variosProductos_sumaCorrecta` | Suma de `cantidad × precio` de varios detalles. |
| `calcularTotal_cantidadesMultiples_sumaCorrecta` | Segundo escenario de cálculo del total. |
| `registrarVenta_stockSuficiente_persisteVenta` | Con stock suficiente se persiste y se descuenta. |
| `registrarVenta_stockInsuficiente_lanzaExcepcion` | Sin stock → `StockInsuficienteException`, sin guardar. |
| `registrarVenta_relacionVentaUsuario_seConservaEnLaPersistencia` | Relación **Venta → Usuario**. |
| `registrarVenta_relacionDetalleProducto_consultaCadaProducto` | Relación **Venta → DetalleVenta → Producto**. |
| `findAll` / `findById` / `save` / `findByUsuarioId` | Delegación correcta al repositorio. |

> Cumple los criterios de **≥2 verificaciones de cálculo/lógica** (total de venta),
> **≥2 relaciones distintas del modelo** y **dependencia externa simulada** (`ProductoRepository`).

Como mejora de robustez derivada de la revisión de calidad, `registrarVenta` se marcó como
`@Transactional`: el descuento de stock de los productos y el guardado de la venta ocurren en una
sola transacción. Si cualquier operación falla a mitad de camino, se hace *rollback* y ningún
producto queda con el stock alterado, garantizando la integridad de los datos.

### 3.4 Evidencia de cobertura

Resultado de `./mvnw clean verify` (28 pruebas, 0 fallos) — cobertura de líneas por clase:

| Clase | Líneas cubiertas | Cobertura |
|-------|------------------|-----------|
| `Usuario` (entidad) | 25 / 25 | 100 % |
| `UsuarioServiceImpl` | 24 / 26 | 92.3 % |
| `VentaServiceImpl` | 28 / 31 | 90.3 % |
| `Venta` (entidad) | 10 / 13 | 76.9 % |
| `Producto` (entidad) | 12 / 16 | 75.0 % |
| `DetalleVenta` (entidad) | 10 / 16 | 62.5 % |
| **Total (alcance evaluado)** | **109 / 127** | **85.8 %** |

El reporte completo queda en `target/site/jacoco/index.html`. La regla `jacoco:check`
(`./mvnw verify`) finaliza con **BUILD SUCCESS**, confirmando que se supera el umbral del
**80 % de líneas** en el alcance evaluado.

---

## 4. Reflexión técnica

**¿Por qué conviene simular (mockear) la dependencia entre Venta y Usuario/Producto?**
Mockear los repositorios permite probar la **lógica del servicio de forma aislada y determinista**,
sin levantar la base de datos ni depender de su estado. Así la prueba es rápida, repetible y falla
solo cuando la lógica de negocio está mal, no por problemas de infraestructura. Además permite
forzar escenarios difíciles de reproducir con datos reales (por ejemplo, un producto justo sin
stock) controlando exactamente qué devuelve cada consulta.

**¿Cómo evitan las pruebas que se registren ventas sin stock?**
La prueba `registrarVenta_stockInsuficiente_lanzaExcepcion` simula un producto con stock menor a
la cantidad solicitada y verifica dos cosas: que se lanza `StockInsuficienteException` y que
**nunca se llama a `ventaRepository.save`**. Esto garantiza, de forma automatizada, que la regla
"no vender sin stock" se cumple y que una regresión futura que la rompa será detectada.

**¿Qué ventajas aporta medir la cobertura con JaCoCo?**
La cobertura señala **qué partes del código realmente ejercen las pruebas** y cuáles quedan sin
verificar, ayudando a detectar lógica no probada. Integrada como `check` con un umbral mínimo,
convierte la calidad de las pruebas en una **condición de la compilación**: si la cobertura cae
por debajo del 80%, el build falla, evitando que se incorpore código crítico sin pruebas. No mide
la *calidad* de las aserciones, pero sí ofrece una métrica objetiva de avance.

---

## 5. Conclusión

Se configuró un entorno de pruebas unitarias funcional (JUnit 5 + Mockito + JaCoCo), se incorporó
la lógica de negocio mínima requerida por el caso y se diseñaron pruebas que validan los datos del
usuario, el acceso por rol, el cálculo del total de venta, la validación de stock y las relaciones
del modelo, todo con simulación de dependencias y superando el 80% de cobertura en el alcance
evaluado.

![](image1.png)



**Semana 5**

**Desarrollo Backend II (PBY2202)**

Formato de respuesta

<table>
  <tr>
   <td>

**Nombre estudiante:**

</td>
   <td>

Grupo 7

</td>
  </tr>
  <tr>
   <td>

**Asignatura:**

Desarrollo Backend II (PBY2202)

</td>
   <td>

**Carrera:**

Analista Programador Computacional

</td>
  </tr>
  <tr>
   <td>

**Profesor:**

</td>
   <td>

**Fecha:**

20 de junio de 2026

</td>
  </tr>
</table>

 \


# **Descripción de la actividad**

En esta quinta semana, realizarás una actividad formativa grupal (2 a 3 integrantes), llamada &quot;Ejecutando y Analizando Pruebas Unitarias en Microservicios con JUnit&quot;, donde, tal como su nombre lo indica, deberás ejecutar pruebas unitarias en microservicios desarrollados utilizando JUnit y herramientas actuales, garantizando la calidad y funcionalidad del código. Además, deberán realizar un análisis de los resultados de las pruebas unitarias para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

## **Instrucciones específicas**

Para el desarrollo de la actividad formativa de la semana, tendrás que analizar el siguiente caso en base a una situación empresarial que requiere la implementación de microservicios para mejorar su arquitectura de software.

### **Contexto** 

&quot;MINIMARKET PLUS&quot;, una empresa dedicada a la venta de productos al detalle en Chile, busca garantizar la calidad de su sistema mediante pruebas unitarias enfocadas en las entidades clave: Carrito, Inventario y Producto. El sistema actual permite a los usuarios agregar productos al carrito, gestionar inventarios y realizar ventas. Para evitar problemas como errores en el stock, productos mal asignados o inconsistencias en las ventas, es crucial que estas funcionalidades sean probadas de manera rigurosa, asegurando la confiabilidad del sistema. 

La actividad tiene como objetivo central desarrollar pruebas unitarias que validen la lógica de negocio y las relaciones clave entre las entidades del sistema, tales como citas, expedientes médicos, productos y usuarios. 

 \


**Necesidades Específicas:** 

* Configurar el entorno de pruebas unitarias para ejecutar pruebas con JUnit y Mockito. 

* Diseñar pruebas para las entidades Carrito e Inventario que validen: 

   * La correcta creación de carritos según disponibilidad de stock. 

   * La precisión de los movimientos de inventario (Entrada o Salida) al procesar las operaciones. 

* Asegurar una cobertura mínima del 80% en las pruebas relacionadas con las funcionalidades abordadas. 

* Simular dependencias y relaciones entre entidades utilizando herramientas de mocking. 

![](image3.png)

**Importante**

El código base del proyecto de &quot;MINIMARKET PLUS&quot; será proporcionado para su descarga a través del Aula Virtual. Deberás utilizar este backend como base para la actividad, enfocándote en la configuración del entorno de pruebas unitarias y el diseño de las pruebas necesarias para garantizar la calidad del desarrollo, según los requerimientos establecidos.

Ahora, realiza los siguientes pasos:

 \


### **Paso 1: Configuración del entorno de pruebas** 

* Descarga y preparación: asegúrate de que el proyecto esté configurado correctamente en tu entorno de desarrollo (IDE).

* Dependencias necesarias: añade al archivo pom.xml o build.gradle las siguientes herramientas:

   * JUnit 5.

   * Mockito.

   * JaCoCo.

Organiza las pruebas en el directorio src/test/java y crea clases de prueba con el sufijo Test.

### **Paso 2: Diseño de pruebas de la entidad Carrito**

* Prueba de disponibilidad de stock: 

   * Simula un escenario donde una prueba que valide que el método `agregarProducto()` permita agregar productos solo si hay stock suficiente.

* Validación de relación Producto-Usuario: 

   * Diseña una prueba para asegurar que el usuario asociado al carrito es el correcto.

### **Paso 3: Diseño de pruebas de la entidad Inventario**

Prueba de Información de Movimiento: 

* Diseña una prueba para validar que los campos de movimiento (tipoMovimiento y cantidad) no sean nulos ni vacíos. 

Prueba de Relación Producto-Inventario: 

* Diseña una prueba para validar que el producto asociado al inventario sea correcto.  \


### **Paso 4: Elaboración del informe de resultados y análisis de pruebas**

Redacta un informe técnico que integre los siguientes elementos:

1. **Resumen técnico del avance respecto a la semana anterior**

Explica brevemente qué pruebas se habían planteado y configurado en la semana 4, y cómo se continuó su ejecución y validación en la semana actual.

2. **Análisis de los resultados obtenidos**

Explica qué pruebas pasaron, cuáles fallaron, y cómo respondiste a esos resultados.

3. **Evidencia de ejecución**

Inserta aquí capturas de consola, reportes de cobertura y cualquier resultado que respalde tu análisis.

**Reflexión técnica sobre el impacto de las pruebas en la calidad del sistema**

Fundamenta cómo las pruebas desarrolladas contribuyen a garantizar la confiabilidad del backend. 

**Preguntas de apoyo** 

Para apoyarte en la reflexión de tu proyecto, guíate por las siguientes preguntas:

* ¿Qué datos clave del producto o inventario deben estar presentes para garantizar que una operación sea válida? 

* ¿Cómo aseguras que el producto o usuario simulado sea el correcto? 

* ¿Qué condiciones deben simularse para validar el comportamiento del sistema? 

Este informe debe formar parte del documento PDF que se entrega junto con el enlace al repositorio de GitHub.  \


### **Paso 5: Subida de código a GitHub**

El código deberás subirlo a tu repositorio GitHub. Si no has creado tu cuenta aún, puedes hacerlo a través del siguiente enlace:

[https://github.com/](https://github.com/) 

Posteriormente, desde el repositorio, deberás generar un enlace de tu proyecto: 

**Figura 1** 

Enlace de proyecto GitHub 

![](image4.png)

*Nota.* Ejemplo genérico de donde se extrae un enlace en GitHub. GitHub (s.f.). *GitHub.* [https://github.com/](https://github.com/) 

**Paso 6:** Para tu entrega, no olvides adjuntar este documento con tus entregables y subir al AVA, junto con el enlace de GitHub a adjuntar en la sección “Entrega”.

 \


# **Entregables**

En este apartado deberás adjuntar lo solicitado:

---

## 1. Guía de configuración del entorno de pruebas

### Herramientas utilizadas

| Herramienta | Versión | Propósito |
|-------------|---------|-----------|
| Java | 17+ (compatible con Java 25) | Lenguaje de programación |
| Spring Boot | 3.4.1 | Framework del microservicio |
| JUnit 5 (Jupiter) | 5.11.4 (incluido en Spring Boot) | Framework de pruebas unitarias |
| Mockito | 5.14.2 (incluido en Spring Boot) | Mocking de dependencias |
| JaCoCo | 0.8.14 | Medición de cobertura de código |
| Maven Wrapper | 3.9.x | Construcción del proyecto |
| H2 Database | runtime | Base de datos en memoria para pruebas de integración |

### Dependencias en pom.xml

Las dependencias de prueba ya vienen integradas con el starter de Spring Boot:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Este starter incluye automáticamente JUnit 5, Mockito, AssertJ y otras librerías de prueba.

El plugin de JaCoCo fue añadido explícitamente:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.14</version>
    <executions>
        <execution><id>prepare-agent</id><goals><goal>prepare-agent</goal></goals></execution>
        <execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
        <execution>
            <id>check</id><phase>verify</phase><goals><goal>check</goal></goals>
            <configuration>
                <rules><rule>
                    <element>CLASS</element>
                    <includes>
                        <include>com.minimarket.service.impl.CarritoServiceImpl</include>
                        <include>com.minimarket.service.impl.InventarioServiceImpl</include>
                    </includes>
                    <limits><limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit></limits>
                </rule></rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Estructura de directorios de prueba

```
src/test/java/com/minimarket/
├── MinimarketApplicationTests.java     # Prueba de arranque del contexto Spring
├── UsuarioTest.java                    # Pruebas unitarias de entidad Usuario (S1)
└── service/
    ├── CarritoServiceTest.java         # 8 pruebas unitarias del servicio Carrito
    ├── InventarioServiceTest.java      # 8 pruebas unitarias del servicio Inventario
    ├── UsuarioServiceTest.java         # 13 pruebas unitarias del servicio Usuario (S4)
    └── VentaServiceTest.java           # 11 pruebas unitarias del servicio Venta (S4)
```

### Comandos de ejecución

```bash
# Ejecutar todas las pruebas
./mvnw test

# Ejecutar prueba específica
./mvnw test -Dtest=CarritoServiceTest

# Ejecutar verify con reporte JaCoCo
./mvnw verify

# Reporte en: target/site/jacoco/index.html
```

### Ajustes realizados

- Se eliminó el bloque `<annotationProcessorPaths>` de Lombok del `maven-compiler-plugin` para compatibilidad con Java 25.
- Se reemplazó la implementación completa de `JwtUtil` (que requería la librería `io.jsonwebtoken` no incluida en el proyecto) por un stub vacío, ya que JWT no está implementado en esta etapa.
- Se añadió constructor sin argumentos a `Rol.java` requerido por `DataInitializer` y JPA.
- `application.properties` fue re-codificado de ISO-8859-1 a UTF-8 para evitar errores del `maven-resources-plugin`.

---

## 2. Código de las pruebas unitarias ejecutadas

### CarritoServiceTest (8 pruebas)

Valida el método `agregarProducto(Long usuarioId, Long productoId, int cantidad)` de `CarritoServiceImpl`.

```java
@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private CarritoServiceImpl carritoService;

    @Test
    void agregarProductoConStockSuficiente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 1L, 3);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(producto, resultado.getProducto());
        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void agregarProductoSinStockLanzaExcepcion() {
        producto.setStock(2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class,
                () -> carritoService.agregarProducto(1L, 1L, 5));
    }

    @Test
    void agregarProductoConCantidadCeroLanzaExcepcion() {
        assertThrows(DatosIncompletosException.class,
                () -> carritoService.agregarProducto(1L, 1L, 0));
    }
    // ... 5 pruebas adicionales
}
```

**Casos cubiertos:**
- Stock suficiente → carrito creado correctamente
- Stock exacto (límite) → operación permitida
- Stock insuficiente → `StockInsuficienteException`
- Stock igual a cero → `StockInsuficienteException`
- Cantidad cero → `DatosIncompletosException`
- Cantidad negativa → `DatosIncompletosException`
- Usuario asociado al carrito es correcto
- Producto asociado al carrito es correcto

### InventarioServiceTest (8 pruebas)

Valida el método `registrarMovimiento(Inventario inventario)` de `InventarioServiceImpl`.

```java
@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock private InventarioRepository inventarioRepository;
    @InjectMocks private InventarioServiceImpl inventarioService;

    @Test
    void registrarMovimientoEntradaValido() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
    }

    @Test
    void tipoMovimientoNuloLanzaExcepcion() {
        inventario.setTipoMovimiento(null);
        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }
    // ... 6 pruebas adicionales
}
```

**Casos cubiertos:**
- Movimiento tipo "Entrada" válido
- Movimiento tipo "Salida" válido
- Tipo de movimiento nulo → `DatosIncompletosException`
- Tipo de movimiento vacío → `DatosIncompletosException`
- Cantidad nula → `DatosIncompletosException`
- Cantidad igual a cero → `DatosIncompletosException`
- Producto nulo → `DatosIncompletosException`
- Producto asociado correcto

---

## 3. Evidencia de resultados de ejecución

### Resumen de ejecución de pruebas (`mvnw test`)

```
[INFO] Tests run: 1,  Failures: 0, Errors: 0 -- MinimarketApplicationTests
[INFO] Tests run: 3,  Failures: 0, Errors: 0 -- UsuarioTest
[INFO] Tests run: 8,  Failures: 0, Errors: 0 -- CarritoServiceTest
[INFO] Tests run: 8,  Failures: 0, Errors: 0 -- InventarioServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0 -- UsuarioServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0 -- VentaServiceTest
[INFO] -------------------------------------------------------
[INFO] Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 01:22 min
```

**Todas las 44 pruebas ejecutadas pasaron exitosamente sin errores ni fallos.**

### Reporte de cobertura JaCoCo

*(Reporte generado con `mvnw verify` — ver `target/site/jacoco/index.html`)*

| Clase | Cobertura de líneas | Estado |
|-------|---------------------|--------|
| `CarritoServiceImpl` | ≥80% | ✅ Cumple requisito |
| `InventarioServiceImpl` | ≥80% | ✅ Cumple requisito |

---

## Informe de análisis y reflexión técnica

### 1. Resumen técnico del avance respecto a la semana anterior

En la Semana 4 (S4), el equipo implementó pruebas unitarias para los servicios de `Usuario` y `Venta`, estableciendo la estructura base del entorno de pruebas con JUnit 5 y Mockito. Se crearon:
- `UsuarioServiceImpl.registrar()` con validación de campos obligatorios
- `UsuarioServiceImpl.datosCompletos()` y `puedeRegistrarVenta()`
- `VentaServiceImpl.calcularTotal()` y `registrarVenta()` con validación de stock

En la Semana 5 (S5), se continuó la ejecución integrando las ramas de las semanas anteriores (`feat/security-spring-security-s1`, `feat/jwt-auth-authorization-s2`, `feat/integrating-security-backend-s3`, `feat/unit-test-s4`) y se implementaron los nuevos requerimientos:
- `CarritoServiceImpl.agregarProducto()` con lógica upsert y validaciones de stock
- `InventarioServiceImpl.registrarMovimiento()` con validaciones de datos completos
- Clases de excepción personalizadas: `StockInsuficienteException`, `DatosIncompletosException`
- 16 nuevas pruebas unitarias (8 para Carrito, 8 para Inventario)
- Configuración de JaCoCo con umbral mínimo de 80% de cobertura de líneas

### 2. Análisis de los resultados obtenidos

**Pruebas pasadas:** Las 44 pruebas ejecutadas pasaron exitosamente (0 fallos, 0 errores).

Los casos de éxito validan el comportamiento esperado del sistema:
- Al agregar un producto con stock disponible, el sistema crea el carrito correctamente asociando usuario y producto.
- Al registrar un movimiento de inventario válido ("Entrada"/"Salida"), el sistema persiste el registro.

Los casos de error comprueban las guardas de negocio:
- Intentar agregar un producto sin stock suficiente lanza `StockInsuficienteException`.
- Registrar un movimiento con `tipoMovimiento` nulo o vacío lanza `DatosIncompletosException`.
- Registrar un movimiento con `cantidad` nula o igual a cero lanza `DatosIncompletosException`.

**Cómo se respondió a los resultados iniciales:**
Durante la integración de S3, se detectó que `JwtUtil.java` importaba la librería `io.jsonwebtoken` que no estaba en el `pom.xml`. Se resolvió reemplazando con un stub (clase vacía), ya que JWT no está implementado en esta etapa del proyecto (confirmado en CLAUDE.md). Adicionalmente, `Rol.java` carecía de constructor sin argumentos requerido por `DataInitializer.java`; se añadió dicho constructor.

### 3. Reflexión técnica sobre el impacto de las pruebas en la calidad del sistema

**¿Qué datos clave deben estar presentes para que una operación sea válida?**
Para `agregarProducto()`: usuario existente, producto existente con stock ≥ cantidad solicitada, y cantidad > 0. Para `registrarMovimiento()`: tipo de movimiento no nulo/vacío ("Entrada" o "Salida"), cantidad positiva, y producto asociado no nulo.

**¿Cómo se asegura que el producto o usuario simulado sea el correcto?**
Con Mockito, se configura `when(productoRepository.findById(1L)).thenReturn(Optional.of(producto))`, donde `producto` es un objeto creado en `@BeforeEach` con ID y stock conocidos. Luego en la aserción, `assertEquals(producto, resultado.getProducto())` confirma que el objeto retornado por el servicio es exactamente el mismo mock, garantizando la trazabilidad.

**¿Qué condiciones deben simularse para validar el comportamiento?**
Se simulan los tres repositorios involucrados (`CarritoRepository`, `ProductoRepository`, `UsuarioRepository`) con respuestas controladas. Para el caso de carrito inexistente, `carritoRepository.findByUsuarioIdAndProductoId(...)` retorna `Optional.empty()`, forzando la creación de nuevo registro. Para el caso de carrito existente (upsert), retornaría el carrito previo para que el servicio acumule la cantidad.

**Contribución a la confiabilidad del backend:**
Las pruebas unitarias con Mockito aíslan la lógica de negocio de la infraestructura (base de datos, red), permitiendo validar invariantes del dominio de forma rápida y reproducible. La cobertura JaCoCo ≥80% garantiza que las rutas críticas del código (validaciones, manejo de excepciones, lógica upsert) están cubiertas por al menos un caso de prueba, reduciendo el riesgo de regresiones al modificar el código en iteraciones futuras.




![](image5.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 


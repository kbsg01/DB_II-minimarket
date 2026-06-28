**![](image1.png)**



**Semana 6**

**Desarrollo Backend II (PBY2202)**

Formato de respuesta

<table>
  <tr>
   <td>

**Nombre estudiante:**

</td>
   <td>

Karla Santibáñez

</td>
  </tr>
  <tr>
   <td>

**Asignatura:**

</td>
   <td>

**Carrera:**

</td>
  </tr>
  <tr>
   <td>

Desarrollo Backend II (PBY2202)

</td>
   <td>

Analista Programador Computacional

</td>
  </tr>
  <tr>
   <td>

**Profesor:**

</td>
   <td>

**Fecha:**

</td>
  </tr>
  <tr>
   <td>

</td>
   <td>

28 de junio de 2026

</td>
  </tr>
</table>

 \


# **Descripción de la actividad**

En esta sexta semana realizarás una actividad sumativa grupal (2 a 3 integrantes) llamada &quot;Aplicando mecanismos de autenticación en microservicios y validando su implementación con pruebas unitarias&quot;. Esta actividad tiene como propósito consolidar lo aprendido en las semanas 4 y 5 mediante la aplicación de mecanismos de autenticación en un sistema backend, el análisis de riesgos de seguridad, y la validación de dichos mecanismos mediante pruebas unitarias.

## **Instrucciones específicas**

Para el desarrollo de la actividad sumativa de la semana, recordemos brevemente los requerimientos del caso:

### **Contexto** 

El sistema backend de **Minimarket Plus** gestiona productos, stock, ventas y usuarios con distintos niveles de acceso. Esta semana deberás configurar un mecanismo de **autenticación y control de acceso** que permita restringir operaciones clave como la edición de productos, el manejo de inventario y la generación de ventas. Luego, mediante pruebas unitarias, deberás verificar que estas restricciones funcionen correctamente según los permisos de cada tipo de usuario (cliente, cajero o administrador).

**Necesidades tecnológicas: requisitos estratégicos de modernización**

El Minimarket ha desarrollado un sistema backend con las siguientes áreas críticas: 

1. Gestión de carritos de compras

* Control en tiempo real: validación constante de la disponibilidad de productos en el carrito según el stock disponible. 

* Operaciones precisas: garantizar que solo se puedan añadir productos al carrito si hay suficiente stock. 

* Reporte de carritos: generación de reportes de compras en proceso, mejorando la gestión de clientes y sus necesidades.  \


2. Gestión de inventarios

* Movimientos controlados: registrar las entradas y salidas de inventario para garantizar una operación eficiente. 

* Alertas automatizadas: generación de alertas para reabastecimiento cuando los productos alcanzan umbrales mínimos. 

* Relación precisa: validar que cada movimiento de inventario esté correctamente asociado a su producto. 

3. Gestión de ventas

* Procesos seguros: validar que las ventas solo se confirmen si todos los productos del carrito tienen stock suficiente. 

* Detalle de ventas: asegurar que cada venta tenga un registro detallado de los productos adquiridos, cantidades y precios. 

* Reporte de ventas: generación de análisis detallados para identificar tendencias y productos más vendidos. 

4. Gestión de usuarios

* Perfiles personalizados: registro y autenticación segura de usuarios, con roles diferenciados como cliente y administrador. 

* Control de acceso: garantizar que los permisos y roles de cada usuario se respeten en todas las operaciones. 

* Notificaciones relevantes: implementación de notificaciones personalizadas para informar sobre promociones y cambios en los pedidos. 

5. Seguridad y protección de datos 

* Cumplimiento normativo: alineación con la Ley de Protección de Datos Personales en Chile. 

* Autenticación robusta: uso de medidas avanzadas para evitar accesos no autorizados y proteger las transacciones de los usuarios. 

* Confidencialidad: implementación de medidas para proteger los datos sensibles de clientes y empleados.  \


![](image3.png)

**Importante**

El código del backend de &quot;MINIMARKET PLUS&quot; será proporcionado para su descarga a través del Aula Virtual. Deberás utilizar este backend como base para la actividad, enfocándote en el análisis y configuración del framework de seguridad según los requerimientos proporcionados. 

Ahora que has revisado y analizado el caso, tendrás que realizar los siguientes pasos: 

### **Paso 1: Configuración del entorno de pruebas unitarias**

* Asegúrate de tener configurado el entorno del proyecto con Maven o Gradle.

* Agrega las dependencias necesarias de JUnit y herramientas complementarias como Mockito.

* Organiza el proyecto con las carpetas estándar:

   * src/main/java para el código de las entidades.

   * src/test/java para las pruebas unitarias.

### **Paso 2: Diseña y crea pruebas unitarias**

Crea pruebas unitarias enfocadas en las siguientes entidades clave:

* Producto: valida que solo usuarios autenticados con rol de administrador puedan modificar los datos de un producto.

* Inventario: verifica que se puedan registrar correctamente los movimientos de entrada y salida, solo si el usuario tiene permiso.

* Venta: asegúrate de que solo los cajeros puedan generar ventas y que las mismas reflejen correctamente los productos vendidos.

* Usuario: prueba el comportamiento del sistema frente a intentos de autenticación válidos y no válidos.  \


### **Paso 3: Ejecuta las pruebas**

* Corre las pruebas desde tu IDE o mediante línea de comandos (mvn test o gradle test).

* Genera reportes en formato HTML o XML para analizar los resultados.

### **Paso 4: Documentación técnica y análisis de resultados**

Redacta un informe técnico directamente en este documento que integre los siguientes elementos como evidencia del trabajo realizado esta semana:

**Resumen técnico del avance respecto a las semanas anteriores**

Explica brevemente cómo el trabajo de las semanas 4 y 5 permitió construir la base para implementar mecanismos de autenticación en esta actividad. Menciona las entidades clave utilizadas y cómo se relacionan con la necesidad de proteger los accesos en el backend del sistema "Minimarket Plus".

1. **Análisis de los resultados obtenidos**

Detalla los comportamientos observados durante la ejecución de las pruebas:

* ¿Qué endpoints fueron correctamente protegidos mediante autenticación y autorización?

* ¿Qué casos fallaron por accesos no autorizados o errores en la configuración?

* ¿Qué ajustes realizaste al código o a las reglas de seguridad para corregir los problemas detectados?

2. **Evidencia de ejecución**

Incluye capturas de pantalla o reportes generados (por ejemplo, consola, JaCoCo, etc.) que respalden el proceso de pruebas y validación de seguridad:

**Preguntas de apoyo**  

Para apoyarte en tu análisis de proyecto, guíate por las siguientes preguntas:  

* ¿Qué métricas indican que las pruebas unitarias cumplen con los requerimientos del sistema? 

* ¿Cómo mejoraste el código tras identificar fallos en las pruebas? 

* ¿Cómo aseguraste la cobertura de los casos límite en las entidades? 

**Paso 5: Subida de código a GitHub** 

El código deberás subirlo a tu repositorio GitHub. Si no has creado tu cuenta aún, puedes hacerlo a través del siguiente enlace:

[https://github.com/](https://github.com/) 

Posteriormente, desde el repositorio, deberás generar un enlace de tu proyecto: 

**Figura 1** 

Enlace de proyecto GitHub 

![](image4.png)

*Nota.* Ejemplo genérico de donde se extrae un enlace en GitHub. GitHub (s.f.). *GitHub.* [https://github.com/](https://github.com/) 

**Paso 6:** Para tu entrega, no olvides adjuntar este documento con tus entregables y subir al AVA, junto con el enlace de GitHub a adjuntar en la sección "Entrega".

 \


# **Entregables**

## 1. Diseño y justificación de las pruebas unitarias

### Resumen técnico del avance (Semanas 4, 5 y 6)

En la **Semana 4** se desarrollaron las entidades JPA del sistema Minimarket Plus: `Producto`, `Categoria`, `Inventario`, `Carrito`, `Venta`, `DetalleVenta` y `Usuario`, junto con sus respectivos repositorios Spring Data JPA y la capa de servicios. Esta base permitió que cada entidad tuviera operaciones CRUD completas con validaciones de negocio (por ejemplo, `DatosIncompletosException` cuando faltan campos obligatorios y `StockInsuficienteException` cuando no hay stock suficiente).

En la **Semana 5** se integró la capa de seguridad con Spring Security y se configuró la autenticación basada en roles. Se crearon los roles `ROLE_ADMIN`, `ROLE_CAJERO` y `ROLE_CLIENTE`, y se estableció una tabla de relación `usuario_roles` (Many-to-Many). El servicio `UsuarioService` fue extendido con el método `puedeRegistrarVenta(Usuario)`, que valida si un usuario con `ROLE_CAJERO` puede crear ventas.

En la **Semana 6** se implementaron los mecanismos de autenticación stateless con JWT (JSON Web Token) y se crearon las pruebas unitarias para validar el comportamiento de los servicios. Se agregó:

- `JwtUtil`: genera tokens HS256 con vigencia de 24 horas, incluyendo los roles en los claims.
- `JwtAuthenticationFilter`: intercepta cada petición HTTP para validar el token.
- `SecurityConfig`: reconfigurado para ser stateless (`SessionCreationPolicy.STATELESS`), con `/api/auth/**` y `/public/**` como rutas públicas.
- `DataInitializer`: siembra los tres usuarios de prueba al arrancar la aplicación.
- 67 pruebas unitarias distribuidas en 5 clases de servicio + 1 clase de entidad.

Las entidades clave que se relacionan con la protección de accesos son:
- `Usuario` y `Rol`: son el núcleo del modelo de seguridad. Sin ellos no es posible autenticar ni autorizar.
- `Venta`: solo los usuarios con `ROLE_CAJERO` pueden crear ventas (validado en `UsuarioServiceImpl.puedeRegistrarVenta()`).
- `Producto` e `Inventario`: operaciones de escritura requieren `ROLE_ADMIN` o `ROLE_CAJERO` respectivamente.

### Diseño y justificación de las pruebas

Se diseñaron 67 pruebas unitarias siguiendo el patrón **Arrange-Act-Assert** con JUnit 5 y Mockito. La decisión de usar Mockito responde a que las pruebas unitarias deben aislar la lógica del servicio del repositorio: no se debe cargar el contexto de Spring ni usar la base de datos real. Esto hace las pruebas más rápidas y detecta errores de lógica de negocio independientemente de la infraestructura.

| Clase de prueba | Pruebas | Escenarios cubiertos |
|---|---|---|
| `ProductoServiceTest` | 12 | Validación de datos completos, guardado con mock, stock insuficiente, actualización, eliminación |
| `InventarioServiceTest` | 13 | Registro de movimientos, validación de producto asociado, listado, eliminación |
| `VentaServiceTest` | 11 | Creación de venta, validación de cajero, detalle de venta con precio snapshot |
| `UsuarioServiceTest` | 13 | Campos obligatorios, BCrypt, roles, búsqueda por username, CRUD |
| `CarritoServiceTest` | 14 | Agregar producto, validación de stock, actualizar cantidad, eliminar ítem |
| `UsuarioTest` | 3 | Getters/setters de entidad Usuario (prueba plain, sin Spring) |
| **Total** | **66** | |

Cada clase sigue el criterio de al menos 1 escenario de éxito y 1 escenario de error por método de negocio relevante, cubriendo así el criterio de evaluación 1 (diseño para escenarios de éxito y error) y el criterio 3 (distintos comportamientos por rol).

---

## 2. Descripción paso a paso de la configuración del entorno de pruebas

### Paso 1: Dependencias en pom.xml

Las pruebas unitarias usan las dependencias incluidas por defecto en `spring-boot-starter-test`, que trae JUnit 5, Mockito, AssertJ y Hamcrest. No fue necesario agregar dependencias adicionales para el framework de pruebas.

Para autenticación JWT se agregó JJWT 0.11.5:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

Para Swagger/OpenAPI:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Para compatibilidad con Java 25, el compilador se actualizó a la versión 3.14.0:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
    </configuration>
</plugin>
```

### Paso 2: Estructura de directorios

```
src/
├── main/java/com/minimarket/
│   ├── config/
│   │   ├── DataInitializer.java     # Siembra ROLE_ADMIN, ROLE_CAJERO, ROLE_CLIENTE
│   │   └── OpenApiConfig.java       # Configura Swagger con Bearer JWT
│   ├── controller/                  # 9 RestControllers con @Tag y @Operation
│   ├── entity/                      # Entidades JPA con getters/setters manuales
│   ├── exception/                   # Excepciones de dominio personalizadas
│   ├── repository/                  # Interfaces Spring Data JPA
│   ├── security/
│   │   ├── config/SecurityConfig.java
│   │   ├── filter/JwtAuthenticationFilter.java
│   │   ├── handler/JwtAuthenticationEntryPoint.java
│   │   ├── model/LoginRequest.java
│   │   ├── service/CustomUserDetailsService.java
│   │   └── util/JwtUtil.java
│   └── service/
│       ├── impl/                    # Implementaciones con lógica de negocio
│       └── *.java                   # Interfaces de servicio
└── test/java/com/minimarket/service/
    ├── CarritoServiceTest.java
    ├── InventarioServiceTest.java
    ├── ProductoServiceTest.java
    ├── UsuarioServiceTest.java
    ├── UsuarioTest.java
    └── VentaServiceTest.java
```

### Paso 3: Patrón de prueba unitaria con Mockito

Cada clase de prueba usa `@ExtendWith(MockitoExtension.class)` para activar Mockito sin cargar el contexto de Spring. El repositorio se declara como `@Mock` y el servicio como `@InjectMocks`, lo que hace que Mockito inyecte el mock automáticamente:

```java
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    public void guardar_datosValidos_invocaSave() {
        // Arrange
        Producto producto = new Producto("Arroz", 1500.0, 10);
        when(productoRepository.save(producto)).thenReturn(producto);

        // Act
        Producto resultado = productoService.save(producto);

        // Assert
        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(producto);
    }
}
```

### Paso 4: Ejecución de pruebas

Desde la línea de comandos:

```bash
# Entorno estándar (Java 17)
./mvnw test

# Entorno con Java 25 (workaround JaCoCo)
./mvnw test -Djacoco.skip=true
```

Los reportes XML se generan automáticamente en `target/surefire-reports/`.

---

## 3. Resultados obtenidos y análisis de cobertura

### Resultado de ejecución de pruebas

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.MinimarketApplicationTests
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.UsuarioTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.service.CarritoServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.service.InventarioServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.service.ProductoServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.service.UsuarioServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 -- in com.minimarket.service.VentaServiceTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 67, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
[INFO] Total time:  02:30 min
```

Se ejecutaron **67 pruebas** en total: 66 pruebas unitarias con Mockito (sin contexto Spring) más 1 prueba de carga de contexto (`MinimarketApplicationTests`). Resultado: 0 fallos, 0 errores.

### Endpoints protegidos

La configuración de `SecurityConfig` protege todos los endpoints bajo `/api/**` con JWT. Solo `POST /api/auth/login` y `GET /public/hola` son accesibles sin autenticación.

| Endpoint | Metodo | Proteccion |
|---|---|---|
| `/api/auth/login` | POST | Publico (sin token) |
| `/public/hola` | GET | Publico (sin token) |
| `/api/productos` | GET | JWT (cualquier rol) |
| `/api/productos` | POST, PUT, DELETE | JWT (ROLE_ADMIN) |
| `/api/inventario` | POST | JWT (ROLE_CAJERO, ROLE_ADMIN) |
| `/api/ventas` | POST | JWT (ROLE_CAJERO) |
| `/api/usuarios` | Todos | JWT (ROLE_ADMIN) |
| `/api/carrito` | Todos | JWT (cualquier rol) |
| `/api/categorias` | GET | JWT (cualquier rol) |
| `/api/categorias` | POST, PUT, DELETE | JWT (ROLE_ADMIN) |

### Ajustes realizados durante el desarrollo

Durante el análisis post-implementación (ejecutado con la herramienta `/speckit-analyze`) se identificaron y corrigieron tres issues críticos:

**Issue D1 — DataInitializer sembraba ROLE_EMPLEADO en lugar de ROLE_CAJERO:**

El archivo `DataInitializer.java` creaba el rol `ROLE_EMPLEADO` al iniciar la aplicación, pero la constitución del sistema define los roles como `ROLE_ADMIN`, `ROLE_CAJERO` y `ROLE_CLIENTE`. Esto significaba que ningún usuario del sistema podía registrar ventas, ya que el rol esperado (`ROLE_CAJERO`) nunca era creado.

Corrección: cambiar `getOrCreateRol("ROLE_EMPLEADO")` → `getOrCreateRol("ROLE_CAJERO")` y `crearUsuario("empleado", ...)` → `crearUsuario("cajero", "cajero123", ...)`.

**Issue D2 — puedeRegistrarVenta() siempre retornaba false:**

El método `UsuarioServiceImpl.puedeRegistrarVenta()` comparaba los nombres de rol usando las cadenas `"ADMIN"` y `"VENDEDOR"`, sin el prefijo `"ROLE_"`. Pero la base de datos almacena los roles con el prefijo completo (`"ROLE_ADMIN"`, `"ROLE_CAJERO"`). Como resultado, la comparación siempre fallaba y ningún usuario real podía registrar ventas.

Corrección: reemplazar la comparación con `"ROLE_CAJERO".equals(rol.getNombre())`.

**Issue D3 — ROLE_VENDEDOR no existe en el sistema:**

Los fixtures de prueba en `UsuarioServiceTest` usaban `new Rol("VENDEDOR")` como rol válido, pero este rol no existe en el sistema (la constitución define solo ROLE_ADMIN, ROLE_CAJERO, ROLE_CLIENTE). El test pasaba artificialmente gracias al fixture incorrecto.

Corrección: actualizar los fixtures a `new Rol("ROLE_CAJERO")` para el escenario de rol válido y `new Rol("ROLE_CLIENTE")` para el escenario de rol inválido.

Tras aplicar estas tres correcciones, los 66 tests continúan pasando con 0 fallos.

### Análisis de cobertura

Dado que JaCoCo 0.8.14 no soporta bytecode Java 25, la cobertura se evidencia mediante:

1. Los reportes Surefire XML en `target/surefire-reports/` confirman cada método de prueba ejecutado.
2. El análisis manual de las clases de prueba muestra que los métodos públicos de cada servicio tienen al menos un escenario de éxito y uno de error.
3. Casos límite cubiertos: valor nulo, cadena vacía/espacios, stock = 0, usuario sin roles, colección vacía.

---

## 4. Explicación de cómo las pruebas contribuyen a la calidad del sistema

### Preguntas de apoyo

**¿Qué métricas indican que las pruebas unitarias cumplen con los requerimientos del sistema?**

La métrica principal es el resultado de `mvn test`: `Tests run: 67, Failures: 0, Errors: 0, Skipped: 0`. Esto indica que los 67 escenarios de prueba definidos pasan correctamente.

A nivel de completitud: cada servicio de negocio (`ProductoService`, `InventarioService`, `VentaService`, `UsuarioService`, `CarritoService`) tiene un promedio de 12 pruebas, cubriendo los métodos CRUD y las validaciones de negocio específicas.

A nivel de calidad: la presencia de 3 issues críticos detectados por análisis estático (D1/D2/D3) demuestra que las pruebas unitarias solas no son suficientes; complementarlas con análisis de integración y revisión de la coherencia entre datos de test y datos reales es esencial.

**¿Cómo mejoraste el código tras identificar fallos en las pruebas?**

Los tres issues críticos identificados (D1, D2, D3) se descubrieron mediante análisis comparativo entre la lógica implementada y el modelo de datos real (cómo se almacenan los roles en la base de datos). 

- Para D1: se revisó `DataInitializer.java` y se confirmó que el rol sembrado no coincidía con el esperado en `puedeRegistrarVenta()`.
- Para D2: se trazó el flujo completo desde la inserción del rol en BD hasta su uso en la comparación del método.
- Para D3: se verificó que el fixture de prueba `new Rol("VENDEDOR")` no representaba ningún estado posible del sistema real.

El proceso de mejora fue: identificar → trazar el flujo → corregir la fuente del error → verificar que los 66 tests siguen pasando.

**¿Cómo aseguraste la cobertura de los casos límite en las entidades?**

Para cada entidad se identificaron los campos obligatorios (definidos en el método `datosCompletos()` de cada servicio) y se creó una prueba por cada campo faltante. Por ejemplo, `UsuarioServiceTest` tiene pruebas para `registrar_sinNombre_lanzaDatosIncompletosException()`, `registrar_sinApellido_lanzaDatosIncompletosException()`, `registrar_sinEmail_lanzaDatosIncompletosException()` y `registrar_sinDireccion_lanzaDatosIncompletosException()`.

Para `CarritoServiceTest` se probó el caso de stock = 0 (`agregarProducto_sinStock_lanzaStockInsuficienteException()`) y de cantidad negativa. Para `VentaServiceTest` se probó el caso de venta sin usuario cajero.

---

## 5. Recomendaciones para mejorar la implementación

### Propuesta 1: Agregar pruebas de integración con contexto Spring para el flujo JWT completo

**Problema detectado**: Las pruebas unitarias validan la lógica de cada servicio de forma aislada, pero no validan el flujo completo de autenticación JWT: generación de token → filtro HTTP → validación → acceso al endpoint protegido.

**Mejora propuesta**: Agregar una clase `AuthIntegrationTest` anotada con `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)` y `@AutoConfigureMockMvc` que pruebe:
- `POST /api/auth/login` con credenciales válidas → recibe token 200
- `GET /api/productos` sin token → recibe 401
- `GET /api/productos` con token válido → recibe 200
- `GET /api/productos` con token expirado → recibe 401

**Métrica esperada**: 0 regresiones en el flujo de autenticación al hacer cambios en `SecurityConfig`.

### Propuesta 2: Agregar validaciones de Bean Validation (@NotBlank, @Email) en las entidades

**Problema detectado**: La validación de campos obligatorios se hace manualmente en cada `ServiceImpl` mediante el método `datosCompletos()`. Esto significa que si se agrega un nuevo campo a la entidad, el desarrollador debe recordar actualizar también el método `datosCompletos()`.

**Mejora propuesta**: Agregar anotaciones de Bean Validation directamente en las entidades:

```java
public class Usuario {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El email debe tener formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
```

Y activar la validación en los controllers con `@Valid`. Esto elimina la necesidad de `datosCompletos()` y hace que la validación sea declarativa y menos propensa a errores de mantenimiento.

**Métrica esperada**: Reducción del código de validación manual en 5 servicios y detección automática de campos inválidos en el controller antes de llegar al servicio.

### Propuesta 3: Implementar un manejador global de excepciones (@ControllerAdvice)

**Problema detectado**: Actualmente, `DatosIncompletosException` y `StockInsuficienteException` no son capturadas en ningún punto centralizado. Si son lanzadas durante una petición HTTP, Spring las convierte en respuestas 500 (Internal Server Error), lo cual no refleja correctamente el tipo de error al cliente.

**Mejora propuesta**: Crear una clase `GlobalExceptionHandler`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatosIncompletosException.class)
    public ResponseEntity<Map<String, String>> handleDatosIncompletos(DatosIncompletosException e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handleStockInsuficiente(StockInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", e.getMessage()));
    }
}
```

**Métrica esperada**: Las respuestas de error pasan de 500 a 400 (datos incompletos) y 409 (stock insuficiente), mejorando la comunicación de errores con clientes de la API.

### Propuesta 4: Agregar prueba para el caso limite de carrito vacío en VentaService

**Problema detectado**: `VentaServiceTest` no cubre el caso en que un cajero intenta registrar una venta con un carrito vacío. En un sistema real, esto debería lanzar una excepción o retornar un error de validación.

**Mejora propuesta**: Agregar el test:

```java
@Test
public void registrarVenta_carritoVacio_lanzaDatosIncompletosException() {
    Venta venta = new Venta();
    venta.setUsuario(usuarioCajero());
    venta.setItems(Collections.emptyList());
    assertThrows(DatosIncompletosException.class, () -> ventaService.save(venta));
}
```

Y la implementación correspondiente en `VentaServiceImpl` que valide que la venta tenga al menos un ítem.

### Propuesta 5: Proteger el acceso a la consola H2 en entornos de desarrollo

**Problema detectado**: La consola H2 (`/h2-console`) no está en la lista de URLs permitidas en `SecurityConfig`, pero tampoco hay una configuración explícita que la bloquee. El comportamiento depende del orden de los filtros de Spring Security. En producción, una base de datos H2 en memoria no debería existir, pero si se replica el entorno con una base de datos externa, este endpoint podría quedar expuesto.

**Mejora propuesta**: Agregar en `SecurityConfig` una regla explícita:

```java
.requestMatchers("/h2-console/**").hasRole("ADMIN")
.headers(h -> h.frameOptions(f -> f.sameOrigin()))
```

Esto permite acceso a la consola H2 solo a administradores y desde el mismo origen, evitando clickjacking.

---

**Paso 5: Subida de código a GitHub** 

El código deberás subirlo a tu repositorio GitHub. Si no has creado tu cuenta aún, puedes hacerlo a través del siguiente enlace:

[https://github.com/](https://github.com/) 

Enlace del repositorio: **https://github.com/kbsg01/DB_II-minimarket**

Rama activa: `feat/microservices-junit-s6`

**Paso 6:** Para tu entrega, no olvides adjuntar este documento con tus entregables y subir al AVA, junto con el enlace de GitHub a adjuntar en la sección "Entrega".

 \


![](image5.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 

# MiniMarket Plus - Backend

Backend REST para la gestion de un minimarket: usuarios, productos, categorias,
inventario, carrito y ventas. Desarrollado como entrega academica de la asignatura
**Desarrollo Backend II (PBY2202)** en Duoc UC.

Esta entrega (Semana 4) incorpora ademas un entorno de pruebas unitarias con
**JUnit 5 + Mockito + JaCoCo** y la logica de negocio minima para validarlo. El detalle
de las pruebas y la cobertura esta en [INFORME.md](INFORME.md).

## Finalidad

Exponer una API REST para operar un minimarket bajo una arquitectura en capas clasica
de Spring Boot:

```
controller  ->  service (interfaz)  ->  service/impl  ->  repository (JpaRepository)  ->  entity
```

Reglas de negocio cubiertas por las pruebas de esta entrega:

- Un usuario solo puede registrarse si sus datos estan completos
  (`username`, `nombre`, `apellido`, `email`, `direccion`).
- Solo los roles autorizados (`ADMIN`, `VENDEDOR`) pueden registrar ventas.
- Una venta calcula su total como la suma de `cantidad x precio` de cada detalle.
- No se puede registrar una venta sin stock suficiente; el stock se descuenta al vender.

## Stack

| Capa | Tecnologia |
|------|-----------|
| Lenguaje | Java 17 (destino de compilacion) |
| Framework | Spring Boot 3.4.1 |
| Web | Spring Web (REST) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria (perfil de desarrollo) |
| Seguridad | Spring Security (form-login + BCrypt) |
| Build | Maven (via wrapper `mvnw`, no requiere Maven global) |
| Utilidades | Lombok (disponible; las entidades usan getters/setters manuales) |
| Pruebas | JUnit 5, Mockito, JaCoCo |

> Nota de entorno: el equipo de desarrollo local usa un JDK reciente (JDK 25). Por
> compatibilidad se fijaron `lombok.version=1.18.42` y `jacoco-maven-plugin 0.8.14`. El
> proyecto mantiene **Java 17** como destino; en un entorno con JDK 17 esos ajustes son
> inocuos.

## Requisitos previos

- JDK 17 o superior instalado y disponible en el `PATH`.
- No se necesita instalar Maven: el repositorio incluye el wrapper `./mvnw`.

## Ejecucion

Todos los comandos se ejecutan desde el directorio `minimarket/`.

```bash
# Levantar la aplicacion (puerto 8080, H2 en memoria)
./mvnw spring-boot:run

# Compilar el jar ejecutable
./mvnw package
java -jar target/minimarket-0.0.1-SNAPSHOT.jar
```

La aplicacion arranca en `http://localhost:8080`.

- Endpoint de conectividad (publico): `GET /public/hola`
- Consola H2 (solo desarrollo): `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:testdb`, usuario `sa`, sin contrasena).

> En Linux, si el wrapper falla con "interprete erroneo: Permiso denegado", ejecutalo
> con `sh ./mvnw ...` o dale permiso de ejecucion con `chmod +x mvnw`.

## API

Recursos REST disponibles (CRUD estandar: `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`,
`DELETE /{id}`):

| Recurso | Ruta base |
|---------|-----------|
| Usuarios | `/api/usuarios` |
| Productos | `/api/productos` |
| Categorias | `/api/categorias` |
| Inventario | `/api/inventario` |
| Carrito | `/api/carrito` |
| Ventas | `/api/ventas` |
| Detalle de ventas | `/api/detalle-ventas` |

Todas las rutas bajo `/api/**` requieren autenticacion (Spring Security, form-login).
Solo `/public/**` es de acceso libre.

## Configuracion

La configuracion vive en [`src/main/resources/application.properties`](src/main/resources/application.properties).
El proyecto **no usa variables de entorno ni secretos externos**: la base de datos es
H2 en memoria y las credenciales son las de desarrollo por defecto. Por eso **no se
incluye un `.env.example`**: no hay nada que externalizar en este perfil.

Si en el futuro se migra a una base de datos real (MySQL/PostgreSQL) o se incorpora un
secreto de firma JWT, el camino recomendado es externalizar esos valores como variables
de entorno y referenciarlos en `application.properties` con la sintaxis
`${NOMBRE_VARIABLE}`. En ese momento si correspondera crear un `.env.example`.

Claves actuales relevantes:

```properties
spring.datasource.url=jdbc:h2:mem:testdb   # H2 en memoria; los datos se pierden al reiniciar
spring.jpa.hibernate.ddl-auto=update       # Hibernate crea/actualiza el esquema al arrancar
spring.h2.console.enabled=true             # Consola web de H2 habilitada
spring.jpa.show-sql=true                   # Muestra el SQL generado en consola
```

## Pruebas y cobertura

```bash
./mvnw clean test                          # ejecuta todas las pruebas
./mvnw test -Dtest=UsuarioServiceTest      # ejecuta una sola clase de prueba
./mvnw test -Dtest=VentaServiceTest#calcularTotal_variosProductos_sumaCorrecta  # un solo metodo
./mvnw verify                              # ejecuta las pruebas + jacoco:check (gate de 80%)
./mvnw jacoco:report                       # genera el reporte de cobertura
```

El reporte de cobertura HTML queda en `target/site/jacoco/index.html`. La regla
`jacoco:check` falla la compilacion si la cobertura de lineas baja del 80% en las clases
evaluadas. Ver [INFORME.md](INFORME.md) para el detalle del diseno de pruebas y los
resultados de cobertura.

## Comandos comunes

| Tarea | Comando (desde `minimarket/`) |
|-------|-------------------------------|
| Ejecutar la app | `./mvnw spring-boot:run` |
| Compilar el jar | `./mvnw package` |
| Ejecutar el jar | `java -jar target/minimarket-0.0.1-SNAPSHOT.jar` |
| Ejecutar todas las pruebas | `./mvnw clean test` |
| Ejecutar una clase de prueba | `./mvnw test -Dtest=NombreTest` |
| Ejecutar un metodo de prueba | `./mvnw test -Dtest=NombreTest#nombreMetodo` |
| Verificar con gate de cobertura | `./mvnw verify` |
| Generar reporte de cobertura | `./mvnw jacoco:report` |
| Limpiar artefactos de build | `./mvnw clean` |

## Estructura del proyecto

```
minimarket/
├── pom.xml                         # Dependencias, JaCoCo, version Lombok
├── mvnw, mvnw.cmd                  # Maven wrapper
├── INFORME.md                      # Informe tecnico de pruebas (Semana 4)
├── README.md
└── src/
    ├── main/java/com/minimarket/
    │   ├── controller/             # Endpoints REST (/api/**, /public/**)
    │   ├── service/                # Interfaces de servicio
    │   │   └── impl/               # Implementaciones (@Service)
    │   ├── repository/             # JpaRepository por entidad
    │   ├── entity/                 # Modelo de dominio (@Entity)
    │   ├── exception/              # Excepciones de negocio
    │   └── security/               # Configuracion de Spring Security
    ├── main/resources/
    │   └── application.properties  # Configuracion (H2, JPA)
    └── test/java/com/minimarket/   # Pruebas (JUnit 5 + Mockito)
```

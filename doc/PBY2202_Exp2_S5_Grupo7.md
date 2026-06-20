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

**Profesor:**

</td>
   <td>

**Fecha:**

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

1. **Guía de configuración del entorno de pruebas**

Describe las herramientas utilizadas, entorno de ejecución y cualquier ajuste realizado.

2. **Guía de configuración del entorno de pruebas**

 \


2. **Código de las pruebas unitarias ejecutadas**

Incluye fragmentos de pruebas aplicadas a las entidades Cita y Usuario:

 \


3. **Evidencia de resultados de ejecución**



 \


**Informe de análisis y reflexión técnica**

 \




![](image5.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 


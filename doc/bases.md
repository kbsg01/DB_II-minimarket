![](image1.png)



**Guía de aprendizaje** 

**Ejecutando Pruebas Unitarias en Microservicios con JUnit**

**Exp 2 – Semana 5**

# **Desarrollo Backend II (PBY2202)**

**Facilitador disciplinar:** Ányelo Castellón Ríos

**Asesor par:** Ignacio Villarroel Sánchez \


**Índice**

[Introducción a la semana	4]()

[Resultado de aprendizaje	5]()

[Conceptos relevantes	5]()

[Preguntas activadoras	6]()

[Actividad	6]()

[Conceptos fundamentales de las pruebas unitarias en microservicios	7]()

[¿Qué son las pruebas unitarias?	7]()

[Importancia de las pruebas unitarias en microservicios	8]()

[Pruebas Unitarias con JUnit	8]()

[Configuración de un entorno para pruebas con JUnit	10]()

[Estructura y desarrollo de Pruebas unitarias	12]()

[Componentes básicos de una prueba unitaria con JUnit	12]()

[Métodos de aserción en JUnit	16]()

[Buenas prácticas en la creación de pruebas unitarias	21]()

[Ejecución de las pruebas unitarias	23]()

[Ejecución de pruebas desde el IDE y línea de comando	23]()

[Lectura de reportes de pruebas (consola, reportes HTML, integraciones con CI/CD)	25]()

[Identificación y resolución de errores y fallos en las pruebas	27]()

[Buenas prácticas y estándares de calidad en pruebas unitarias	29]()

[Mantención y refactorización de las pruebas	30]()

[Cumplimiento de estándares de la industria y requisitos del proyecto	31]()

[Ejemplo de implementación	33]()

[Cierre de la semana	39]()

[Referencias	40]()

[Glosario	41]()

[Apuntes	44]()



 \


# **![](image3.png)**Introducción a la semana**

En esta semana, profundizaremos en la creación y ejecución de pruebas unitarias que garanticen la calidad y funcionalidad de nuestros microservicios. Comprenderás cómo utilizar JUnit, una herramienta estándar en la industria, para validar el correcto funcionamiento de cada componente, asegurando que el código cumpla con los requisitos y se mantenga estable a lo largo del tiempo.

Durante esta semana, aprenderás a configurar el entorno de pruebas, a estructurar tus casos de prueba siguiendo buenas prácticas y a utilizar técnicas como mocks para aislar los servicios de sus dependencias externas. De esta forma, podrás identificar y corregir problemas de manera temprana, evitando que se propaguen hacia etapas más complejas del desarrollo.

Entre los contenidos más relevantes que encontrarás en esta guía se incluyen:

* Configuración y ejecución de pruebas unitarias con Junit.

* Uso de aserciones y anotaciones en Junit.

* Aplicación de mocks y stubs en un entorno de microservicios.

* Interpretación de reportes y cobertura de código.

* Integración de pruebas unitarias en el flujo de desarrollo continuo.

 **\**


# **Resultado de aprendizaje**

### **El estudiante será capaz de:**

**RA2.** Crea pruebas unitarias en los microservicios, usando tecnologías como JUnit, para asegurar calidad del desarrollo elaborado, de acuerdo a requerimientos y estándares de la industria.

### **Indicador de logro:**

**IL7.** Ejecuta pruebas unitarias en microservicios desarrollados utilizando JUnit y herramientas actuales, garantizando la calidad y funcionalidad del código.

# **Conceptos relevantes**

<table>
  <tr>
   <td>

Pruebas Unitarias

</td>
   <td>

Microservicios

</td>
   <td>

JUnit

</td>
  </tr>
  <tr>
   <td>

Cobertura de código

</td>
   <td>

Mocks y Stubs

</td>
   <td>

Buenas prácticas de pruebas

</td>
  </tr>
  <tr>
   <td>

Automatización

</td>
   <td>

Diseño de Pruebas

</td>
   <td>

CI/CD

</td>
  </tr>
</table>

 **\**


# **Preguntas activadoras**

* ¿De qué forma consideras que el uso de JUnit facilita la creación de pruebas unitarias para microservicios en comparación con otras herramientas?

* ¿Cómo crees que las aserciones y anotaciones en JUnit contribuyen a mantener las pruebas unitarias claras y organizadas?

* ¿De qué manera el uso de mocks y stubs en microservicios puede ayudarte a detectar fallas en módulos específicos sin interferencias externas?

* ¿Por qué resulta relevante interpretar y actuar sobre los reportes de cobertura de código, en especial cuando se busca asegurar la calidad del servicio?

* ¿Cómo integrarías las pruebas unitarias en un flujo de desarrollo continuo para incrementar la confiabilidad del software a lo largo del tiempo?

# **Actividad**

En esta quinta semana, realizarás una actividad formativa grupal (2 a 3 integrantes), llamada &quot;Ejecutando y analizando pruebas unitarias en microservicios con JUnit&quot;, donde deberás ejecutar pruebas unitarias en microservicios utilizando JUnit y herramientas actuales, garantizando la calidad y funcionalidad del código. Además, deberás realizar un análisis de los resultados de las pruebas unitarias para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

 **\**


# **Conceptos fundamentales de las pruebas unitarias en microservicios**

## **¿Qué son las pruebas unitarias?**

Las pruebas unitarias son un tipo de prueba automatizada que verifica el funcionamiento de las unidades más pequeñas de un código -como métodos o funciones- de forma aislada. Su objetivo principal es asegurar que cada componente del software cumpla con los requisitos esperados, facilitando la detección temprana de errores.

Por ejemplo, una prueba unitaria verifica si una función matemática simple como sumar (a, b) retorna correctamente la suma de dos números dados.

### **Ventajas de las pruebas unitarias:** 

* Facilitan la detección de errores en etapas tempranas del desarrollo.

* Permiten mantener el código estable durante cambios o refactorizaciones.

* Mejoran la confianza en la calidad del software entregado.

 **\**


## **Importancia de las pruebas unitarias en microservicios**

En una arquitectura de microservicios, donde cada componente realiza tareas específicas y se comunica con otros a través de APIs, las pruebas unitarias son esenciales para:

* **Asegurar la funcionalidad individual:** verificar que cada microservicio funcione correctamente en aislamiento antes de integrarlo con otros.

* **Detectar fallos rápidamente:** cuando algo falla, las pruebas ayudan a localizar el error en un servicio específico.

* **Soportar cambios frecuentes:** dado que los microservicios suelen actualizarse de manera independiente, las pruebas aseguran que los cambios no afecten funcionalidades existentes.

## **Pruebas Unitarias con JUnit**



La semana anterior, introdujimos JUnit como una herramienta esencial para realizar pruebas unitarias en proyectos Java. Ahora, profundizaremos en sus características clave y las razones que explican por qué es ampliamente utilizado por los desarrolladores de microservicios.

Entre sus características, se encuentran el uso de anotaciones (@Test, @BeforeEach, @AfterEach) para estructurar las pruebas, reportes detallados de resultados para analizar fallos y compatibilidad con herramientas de integración continua (CI/CD) como Jenkins.

### **Por qué usar JUnit en microservicios:**

* Su simplicidad permite escribir pruebas unitarias rápidamente.

* Admite pruebas de lógica de negocio compleja y validación de APIs internas.  **\**


# **Configuración de un entorno para pruebas con JUnit**

Antes de empezar a escribir pruebas en tu aplicación backend, necesitas configurar tu entorno. A continuación, te enseñamos cómo realizar dicha acción:

**Paso 1:** crea un proyecto en Java

Si usas una herramienta como Maven o Gradle, configura un proyecto básico.

**Paso 2:** agrega la dependencia de JUnit

Incluye la biblioteca de JUnit en el archivo de configuración del proyecto.

**Figura 1**

*Dependencias JUnit y Mockito*



*Nota.* Configuración de Maven para pruebas con JUnit 5. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

**Paso 3:** organiza las pruebas en un directorio específico

* Por convención, coloca las pruebas en un directorio como `src/test/java.`

* Nombra las clases de prueba con el sufijo Test (por ejemplo, `UserServiceTest`).  **\**


**Paso 4:** escribe tu primera prueba con Junit.

Crea una clase de prueba y utiliza la anotación `@Test` para definir un caso de prueba:

**Figura 2**

*Prueba Unitaria en JUnit 5*



*Nota.* Prueba unitaria para un servicio matemático. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Aquí, se verifica que el método sum de la clase MathService funciona correctamente.

**Paso 5:** ejecuta las pruebas

* Usa tu IDE para ejecutar las pruebas o un comando desde la terminal.

 * **\**


# **![](image8.png)**Estructura y desarrollo de Pruebas unitarias**

Las pruebas unitarias deben seguir una estructura clara y definida que permita validar la funcionalidad de las partes más pequeñas de un sistema, como por ejemplo, los métodos de una clase. En esta sección, exploraremos los componentes básicos de una prueba con JUnit y las mejores prácticas para escribir código de prueba eficaz y mantenible.

## **Componentes básicos de una prueba unitaria con JUnit**

JUnit ofrece diversas herramientas que permiten estructurar y organizar pruebas de manera eficiente. Aquí describimos los componentes esenciales que necesitas conocer para crear tus primeras pruebas unitarias.

### **Anotaciones principales de JUnit**

Como refuerzo, recordemos brevemente las anotaciones vistas en la semana anterior, ya que son fundamentales para estructurar las pruebas unitarias. Estas anotaciones serán esenciales al combinar diferentes estrategias de prueba en proyectos más complejos:

 \


* **@Test**

Marca un método como una prueba unitaria. Este método se ejecutará automáticamente al ejecutar las pruebas.

**Figura 3**

*Test de la Función de Suma*



*Nota*. Validación de una operación matemática usando JUnit. JetBrains. (2024). IntelliJ IDEA. (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


* **@BeforeEach**

Se ejecuta antes de cada prueba para configurar el entorno necesario (por ejemplo, inicializar objetos o datos).

**Figura 4**

*Configuración Inicial en JUnit*



*Nota.* Método configurado con @BeforeEach para inicializar el servicio antes de cada prueba. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

* **@AfterEach**

Se ejecuta después de cada prueba para limpiar o liberar recursos utilizados durante la prueba.

**Figura 5**

*Limpieza Posterior en JUnit*



*Nota.* Método configurado con @AfterEach para liberar recursos después de cada prueba. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


* **@BeforeAll**

Se ejecuta una sola vez antes de todas las pruebas en la clase. Útil para configurar recursos compartidos.

**Figura 6** 

*Inicialización Global en JUnit*



*Nota.* Método anotado con @BeforeAll para ejecutar configuraciones previas una sola vez antes de todas las pruebas. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

* **@AfterAll**

Se ejecuta una sola vez después de todas las pruebas. Sirve para liberar recursos globales.

**Figura 7**

*Finalización Global en JUnit*



*Nota.* Método anotado con @AfterAll para ejecutar tareas finales después de todas las pruebas. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


## **Métodos de aserción en JUnit**

En esta sección, presentamos ejemplos avanzados que ilustran cómo los métodos de aserción de JUnit pueden aplicarse en escenarios más complejos y representativos de proyectos de microservicios. Estos ejemplos ayudarán a consolidar conceptos y mostrar cómo validar correctamente el comportamiento del sistema.

* **assertEquals(expected, actual, message):** verifica que dos valores sean iguales. Este método no solo se utiliza para comparar valores simples, sino también objetos más complejos, siempre que implementen correctamente el método equals.

**Figura 8**

*Prueba de recuperación de usuario*



*Nota.* Uso de assertEquals para validar que el usuario devuelto por el servicio coincida con el usuario esperado. JetBrains. (2024). *IntelliJ IDEA (2024.2.4).* [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Este ejemplo asegura que el microservicio devuelva el usuario correcto basado en el correo electrónico, considerando todos los atributos relevantes. \


* **assertTrue(condition, message):** verifica que una condición sea verdadera. Este método es ideal para comprobar condiciones que dependen de reglas de negocio específicas.

**Figura 9**

*Validación de pedidos válidos*



*Nota.* Uso de assertTrue para comprobar que pedido sea considerado válido cuando contiene al menos un producto. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


* **assertFalse(condition, message):** verifica que una condición sea falsa. Este método es ideal para comprobar condiciones que dependen de reglas de negocio específicas.

**Figura 10**

*AssertFalse para asegurar que un pedido vacío no se considere válido* 



*Nota.* Asegura que un pedido vacío no sea considerado válido. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


* **assertNotNull(object, message):** verifica que un objeto no sea nulo. Esta aserción puede usarse para garantizar que los objetos generados o devueltos cumplan con requisitos esenciales, como ser no nulos y contener atributos válidos.

**Figura 11**

*Generación de token de sesión válido*



*Nota.* Verifica que el token no sea nulo y cumpla con el formato alfanumérico de 16 caracteres. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Este ejemplo combina múltiples validaciones para garantizar que el sistema cumpla con estándares específicos. \


* **assertThrows(expectedException, executable, message):** verifica que un código arroje una excepción esperada.

**Figura 12**

*Manejo de excepciones en JUnit*



*Nota.* Uso de assertThrows para verificar que se lanza una excepción al dividir por cero. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


## **Buenas prácticas en la creación de pruebas unitarias**



Escribir pruebas unitarias efectivas requiere seguir principios claros que aseguren su utilidad y mantenibilidad. A continuación, se describen las mejores prácticas para desarrollar pruebas confiables:

* **Usa nombres descriptivos para los métodos de prueba:** el nombre de cada método debe explicar claramente el comportamiento que se está verificando. Esto ayuda a entender rápidamente qué hace la prueba y facilita el diagnóstico cuando falla.

* **Asegura la independencia de las pruebas:** cada prueba debe ejecutarse de manera aislada, sin depender de otras pruebas o de un orden específico. Esto permite ejecutar cualquier prueba individualmente o en paralelo sin conflictos.

* **Prueba un único comportamiento por método:** cada método de prueba debe validar solo un aspecto del sistema, lo que simplifica su comprensión y mantenimiento.

* **Usa datos representativos y variados:** es esencial probar tu código con diferentes tipos de valores, incluyendo casos normales, extremos y valores fuera de rango. Esto asegura que el código sea robusto frente a una amplia gama de entradas.

**Ejemplo:** probar un método que calcula el área de un círculo.

   * **Valores normales:** radio = 5.

   * **Límites:** radio = 0.

   * **Casos negativos:** radio = -1.

 \


* **Integra las pruebas en el flujo de desarrollo:** automatiza y ejecuta las pruebas frecuentemente como parte del flujo de integración continua (CI/CD). Esto garantiza que los errores se detecten rápidamente durante el desarrollo.

**Ejemplo de herramientas populares:**

   * Jenkins, GitHub Actions o GitLab CI para integración continua.

   * Plugins de IDE para ejecutar pruebas automáticamente al guardar cambios.

![](image20.png)

 \


# **Ejecución de las pruebas unitarias**

Habiendo identificado las unidades de prueba y creado aserciones con JUnit, el siguiente paso es ejecutar dichas pruebas para validar el correcto funcionamiento del código. Este proceso incluye desde la ejecución en entornos locales (IDE, línea de comando) hasta la integración con herramientas de integración continua (CI/CD). De igual forma, es esencial comprender cómo interpretar los reportes generados y cómo actuar ante fallos o errores detectados.

## **Ejecución de pruebas desde el IDE y línea de comando**



1. **Ejecución desde el IDE**

La mayoría de los IDEs para Java (como IntelliJ IDEA, Eclipse o VSCode con extensiones adecuadas) proporcionan integración con JUnit. Esto permite:

* Ejecutar una clase de prueba completa o un método de prueba individual con un simple clic.

* Ver resultados de forma inmediata en el panel del IDE, mostrando qué pruebas pasaron, fallaron o tuvieron errores.

* Depurar pruebas fallidas estableciendo puntos de interrupción (breakpoints) en el código de prueba o en el código probado, con el fin de comprender el flujo de ejecución y el estado de las variables en tiempo real.

 * \


2. **Ejecución desde la línea de comando (Maven/Gradle)**

En entornos de desarrollo profesional, es común ejecutar las pruebas unitarias a través de herramientas de construcción. Por ejemplo, con Maven se pueden utilizar comandos como:

* mvn test

**Figura 13**

*Manejo de Exceptions&#39; en JUnit*



*Nota.* Todas las pruebas se ejecutaron con éxito, sin fallos ni errores, y la compilación fue exitosa. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Esto ejecutará todas las pruebas del proyecto y generará reportes. En el caso de Gradle, un comando similar sería:

* gradle test

La ejecución desde la línea de comando facilita su incorporación en pipelines de CI/CD, y no depende de un entorno gráfico. También resulta útil para entornos donde no se dispone de un IDE, como servidores de integración continua.

 \


3. **Filtrado de pruebas**

Tanto en el IDE como desde la línea de comando, es posible filtrar las pruebas a ejecutar por paquete, clase o método específico. Esto permite repetir rápidamente solo las pruebas fallidas o centrarse en una funcionalidad en particular sin tener que esperar a que se ejecuten todas las pruebas.

## **Lectura de reportes de pruebas (consola, reportes HTML e integraciones con CI/CD)**

Aprender a interpretar los resultados y métricas generadas tras la ejecución de las pruebas es esencial para comprender el estado de la calidad del código.

1. **Consola de salida**

Al ejecutar las pruebas, la consola mostrará un resumen del número de pruebas ejecutadas, pasadas y fallidas. Por ejemplo:

**Figura 14**

*Resumen de ejecución de pruebas*



*Nota*. Se ejecutaron 10 pruebas, con 1 fallo y 0 errores o pruebas omitidas. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) 

Aquí se puede identificar rápidamente si hay problemas y, en caso de fallo, el tipo de error o excepción generada.

 \


2. **Reportes HTML y de herramientas de construcción**

Maven y Gradle generan reportes detallados en formato HTML o XML. Estos reportes suelen guardarse en directorios específicos (por ejemplo, target/surefire-reports en Maven) y muestran:

* Número total de pruebas ejecutadas, fallidas, con errores o ignoradas.

* Tiempos de ejecución de cada prueba.

* Mensajes de error, excepciones y *stack traces* completos que permiten un análisis detallado.

Estos reportes son útiles para su posterior revisión o para adjuntarlos en la documentación del proyecto.

3. **Integraciones con CI/CD (Jenkins, GitHub Actions, GitLab CI)**

Las herramientas de integración continua ejecutan las pruebas automáticamente en cada commit o push al repositorio. De esta forma:

* Se generan reportes en tiempo real accesibles desde la interfaz de la herramienta de CI/CD.

* Se pueden configurar notificaciones (por ejemplo, vía correo o chat) que alerten al equipo sobre fallos o regresiones.

* Se facilitan métricas más avanzadas, como tendencias en el tiempo del porcentaje de éxito en las pruebas, cobertura de código y análisis estático.

4. **Interpretación de resultados y métricas**

* **Tasa de éxito:** porcentaje de pruebas que pasaron con éxito. Una tasa alta sugiere buena estabilidad.

* **Pruebas fallidas repetidamente:** indican un posible bug persistente en el código.

* **Pruebas lentas:** algunas pruebas pueden ser indicadoras de problemas de rendimiento o de dependencias externas innecesarias.

Una correcta interpretación de los reportes permite realizar mejoras continuas en el código y en el conjunto de pruebas.  \


## **Identificación y resolución de errores y fallos en las pruebas**

Es fundamental desarrollar la capacidad de diagnosticar las causas de fallos en las pruebas unitarias y adoptar una estrategia para corregirlos de manera eficiente.

1. **Tipos de fallos comunes en las pruebas**

* **Asserts fallidos:** el resultado real no coincide con el esperado. Esto puede indicar un error en la lógica, datos de prueba incorrectos o cambios recientes en el código que no se reflejaron en la prueba.

* **Excepciones no esperadas:** ocurren cuando la prueba no anticipa que un método lance una excepción. Puede ser un bug no contemplado o un comportamiento no documentado.

* **Problemas de configuración del entorno de pruebas:** dependencias no configuradas, problemas con el classpath o mocks mal definidos pueden causar fallos que no se deben a la lógica del método probado.

2. **Estrategias para resolver problemas detectados**

* **Revisar el mensaje de error y el stack trace:** el primer paso ante una prueba fallida es leer el mensaje de error proporcionado y analizar la traza de la excepción. Esto guía hacia la línea de código y el método donde se generó el fallo.

* **Depuración y breakpoints:** ejecutar la prueba en modo depuración, estableciendo puntos de interrupción para inspeccionar variables y flujos de control. Esto ayuda a entender el estado interno del programa en el momento del fallo.

* **Revisar los datos de prueba:** asegurar que las entradas a la prueba sean correctas, realistas y consistentes con las condiciones esperadas.

* **Actualizar las pruebas tras cambios en el código:** si el método ha cambiado su lógica o su firma, las pruebas deben reflejar dichos cambios.

* **Refactorizar el código o la prueba:** si el problema es recurrente, puede ser necesario refactorizar el código probado para hacerlo más testeable, o mejorar la calidad de la propia prueba.

 \


3. **Mejora continua con base en los resultados**

Cada error encontrado y resuelto es una oportunidad para:

* Mejorar las pruebas agregando casos que eviten regresiones.

* Refinar la lógica del microservicio para manejar adecuadamente situaciones especiales.

* Incrementar la cobertura y la robustez del conjunto de pruebas, fortaleciendo la calidad global del servicio.

 \


# **Buenas prácticas y estándares de calidad en pruebas unitarias**

Para garantizar la confiabilidad y el valor de las pruebas unitarias, no basta con crearlas, sino que deben mantenerse bajo altos estándares de calidad, alinearse con los requerimientos del proyecto y seguir las mejores prácticas de la industria. Estas consideraciones, desde el uso de métricas de cobertura hasta la mantención continua, contribuyen a que las pruebas sean eficientes, fáciles de entender y sostenibles a largo plazo.



### **Cobertura de código y métricas relevantes**

* **Cobertura de código:** evalúa qué porcentaje del código es ejercitado por las pruebas. Herramientas como JaCoCo generan reportes que muestran líneas, métodos y rutas lógicas cubiertas.

* **Tipos de cobertura:**

   * **Líneas:** porcentaje de líneas ejecutadas por las pruebas.

   * **Ramas lógicas:** porcentaje de decisiones (if/else) cubiertas.

   * **Clases/Métodos:** porcentaje de clases y métodos que se ponen a prueba al menos una vez.

 * \


* **Métricas adicionales:**

   * **Índice de fallas frecuentes:** mide cuántas veces se repiten fallos, evidenciando inestabilidad.

   * **Tiempo de ejecución:** indica la eficiencia de las pruebas; cuanto más rápidas, más fluidos resultan los ciclos de integración.

* **Interpretación correcta:** una alta cobertura es útil, pero no lo es todo. Debe complementarse con pruebas significativas y casos que validen realmente la lógica del código, no solo incrementar un porcentaje numérico.

## **Mantención y refactorización de las pruebas**



* **Cuidar el código de prueba:** las pruebas no son artefactos secundarios; deben ser mantenidas, optimizadas y refactorizadas con la misma disciplina que el código productivo.

* **Refactorización continua:**

   * **Eliminar duplicaciones:** extraer métodos comunes para evitar repetir código en múltiples pruebas.

   * **Nombres descriptivos:** nombrar métodos de prueba según la funcionalidad que validan.

   * **Uso adecuado de mocks:** emplear mocks y stubs sin exagerar, manteniendo la claridad de las pruebas.

 * \


* **Actualización permanente:** al cambiar el código productivo, actualiza las pruebas en consecuencia. Esto previene falsos positivos (pruebas que pasan sin validar la nueva lógica) y falsos negativos (fallos por no adaptar la prueba a los cambios).

## **Cumplimiento de estándares de la industria y requisitos del proyecto**



* **Estándares internos y de la industria:** seguir normas de codificación, formateo, nomenclatura y estilo asegura coherencia. Aplicar principios como FIRST (Fast, Independent, Repeatable, Self-Validating, Timely) mejora la calidad de las pruebas.

* **Alineación con requerimientos del proyecto:** las pruebas deben reflejar los requerimientos funcionales y no funcionales (por ejemplo, validación de tiempos de respuesta). Esto garantiza que las pruebas sean relevantes para los objetivos del sistema.

* **Validación continua:** auditorías, revisiones de código y herramientas de CI/CD pueden requerir reportes de cobertura y cumplimiento de estándares. Mantener buenas prácticas consolida la confianza del equipo y las partes interesadas en la calidad del software.

 \


**Tabla 1**

*Recomendaciones para el cumplimiento de estándares de la industria*

<table>
  <tr>
   <td>

**Aspecto**

</td>
   <td>

**Recomendación**

</td>
   <td>

**Beneficio**

</td>
  </tr>
  <tr>
   <td>

Cobertura de código

</td>
   <td>

Medir con herramientas (JaCoCo) y revisar ramas lógicas.

</td>
   <td>

Mayor visibilidad sobre la eficacia de las pruebas.

</td>
  </tr>
  <tr>
   <td>

Métricas adicionales

</td>
   <td>

Monitorear fallas frecuentes y tiempo de ejecución.

</td>
   <td>

Detección de inestabilidades y mejora continua.

</td>
  </tr>
  <tr>
   <td>

Mantenimiento

</td>
   <td>

Refactorizar, eliminar duplicaciones, nombres claros.

</td>
   <td>

Pruebas más limpias, comprensibles y sostenibles.

</td>
  </tr>
  <tr>
   <td>

Actualización

</td>
   <td>

Ajustar las pruebas ante cambios en el código.

</td>
   <td>

Evitar falsos positivos/negativos, alineación con la lógica actual.

</td>
  </tr>
  <tr>
   <td>

Estándares de calidad

</td>
   <td>

Adoptar reglas internas e industriales (ej. FIRST).

</td>
   <td>

Asegurar coherencia, confiabilidad y mejores prácticas reconocidas.

</td>
  </tr>
  <tr>
   <td>

Requerimientos del proyecto

</td>
   <td>

Integrar validaciones funcionales y no funcionales.

</td>
   <td>

Pruebas orientadas a metas de negocio y calidad del producto.

</td>
  </tr>
</table>

 \


# **Ejemplo de implementación**

A continuación, se presenta un ejemplo de código que ilustra la creación, ejecución y análisis de una prueba unitaria en un microservicio hipotético. Este ejemplo se alinea con los principios abordados anteriormente: 

* Se define un requerimiento claro,

* Se implementa una prueba usando JUnit,

* Se utilizan aserciones significativas, y

* Se prepara el terreno para el análisis posterior de resultados y la mejora continua.

# **Descargable**

Puedes descargar el ejemplo de código en el siguiente enlace: [https://ava.duoc.cl/bbcswebdav/xid-5318673_1](https://ava.duoc.cl/bbcswebdav/xid-5318673_1)

### **Contexto del microservicio:**

* El microservicio gestiona pedidos de una tienda en línea.

* Existe un método calculateTotal(Order order) en la clase OrderService que suma los precios de todos los ítems del pedido.

* El requerimiento: *&quot;El total del pedido debe ser la suma correcta de los precios de los ítems añadidos&quot;.*

 \


A continuación, te mostramos la clase OrderService y las clases Order e Item como un ejemplo simplificado:

**Figura 15**

*Clase Item para gestión de productos*



*Nota.* Define un producto con nombre y precio, incluyendo un método para obtener el precio. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


La clase Order representa un pedido que contiene múltiples ítems:

**Figura 16**

*Clase Order para gestión de pedidos* 



*Nota.* Permite agregar y obtener elementos dentro de un pedido mediante una lista. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


Servicio, que calcula el total de un pedido:

**Figura 17**

*Servicio de cálculo total del pedido* 



*Nota.* Calcula el total de un pedido sumando los precios de los elementos en la lista. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

En la clase de prueba se utiliza JUnit para verificar que el método calculateTotal cumpla con el requerimiento definido. Esta prueba se integra con el entorno de desarrollo y puede ser ejecutada desde el IDE, la línea de comando o integrarse en un pipeline de CI/CD. Posteriormente, el análisis de sus resultados permitirá confirmar el cumplimiento del requerimiento y, en caso contrario, guiará las mejoras necesarias. **\**


**Figura 18**

*Prueba unitaria para cálculo de total del pedido*



*Nota*. Valida que el total calculado para un pedido coincida con la suma de los precios de los ítems. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) \


**Análisis del código y resultados de la prueba:**

* **Aserción (assertEquals):** verifica si el total calculado (resultado) coincide con el valor esperado (3500.0).

* **Cumplimiento del requerimiento:** si la prueba es exitosa, indica que el microservicio cumple con la lógica establecida. Si falla, el reporte mostrará un mensaje que guiará al desarrollador a revisar la implementación.

* **Mejora continua:** a partir de esta prueba, se pueden agregar otros casos, como un pedido sin ítems o con ítems de costo cero, aumentando la cobertura y la confianza en la calidad del servicio.

**Próximos pasos:**

* Ejecutar esta prueba desde el IDE o la línea de comando y analizar la salida.

* Verificar cobertura de código con una herramienta como JaCoCo.

* Ajustar la implementación o agregar nuevas pruebas en función de los resultados obtenidos (fallos, excepciones u otros comportamientos inesperados).

* Documentar los hallazgos y planificar mejoras basadas en los resultados.

 \


# **![](image31.png)**Cierre de la semana**

A lo largo de esta semana, hemos profundizado en el diseño, ejecución y análisis de pruebas unitarias orientadas a microservicios, así como en la adopción de buenas prácticas y estándares de calidad. Comenzamos revisando la identificación de las unidades de prueba y el uso de aserciones con JUnit, estableciendo las bases para validar funcionalmente el código, detectar errores tempranamente y mantener la calidad del microservicio. Posteriormente, abordamos la ejecución de pruebas desde el IDE y la línea de comando, la interpretación de reportes, y la resolución de problemas a través de la depuración y el ajuste continuo de las pruebas.

Finalmente, integramos el concepto de calidad total en las pruebas unitarias mediante la cobertura de código, métricas, cumplimiento de estándares de la industria y la mantención proactiva del conjunto de pruebas. Esta visión integral no solo refuerza la funcionalidad esperada en los microservicios, sino que también promueve un enfoque sistemático y sostenible en el proceso de desarrollo. De esta forma, las pruebas unitarias pasan de ser una mera formalidad a convertirse en un pilar fundamental que asegura la confiabilidad, mantenibilidad y evolución continua del sistema. \


 # **Referencias**

* JUnit 5 User Guide: proporciona documentación detallada sobre el uso de JUnit 5 para pruebas unitarias en Java. [https://junit.org/junit5/docs/current/user-guide/](https://junit.org/junit5/docs/current/user-guide/)

* Mockito Framework Site: sitio oficial de Mockito, un framework de simulación para pruebas unitarias en Java. [https://site.mockito.org/](https://site.mockito.org/)

 \


# **Glosario**

En el siguiente glosario encontrarás términos esenciales en la seguridad de aplicaciones backend y el uso de Spring Security. Te invitamos a leerlos y familiarizarte con todos ellos.

<table>
  <tr>
   <td>

**Término**

</td>
   <td>

**Definición**

</td>
  </tr>
  <tr>
   <td>

**Prueba Unitaria**

</td>
   <td>

Test que valida el correcto funcionamiento de una unidad específica de código, usualmente un método o función.

</td>
  </tr>
  <tr>
   <td>

**Microservicios**

</td>
   <td>

Arquitectura que divide la aplicación en servicios independientes, pequeños y altamente desacoplados.

</td>
  </tr>
  <tr>
   <td>

**JUnit**

</td>
   <td>

Framework de pruebas unitarias para Java, que facilita la creación, ejecución y reporte de casos de prueba.

</td>
  </tr>
  <tr>
   <td>

**Aserción**

</td>
   <td>

Instrucción que verifica si el resultado real de una operación coincide con el esperado en una prueba.

</td>
  </tr>
  <tr>
   <td>

**Mock**

</td>
   <td>

Objeto simulado que sustituye dependencias reales en pruebas, aislando la unidad bajo test.

</td>
  </tr>
  <tr>
   <td>

**Cobertura de Código**

</td>
   <td>

Métrica que indica qué porcentaje del código es ejercitado durante la ejecución de las pruebas.

</td>
  </tr>
  <tr>
   <td>

**CI/CD**

</td>
   <td>

Prácticas y herramientas para integrar, probar y desplegar código automáticamente y con frecuencia.

</td>
  </tr>
  <tr>
   <td>

**Refactorización**

</td>
   <td>

Proceso de mejorar la estructura interna del código sin alterar su funcionalidad externa.

</td>
  </tr>
  <tr>
   <td>

**Depuración**

</td>
   <td>

Proceso de identificar y corregir errores o comportamientos inesperados en el código.

</td>
  </tr>
  <tr>
   <td>

**Dependencia**

</td>
   <td>

Relación entre componentes, donde uno requiere el código u objeto de otro para funcionar.

</td>
  </tr>
  <tr>
   <td>

**Reporte de Pruebas**

</td>
   <td>

Documento o salida generada tras la ejecución de las pruebas, detallando resultados, fallos y métricas.

</td>
  </tr>
  <tr>
   <td>

**Métricas de Calidad**

</td>
   <td>

Indicadores (como cobertura o tiempo de ejecución) para evaluar eficacia y estado de las pruebas.

</td>
  </tr>
  <tr>
   <td>

**Rama Lógica**

</td>
   <td>

Camino alternativo en el flujo del programa (por ejemplo, if/else), que debe ser probado.

</td>
  </tr>
  <tr>
   <td>

**Aislamiento**

</td>
   <td>

Ejecución de pruebas sin influencias externas, validando únicamente la unidad bajo test.

</td>
  </tr>
  <tr>
   <td>

**Caso de Prueba**

</td>
   <td>

Escenario que describe condiciones y resultados esperados para evaluar una funcionalidad específica.

</td>
  </tr>
  <tr>
   <td>

**Ejecución de Pruebas**

</td>
   <td>

Proceso de correr los casos de prueba desde IDE, línea de comando o un pipeline de CI/CD.

</td>
  </tr>
  <tr>
   <td>

**IDE**

</td>
   <td>

Entorno de Desarrollo Integrado que combina herramientas de edición, compilación y depuración.

</td>
  </tr>
  <tr>
   <td>

**Estructura de Pruebas**

</td>
   <td>

Forma en que se organizan y agrupan las clases y métodos de prueba en el proyecto.

</td>
  </tr>
  <tr>
   <td>

**Estándares de la Industria**

</td>
   <td>

Buenas prácticas, convenciones y normas ampliamente aceptadas que mejoran la calidad del software.

</td>
  </tr>
  <tr>
   <td>

**Cumplimiento de Requerimientos**

</td>
   <td>

Garantía de que las funcionalidades y pruebas cubren las especificaciones definidas por el proyecto.

</td>
  </tr>
</table>

 \



![](image32.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 


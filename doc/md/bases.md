![](image1.png)



**Guía de aprendizaje** 

**Integración y aplicación de pruebas unitarias en microservicios con JUnit**

**Exp 2 – Semana 6**

# **Desarrollo Backend II (PBY2202)**

**Facilitador disciplinar:** Ányelo Castellón Ríos

**Asesor par:** Ignacio Villarroel Sánchez \


**Índice**

[Introducción a la semana	4]()

[Resultado de aprendizaje	5]()

[Conceptos relevantes	5]()

[Preguntas activadoras	6]()

[Actividad	6]()

[Reforcemos conceptos previos antes de JUnit	7]()

[JUnit: definición de Pruebas unitarias y su rol en microservicios	7]()

[Configuración básica de JUnit	9]()

[Generación y lectura de resultados con JUnit	13]()

[Análisis de resultados de pruebas unitarias	14]()

[Interpretación de reportes de pruebas	15]()

[Métricas y calidad de las pruebas	17]()

[Identificación de Patrones de fallos	18]()

[Estrategias para mejorar la calidad a partir del análisis	21]()

[Refactorización orientada por pruebas	21]()

[Ajuste y mejora de casos de prueba	24]()

[Integración Continua (CI) y Monitoreo Permanente	25]()

[Ejemplo práctico	26]()

[Paso a paso del ejemplo	27]()

[Creación de Pruebas unitarias	30]()

[Ejecución de Pruebas Unitarias	32]()

[Análisis de resultados	32]()

[Cierre de la semana	33]()

[Referencias	34]()

[Glosario	35]()

[Apuntes	38]()



 \


# **![](image3.png)**Introducción a la semana**

En esta semana, nos enfocaremos en el análisis profundo de los resultados de las pruebas unitarias. A partir de la información generada por herramientas estándar en la industria, como JUnit, aprenderás a interpretar reportes, métricas y patrones de fallos, de modo que puedas evaluar si el código cumple con los requisitos y se mantiene en línea con las expectativas de calidad.

Además de aprender cómo leer e interpretar los resultados de las pruebas, aprenderás a identificar áreas de mejora y aplicar los hallazgos encontrados para optimizar tanto el código como las propias pruebas, lo que evitará que errores no detectados se propaguen a otras etapas del desarrollo y/o producción.

Los contenidos clave que encontrarás en esta guía son:

* Interpretación de reportes de pruebas unitarias generados por JUnit

* Identificación de fallos, métricas y tendencias en los resultados

* Acciones de mejora a partir del análisis, como refactorización del código o ajuste de casos de prueba

 **\**


# **Resultado de aprendizaje**

### **El estudiante será capaz de:**

**RA2.** Crea pruebas unitarias en los microservicios, usando tecnologías como JUnit, para asegurar calidad del desarrollo elaborado, de acuerdo a requerimientos y estándares de la industria.

### **Indicador de logro:**

**IL5.** Configura un entorno de pruebas unitarias en microservicios, utilizando diversas herramientas de testing y frameworks, garantizando la cobertura y automatización de las pruebas. 

**IL6.** Diseña pruebas unitarias para microservicios utilizando JUnit, cumpliendo con los requerimientos especificados y estándares de la industria.

**IL7.** Ejecuta pruebas unitarias en microservicios desarrollados utilizando JUnit y herramientas actuales, garantizando la calidad y funcionalidad del código.

**IL8.** Realiza análisis de los resultados de las pruebas unitarias en microservicios para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

# **Conceptos relevantes**

<table>
  <tr>
   <td>

JUnit

</td>
   <td>

Análisis de resultados

</td>
   <td>

Métricas

</td>
  </tr>
  <tr>
   <td>

Reportes

</td>
   <td>

Fallos

</td>
   <td>

Requerimientos

</td>
  </tr>
  <tr>
   <td>

Calidad

</td>
   <td>

Refactorización

</td>
   <td>

Mejora continua

</td>
  </tr>
</table>

 **\**


# **Preguntas activadoras**

* ¿De qué manera el análisis de los reportes generados por JUnit te permite identificar problemas de calidad en el código y priorizar su resolución?

* ¿Cómo influye la interpretación de las métricas de prueba (tiempo de ejecución, porcentaje de éxito, cobertura) en la toma de decisiones para mejorar la calidad del servicio?

* ¿De qué forma reconocer patrones de fallos en las pruebas unitarias puede orientarte hacia una refactorización más efectiva de módulos específicos en el entorno de microservicios?

* ¿Cómo integrarías el análisis continuo de los resultados de las pruebas unitarias en tu flujo de desarrollo para asegurar una mejora constante en la calidad del producto?

# **Actividad**

En esta sexta semana, realizarás una actividad sumativa grupal (2 a 3 integrantes), llamada &quot;Integrando, aplicando y analizando resultados de pruebas unitarias en microservicios con JUnit&quot;, donde deberás integrar un entorno de pruebas unitarias en microservicios utilizando JUnit, garantizando la cobertura y automatización.

 **\**


# **Reforcemos conceptos previos antes de JUnit**

Antes de profundizar en el uso de JUnit y su aplicación en microservicios, es importante repasar algunos conceptos clave que servirán como base para entender y aprovechar mejor las herramientas y estrategias que se abordarán en esta guía. Estos conceptos conectan directamente con los pasos que realizaremos más adelante y ayudan a garantizar una transición fluida hacia el uso práctico de JUnit.



## **JUnit: definición de Pruebas unitarias y su rol en microservicios**

### **¿Qué son las Pruebas unitarias?**

Las pruebas unitarias son un tipo de verificación enfocada en evaluar la funcionalidad de las partes más pequeñas y aisladas del código (por lo general, una función o método específico). Su principal propósito es asegurar que cada unidad de software cumpla con los requerimientos esperados de manera independiente. De esta forma, se detectan errores desde etapas tempranas del desarrollo, lo que reduce costos y esfuerzo de corrección en fases posteriores. \


**Beneficios de las Pruebas unitarias en el Ciclo de desarrollo**

* **Detección temprana de errores:** localizar problemas en fases tempranas facilita su corrección con menor impacto en tiempo y recursos.

* **Fomento de la confianza en el código:** contar con una batería sólida de pruebas unitarias permite realizar cambios y refactorizaciones con la certeza de no introducir errores de forma inadvertida.

* **Documentación viva:** las pruebas unitarias sirven como una especificación técnica, ya que describen la funcionalidad esperada del código y pueden utilizarse como referencia para nuevos desarrolladores.

**El rol de las pruebas unitarias en la arquitectura de microservicios**

Los microservicios dividen la aplicación en componentes independientes, cada uno con su propia lógica de negocio y recursos. Este paradigma favorece la escalabilidad, el despliegue continuo y la resiliencia. Sin embargo, también requiere un mayor esfuerzo de pruebas. En este contexto, las pruebas unitarias resultan cruciales para:

* **Asegurar integridad interna:** antes de integrar un microservicio con otros componentes, es fundamental verificar que cada pieza cumpla su cometido de manera aislada.

* **Promover la independencia:** al garantizar el correcto funcionamiento de cada microservicio individualmente, se facilita la identificación de errores y su corrección sin afectar el sistema en su totalidad.

* **Facilitar la escalabilidad:** una arquitectura de microservicios acompañada de un conjunto sólido de pruebas unitarias permite agregar o modificar servicios con mayor agilidad, reduciendo el riesgo de generar regresiones. \


## **Configuración básica de JUnit**



Como ya sabemos, JUnit es un framework de pruebas unitarias para Java que entrega una forma estructurada de crear, ejecutar y reportar pruebas, permitiendo a los desarrolladores verificar el comportamiento del código de forma automatizada. Las principales ventajas de JUnit incluyen:

* **Simplicidad de uso**: cuenta con anotaciones e infraestructura sencillas, lo que facilita su implementación en proyectos existentes.

* **Integración con herramientas de desarrollo**: JUnit se integra fácilmente con IDEs (como IntelliJ, Eclipse) y servidores de integración continua (como Jenkins o GitHub Actions), favoreciendo un flujo de desarrollo continuo.

* **Reportes claros y métricas de cobertura**: su compatibilidad con herramientas de reporte y análisis permite visualizar resultados y métricas, indispensables para el posterior análisis de la calidad del software.

Ahora que hemos revisado rápidamente sus ventajas, podemos comenzar a utilizar JUnit en un proyecto Java. Para hacerlo, sigue los siguientes pasos:  **\**


**Paso 1 - Dependencia:** incluir la dependencia de JUnit (por ejemplo, JUnit 5) en el archivo de configuración del proyecto (pom.xml en Maven, build.gradle en Gradle).

**Figura 1**

*Dependencia JUnit 4*



*Nota.* Configuración de Maven para usar JUnit. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

**Paso 2 - Estructura de pruebas:** ubicar las clases de prueba en un directorio dedicado (generalmente src/test/java).

**Figura 2**

*Estructura de pruebas en el proyecto*



*Nota.* Organización de pruebas unitarias en el paquete com.test.test.microservices. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 **\**


**Paso 3 - Creación de casos de prueba:** cada caso de prueba se define como un método anotado con @Test. Dentro de ese método se implementan aserciones para validar el comportamiento esperado.

**Figura 3**

*Ejemplo de Test para un cálculo del total en un pedido*



*Nota.* Verifica que el total del pedido coincida con la suma de los precios de los ítems añadidos. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 **\**


**Paso 4 - Ejecución:** las pruebas se ejecutan directamente desde el IDE o mediante comandos de construcción (ej: mvn test o gradle test). Al final, JUnit presenta un reporte indicando qué pruebas pasaron, cuáles fallaron y por qué.

**Figura 4**

*Ejecución de pruebas y compilación*



*Nota.* Todas las pruebas se ejecutaron con éxito, sin fallos ni errores, y la compilación fue exitosa. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Una vez que hayas ejecutado tu prueba, es momento de analizar los resultados entregados por JUnit, lo que veremos a continuación.

 **\**


## **Generación y lectura de resultados con JUnit**



Una vez que las pruebas han sido ejecutadas, JUnit puede entregar los resultados de las pruebas en distintos formatos, tales como texto plano, HTML o XML, dependiendo de la configuración empleada. Sin embargo, más allá de las diferencias en los formatos, los resultados incluidos en los reportes consideran:

* **Listado de pruebas exitosas y fallidas:** permite identificar rápidamente qué partes del código presentan problemas.

* **Mensajes de error y stack traces:** ofrecen pistas sobre la causa raíz del fallo, acelerando la localización del error.

* **Métricas adicionales (con herramientas externas):** integrando JUnit con herramientas de cobertura como JaCoCo, por ejemplo, es posible visualizar el porcentaje de líneas cubiertas, ayudando a evaluar la eficacia de las pruebas y la necesidad de agregar más casos.

Los conceptos que hemos revisado en la sección sientan las bases para comprender y analizar los resultados de las pruebas unitarias en microservicios. Dominar estos fundamentos, desde la definición de pruebas unitarias hasta la configuración básica de JUnit y la interpretación de reportes iniciales, es un paso esencial para avanzar hacia el análisis profundo de resultados y la mejora continua de la calidad del software. 

 **\**


# **Análisis de resultados de pruebas unitarias**

Ahora que hemos repasado los conceptos fundamentales relacionados con las pruebas unitarias —su importancia, uso de herramientas como JUnit y la relevancia de cubrir los principales escenarios— daremos el siguiente paso para centrarnos específicamente en la necesidad de realizar análisis de los resultados de las pruebas unitarias en microservicios para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

En esta nueva sección, profundizaremos en:

* **Cómo interpretar reportes y métricas** que arrojan las pruebas unitarias,

* **Identificación de patrones de fallos** para distinguir entre defectos reales y problemas en las propias pruebas, y

* **Estrategias de mejora continua**, como refactorización y ajustes en el set de pruebas, que se derivan de dicho análisis.

De esta manera, pasamos de una visión teórica de las pruebas unitarias a un enfoque práctico de **evaluación y refinamiento** del código, y sentamos las bases para implementar un ciclo de mejora continua que asegure la calidad y confiabilidad de cada microservicio. \


## **Interpretación de reportes de pruebas**

Los reportes generados tras la ejecución de pruebas unitarias muestran el estado general del software, presentando información sobre el total de pruebas realizadas, resultado (aprobadas o fallidas), errores detectados y tiempo de ejecución. Estos reportes se generan de forma automática y suelen estar disponibles en formatos legibles y para herramientas de integración continua.

**Figura 5**

*Test en consola*



*Nota.* Todas las pruebas se ejecutaron con éxito, sin fallos ni errores, y la compilación fue exitosa. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

### **Elementos clave de los reportes**

* Cantidad total de pruebas ejecutadas.

* Número de pruebas exitosas y fallidas.

* Mensajes de error y *stack traces* asociados a cada fallo.

* Tiempo total y por prueba.

* Enlaces o secciones detalladas para profundizar en el análisis de cada caso.  **\**


**Tabla 1**

*Ejemplo de información en un reporte*

<table>
  <tr>
   <td>

**Elemento**

</td>
   <td>

**Descripción**

</td>
   <td>

**Ejemplo**

</td>
  </tr>
  <tr>
   <td>

Pruebas totales

</td>
   <td>

Cantidad de casos ejecutados

</td>
   <td>

30 pruebas en total

</td>
  </tr>
  <tr>
   <td>

Exitosas

</td>
   <td>

Casos sin fallas

</td>
   <td>

28 aprobadas

</td>
  </tr>
  <tr>
   <td>

Fallidas

</td>
   <td>

Casos con error

</td>
   <td>

2 con AssertionError

</td>
  </tr>
  <tr>
   <td>

Mensajes de error

</td>
   <td>

Detalles del fallo

</td>
   <td>

expected &lt;100&gt; but was &lt;90&gt;

</td>
  </tr>
  <tr>
   <td>

Tiempo de ejecución

</td>
   <td>

Duración de cada prueba

</td>
   <td>

testCalcularTotal (5ms)

</td>
  </tr>
</table>

![](image12.png)

 \


## **Métricas y calidad de las pruebas**



### **Cobertura de código**

La cobertura indica qué porcentaje del código es ejercitado por las pruebas. Una cobertura alta sugiere mayor probabilidad de detectar defectos, aunque no garantiza calidad absoluta. De igual forma, una cobertura baja señala áreas del código sin validar, potencialmente riesgosas.

### **Número de pruebas superadas y fallidas**

La relación entre pruebas aprobadas y fallidas aporta una visión general de la estabilidad del código. Un alto número de fallos puede reflejar problemas lógicos, mala configuración de pruebas o casos no contemplados en el diseño del software.

### **Tiempo de ejecución de las pruebas**

Pruebas excesivamente lentas pueden indicar ineficiencias en el código, dependencias externas no simuladas o diseños que no favorecen el aislamiento de componentes. Reducir el tiempo de ejecución optimiza el flujo de desarrollo e integración continua.

### **Inestabilidad de las pruebas (test flakiness)**

Las pruebas inestables, que en ocasiones son exitosas y en otras fallan sin cambios en el código, generan desconfianza en el suite de pruebas. Esta situación suele deberse a factores externos, entornos no deterministas, datos variables y/o dependencias mal simuladas. **\**


**Tabla 2**

*Tabla de métricas y acciones sugeridas*

<table>
  <tr>
   <td>

**Métrica**

</td>
   <td>

**Descripción**

</td>
   <td>

**Acción Potencial**

</td>
  </tr>
  <tr>
   <td>

Cobertura 80%

</td>
   <td>

80% del código se ejercita en pruebas

</td>
   <td>

Añadir pruebas para casos no cubiertos

</td>
  </tr>
  <tr>
   <td>

2 fallos en 30 Pruebas

</td>
   <td>

Falta de robustez en ciertas funciones

</td>
   <td>

Revisión del módulo fallido o la prueba

</td>
  </tr>
  <tr>
   <td>

Tiempo total 300ms

</td>
   <td>

Pruebas lentas señalan ineficiencias

</td>
   <td>

Refactorizar para mejorar rendimiento

</td>
  </tr>
  <tr>
   <td>

Prueba inestable

</td>
   <td>

Resultado impredecible

</td>
   <td>

Asegurar datos deterministas y entornos aislados

</td>
  </tr>
</table>

## **Identificación de Patrones de fallos**

### **Patrones recurrentes**

Si una misma prueba o un conjunto de pruebas relacionadas fallan sistemáticamente, existe un patrón de fallo. Este patrón puede apuntar a defectos crónicos en el código o a un mal diseño de las pruebas.

### **Falsos positivos y falsos negativos**

* **Falso positivo:** la prueba indica un error, pero el código está correcto. Esto suele ocurrir cuando las condiciones de prueba están mal definidas o las aserciones no reflejan el requerimiento real.

* **Falso negativo:** la prueba no detecta un error que sí existe en el código. Esto ocurre cuando las pruebas no cubren adecuadamente el caso fallido o las aserciones son insuficientes.

 * \


**Tabla 3**

*Falso positivo y falso negativo*

<table>
  <tr>
   <td>

**Tipo de error**

</td>
   <td>

**Escenario de ejemplo**

</td>
   <td>

**Causa probable**

</td>
   <td>

**Cómo solucionarlo**

</td>
  </tr>
  <tr>
   <td>

Falso Positivo

</td>
   <td>

La prueba indica que esMayorDeEdad(17) debe retornar false, pero curiosamente la prueba marca “error” al afirmar que debería ser true.

</td>
   <td>

**Aserción mal planteada:** el test espera true cuando el requerimiento real es false (la persona tiene 17 años).

</td>
   <td>

Corregir la aserción para que coincida con la lógica de negocio real (17 años -&gt; menor de edad).

</td>
  </tr>
  <tr>
   <td>

</td>
   <td>

</td>
   <td>

Datos de prueba incorrectos o mal interpretados.

</td>
   <td>

Ajustar la documentación o los datos usados en la prueba.

</td>
  </tr>
  <tr>
   <td>

Falso Negativo

</td>
   <td>

La prueba pasa exitosamente cuando esMayorDeEdad(20) retorna false, sin detectar que la lógica está mal implementada y en realidad debería retornar true.

</td>
   <td>

**Condición insuficiente:** la prueba no contempla adecuadamente que 20 años es, por definición, mayor de edad.

</td>
   <td>

Revisar la lógica del método y las condiciones de la prueba.

</td>
  </tr>
  <tr>
   <td>

</td>
   <td>

</td>
   <td>

Aserciones incompletas o lógica demasiado laxa.

</td>
   <td>

Asegurar que el test verifique correctamente los límites y valores esperados (≥ 18 años).

</td>
  </tr>
</table>

 \


### **Distinción entre defecto real y problema en la prueba**

Para diferenciar si el origen del fallo está en el código o en la propia prueba, se debe analizar el mensaje de error, el *stack trace* y las condiciones del test. Ajustar datos de entrada, revisar aserciones o aislar dependencias puede ayudar a aclarar si se trata de un defecto funcional o de una falla en el escenario de prueba.

**Tabla 4**

*Patrones de fallo*

<table>
  <tr>
   <td>

**Tipo de fallo**

</td>
   <td>

**Descripción**

</td>
   <td>

**Ejemplo**

</td>
   <td>

**Acción**

</td>
  </tr>
  <tr>
   <td>

Defecto real del código

</td>
   <td>

El método no cumple lo esperado

</td>
   <td>

calcTotal() retorna 90 en vez de 100

</td>
   <td>

Refactorizar la lógica

</td>
  </tr>
  <tr>
   <td>

Problema en la prueba

</td>
   <td>

Las aserciones no reflejan el requerimiento

</td>
   <td>

Aserción espera 100, requerimiento es 90

</td>
   <td>

Ajustar la prueba acorde al requerimiento

</td>
  </tr>
  <tr>
   <td>

Falso Positivo

</td>
   <td>

Error reportado sin razón

</td>
   <td>

Aserción incorrecta provoca fallo

</td>
   <td>

Corregir las aserciones

</td>
  </tr>
  <tr>
   <td>

Falso Negativo

</td>
   <td>

No se detecta un error existente

</td>
   <td>

Prueba no contempla caso límite

</td>
   <td>

Agregar casos faltantes

</td>
  </tr>
</table>

La interpretación de reportes, el análisis de métricas y la identificación de patrones de fallo permiten comprender a fondo el desempeño del software que estemos probando. Esto facilita la toma de decisiones informadas para mejorar la calidad del código, asegurar el cumplimiento de los requerimientos y avanzar hacia la creación de pruebas unitarias que permitan asegurar la calidad del desarrollo acorde a los requerimientos y estándares de la industria, así como también realizar un análisis que permita verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo. 

 \


# **Estrategias para mejorar la calidad a partir del análisis**



Una vez que hayas realizado el análisis de los resultados de las pruebas unitarias, el siguiente paso es aplicar acciones concretas para elevar la calidad del desarrollo. Las estrategias que presentamos a continuación son tres: 

* Refactorización orientada a pruebas.

* Ajuste y mejora de casos de prueba.

* Integración Continua (CI) y Monitoreo Permanente.

Cada una de estas estrategias parten de la información obtenida del análisis previo, transformándola en ajustes al código, a las pruebas y a la infraestructura de integración continua.

## **Refactorización orientada por pruebas**

Consiste en realizar mejoras al código basadas en la información obtenida del análisis de resultados de las pruebas. Estas mejoras no alteran la funcionalidad final, sino que buscan optimizar la estructura interna, la claridad y la mantenibilidad del software. Dado que las pruebas unitarias ofrecen retroalimentación constante, la refactorización se guía por datos objetivos sobre calidad y rendimiento.  \


En este punto, puedes utilizar la metodología Test Driven Development (TDD), que se caracteriza porque en lugar de escribir códigos y probar su funcionamiento, se escriben primero las pruebas y luego se desarrolla el código.

Para ello, se sigue un ciclo iterativo al que se conoce como **rojo-verde-refactorizar** o r**ed green refactor.** El primer paso es escribir la prueba que define una funcionalidad o un comportamiento deseado. Después, se implementa el código que se necesita para pasar la prueba. En último lugar, se refactoriza dicho código para mejorar su calidad o estructura sin cambiar su comportamiento.    

**Figura 6**

*Ciclo TDD: Rojo - Verde - Refactorizar*



*Nota.* La imagen representa el ciclo del Desarrollo Dirigido por Pruebas (TDD), que consta de tres fases: Rojo, Verde y Refactorizar. Rocabado, M. (2018*). Introducción al Desarrollo Guiado por Pruebas (TDD).* [https://mindwaresrl.com/2015/10/18/introduccion-al-desarrollo-guiado-por-pruebas-tdd/](https://mindwaresrl.com/2015/10/18/introduccion-al-desarrollo-guiado-por-pruebas-tdd/)

 \


La ventaja que ofrece el TDD frente a otros enfoques, es que el código se prueba desde el comienzo. Por tanto, si hay errores, se pueden identificar con rapidez para corregirlos.  

### **Acciones clave en la refactorización**

* **Eliminar código redundante**: remover funciones o variables duplicadas para reducir complejidad.

* **Simplificar lógica compleja**: dividir métodos muy extensos en otros más pequeños y legibles.

* **Mejorar nomenclatura:** usar nombres descriptivos para métodos, clases y variables.

* **Adoptar buenas prácticas de diseño (SOLID):** asegurar que la arquitectura resulte flexible y escalable.

**Tabla 5**

*Posibles problemas identificados*

<table>
  <tr>
   <td>

**Problema detectado**

</td>
   <td>

**Acción de refactorización**

</td>
   <td>

**Resultado esperado**

</td>
  </tr>
  <tr>
   <td>

**Lógica repetida en 3 clases**

</td>
   <td>

Extraer la lógica a un método común

</td>
   <td>

Menor duplicación y mantenimiento

</td>
  </tr>
  <tr>
   <td>

**Métodos con más de 100 líneas**

</td>
   <td>

Dividir en sub-métodos lógicos

</td>
   <td>

Mayor legibilidad y simplicidad

</td>
  </tr>
  <tr>
   <td>

**Variables con nombres genéricos**

</td>
   <td>

Renombrar variables según su función

</td>
   <td>

Código más claro e intuitivo

</td>
  </tr>
</table>

 \


## **Ajuste y mejora de casos de prueba**

No solo el código productivo se beneficia del análisis, también las propias pruebas. Ajustar y mejorar las pruebas garantiza que reflejen adecuadamente los requerimientos, cubran escenarios omitidos, eliminen redundancias y mantengan un equilibrio entre cantidad y calidad.

### **Acciones clave para mejorar las pruebas**

* **Agregar escenarios omitidos:** incluir casos límite, valores nulos, datos extremos.

* **Eliminar redundancias:** quitar pruebas duplicadas o que no aporten información nueva.

* **Clarificar aserciones:** utilizar aserciones descriptivas para facilitar la interpretación de fallos.

* **Revisar Mocks/Stubs:** asegurar que las dependencias simuladas se ajusten al comportamiento real esperado.

**Tabla 6**

*Posibles mejoras identificadas*

<table>
  <tr>
   <td>

**Problema en las pruebas**

</td>
   <td>

**Acción de mejora**

</td>
   <td>

**Resultado esperado**

</td>
  </tr>
  <tr>
   <td>

**Falta de pruebas con datos nulos**

</td>
   <td>

Agregar casos de entrada nula

</td>
   <td>

Detección temprana de fallos

</td>
  </tr>
  <tr>
   <td>

**2 pruebas idénticas**

</td>
   <td>

Eliminar una prueba redundante

</td>
   <td>

Menor tiempo de ejecución

</td>
  </tr>
  <tr>
   <td>

**Aserciones poco específicas**

</td>
   <td>

Usar aserciones claras (ej. assertEquals) con mensajes descriptivos

</td>
   <td>

Mayor facilidad de diagnóstico

</td>
  </tr>
</table>

 \


## **Integración Continua (CI) y Monitoreo Permanente**

La integración continua permite ejecutar automáticamente las pruebas con cada cambio en el código. Esto habilita un monitoreo constante, detección temprana de regresiones y una respuesta rápida a los problemas. De esta manera, el análisis de resultados no se limita a un evento aislado, sino que se transforma en un proceso continuo.

### **Buenas prácticas en CI**

* **Ejecución automática de pruebas:** configurar pipelines que corran las pruebas en cada commit.

* **Análisis de cobertura y código estático**: integrar herramientas para medir cobertura (JaCoCo) y calidad (SonarQube).

* **Notificaciones tempranas:** activar alertas en canales de mensajería cuando las pruebas fallan.

* **Paneles de control (Dashboards):** visualizar métricas de calidad en tiempo real.

**Tabla 7**

*Herramientas y sus funciones*

<table>
  <tr>
   <td>

**Herramienta**

</td>
   <td>

**Función**

</td>
   <td>

**Beneficio**

</td>
  </tr>
  <tr>
   <td>

**Jenkins/GitHub Actions**

</td>
   <td>

Pipeline de CI para ejecutar pruebas

</td>
   <td>

Detección inmediata de fallos

</td>
  </tr>
  <tr>
   <td>

**JaCoCo**

</td>
   <td>

Reporte de cobertura de código

</td>
   <td>

Identifica áreas no testeadas

</td>
  </tr>
  <tr>
   <td>

**SonarQube**

</td>
   <td>

Análisis estático y calidad continua

</td>
   <td>

Mejora integral del código

</td>
  </tr>
</table>

 \


# **Ejemplo práctico**

Siguiendo la dinámica de las semanas anteriores, crearemos un microservicio de ejemplo y le agregaremos las pruebas unitarias con JUnit. Para ello, en el siguiente enlace encontrarás lo necesario para llevar a cabo este ejercicio: [https://ava.duoc.cl/bbcswebdav/xid-5381910_1](https://ava.duoc.cl/bbcswebdav/xid-5381910_1)

En particular, pondremos el foco en un microservicio sencillo (por ejemplo, un microservicio de “Productos”), que expone un servicio REST para administrar el listado de productos en una base de datos simulada. 

* Estructura de directorios (ejemplo con Gradle o Maven).

* Dependencias de Junit.

* Asegúrate de tener la dependencia de JUnit (versión 5 recomendada) en tu pom.xml (Maven) o build.gradle (Gradle).

* Si usas Spring Boot, también tendrás las dependencias de spring-boot-starter-test que incluyen JUnit.

 \


## **Paso a paso del ejemplo**

### **Paso 1: Crea la entidad Product**

En la carpeta “entity” definimos una clase Product, que representará al producto dentro de nuestro microservicio:

**Figura 7**

*Definición de clase Product*



*Nota.* Creación del entity Product con su constructor JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) 

  \


### **Paso 2: Crea la clase de servicio ProductService**

Aquí implementaremos la lógica principal (muy básica en este ejemplo) para administrar nuestra lista de productos:

**Figura 8**

*Creación de Service ProductService*



*Nota.* Service de product y sus métodos. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4)*.* [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) 

 

 \


### **Paso 3: Crea el controlador ProductController**

En un proyecto con Spring Boot, el controlador podría lucir así: 

**Figura 9**

*Creación del Controller ProductController*



*Nota.* RestController y sus métodos Get y Post. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 \


![](image19.png)![](image20.png)

**Importante**

Para propósitos de la demostración, estamos omitiendo aspectos como persistencia real en base de datos y configuración de Spring. El objetivo es centrarnos en la lógica de negocio y su prueba unitaria.

 

Ahora, revisemos a partir de lo aprendido con el ejemplo, como se realizaron:

* La creación de pruebas unitarias,

* La ejecución de pruebas unitarias, y

* El análisis de los resultados.

## **Creación de Pruebas unitarias**

Para diseñar nuestras pruebas unitarias, partimos analizando los métodos del servicio y definiendo qué se espera como resultado correcto. De este modo, cubrimos los diferentes escenarios y validamos si el servicio funciona correctamente bajo distintos supuestos.

 \


* **Clase de Test: ProductServiceTest**

En la carpeta test, creamos la clase de prueba correspondiente:

**Figura 10**

*Creación del Test de entity Product*



*Nota.* Test de product y sus distintas formas de métodos de prueba. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

En cada método de prueba se diseña un escenario para verificar el correcto funcionamiento de la capa de servicio. Así diseñamos pruebas unitarias contemplando diferentes condiciones (producto existente, producto inexistente, lista vacía, etc.).  \


## **Ejecución de Pruebas unitarias**

Para ejecutar las pruebas unitarias, podemos utilizar distintas herramientas:

* IDE: IntelliJ IDEA, Eclipse o VSCode, simplemente dando clic derecho en la clase de prueba y seleccionando “Run Test”.

* Terminal (Maven): mvn test

* Terminal (Gradle): gradle test

Esto generará un reporte del resultado de cada test, indicando cuántas pruebas se ejecutaron, cuántas pasaron y cuántas fallaron (si corresponde).

**![](image19.png)****![](image20.png)**

**Importante**

Asegúrate de tener configurado tu proyecto con las dependencias de prueba adecuadas (JUnit 5, por ejemplo) para que la ejecución sea exitosa.

## **Análisis de resultados**

Una vez ejecutadas las pruebas, revisamos:

* **Cantidades de pruebas exitosas y fallidas:** si alguna prueba falla, revisamos la causa en el log o reporte generado (puede estar relacionado con la lógica de negocio o con supuestos incorrectos en la prueba).

* **Cobertura de pruebas (opcionalmente):** herramientas de cobertura como JaCoCo pueden ayudarnos a revisar qué partes del código se están probando.

* **Mejoras al servicio:** si detectamos fallas lógicas en la capa de negocio, corregimos la lógica y volvemos a ejecutar los tests hasta que pasen satisfactoriamente. \


# **![](image22.png)**Cierre de la semana**

A lo largo de esta semana, hemos explorado en profundidad el análisis de los resultados de las pruebas unitarias en entornos de microservicios, comprendiendo cómo esta etapa es clave para mejorar de forma continua la calidad del desarrollo. 

Iniciamos con la interpretación de reportes y métricas, lo que nos permitió identificar fallos, evaluar el grado de cobertura y detectar patrones que señalaban áreas de mejora tanto en el código como en las propias pruebas. Posteriormente, abordamos estrategias concretas: refactorización guiada por los hallazgos, ajuste y optimización de los casos de prueba y la incorporación de prácticas de integración continua para monitorear permanentemente la calidad.

Al extraer información valiosa de cada ejecución, se adquiere la capacidad de reaccionar con agilidad ante problemas, proponer acciones correctivas informadas y mantener el producto alineado con los estándares de la industria. De este modo, las pruebas unitarias, combinadas con su análisis y mejora continua, se consolidan como un pilar esencial para garantizar la confiabilidad, mantenibilidad y evolución constante del software.

 # **Referencias**

* Bechtold, S., Brannen, S., Link, J., Merdes, M., Philipp, M., de Rancourt, J., &amp; Stein, C. (s.f.). *jUnit 5 User Guide.* [https://junit.org/junit5/docs/current/user-guide/](https://junit.org/junit5/docs/current/user-guide/)

* Mockito. (s.f.). *Mockito.* [https://site.mockito.org/](https://site.mockito.org/) \


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

Verificación de la funcionalidad de la unidad más pequeña del código (generalmente una función o método) para asegurar que opere según lo esperado.

</td>
  </tr>
  <tr>
   <td>

**JUnit**

</td>
   <td>

Framework estándar en Java para la creación, ejecución y reporte de pruebas unitarias, facilitando la verificación automática de la lógica del código.

</td>
  </tr>
  <tr>
   <td>

**Microservicio**

</td>
   <td>

Componente independiente de una arquitectura distribuida, responsable de una función específica, que puede ser desarrollado, probado y desplegado por separado.

</td>
  </tr>
  <tr>
   <td>

**Resultado de Aprendizaje (RA)**

</td>
   <td>

Declaración de lo que el estudiante debe ser capaz de hacer al finalizar una experiencia de aprendizaje, orientando el diseño de contenidos y evaluaciones.

</td>
  </tr>
  <tr>
   <td>

**Indicador de Logro (IL)**

</td>
   <td>

Evidencia concreta que permite medir el grado de logro de un resultado de aprendizaje, mostrando el avance o cumplimiento de un objetivo formativo.

</td>
  </tr>
  <tr>
   <td>

**Cobertura de Código**

</td>
   <td>

Métrica que indica el porcentaje del código ejercitado por las pruebas, evidenciando partes no testeadas que podrían ocultar defectos.

</td>
  </tr>
  <tr>
   <td>

**Asersión (Assertion)**

</td>
   <td>

Condición utilizada dentro de una prueba unitaria para verificar que un resultado obtenido coincide con el resultado esperado.

</td>
  </tr>
  <tr>
   <td>

**Test Flakiness (Inestabilidad)**

</td>
   <td>

Situación donde una prueba a veces pasa y a veces falla sin cambios en el código, dificultando la confianza en el conjunto de pruebas.

</td>
  </tr>
  <tr>
   <td>

**Refactorización**

</td>
   <td>

Proceso de mejorar la estructura interna del código sin modificar su comportamiento externo, incrementando su mantenibilidad y legibilidad.

</td>
  </tr>
  <tr>
   <td>

**Integración Continua (CI)**

</td>
   <td>

Práctica de ejecutar automáticamente las pruebas y análisis de calidad del código con cada modificación, detectando problemas de forma temprana.

</td>
  </tr>
  <tr>
   <td>

**Pipeline de CI**

</td>
   <td>

Secuencia automatizada de pasos (construcción, pruebas, análisis, despliegue) que permite un monitoreo permanente de la calidad y funcionalidad del software.

</td>
  </tr>
  <tr>
   <td>

**Mock**

</td>
   <td>

Objeto simulado que imita el comportamiento de una dependencia externa, facilitando la ejecución de pruebas unitarias aisladas.

</td>
  </tr>
  <tr>
   <td>

**Stub**

</td>
   <td>

Tipo de objeto simulado más simple que un mock, proporcionando datos predefinidos para las pruebas, sin lógica compleja.

</td>
  </tr>
  <tr>
   <td>

**Informe de Pruebas (Reporte)**

</td>
   <td>

Documento generado automáticamente tras la ejecución de las pruebas, detallando casos exitosos, fallidos, tiempo y mensajes de error.

</td>
  </tr>
  <tr>
   <td>

**Métrica**

</td>
   <td>

Valor cuantitativo que permite medir aspectos específicos de la calidad y rendimiento del código y las pruebas (ej. cobertura, tiempo de ejecución).

</td>
  </tr>
  <tr>
   <td>

**Patrones de Fallos**

</td>
   <td>

Tendencias o repeticiones en los errores detectados por las pruebas, que orientan a la identificación de problemas crónicos en código o pruebas.

</td>
  </tr>
  <tr>
   <td>

**Falso Positivo**

</td>
   <td>

Situación en que una prueba señala un error inexistente en el código, comúnmente debido a condiciones de prueba incorrectas.

</td>
  </tr>
  <tr>
   <td>

**Falso Negativo**

</td>
   <td>

Caso en el que una prueba no detecta un defecto real del código, dejando pasar un error sin reportarlo.

</td>
  </tr>
  <tr>
   <td>

**Mejora Continua**

</td>
   <td>

Enfoque sistemático para refinar y optimizar el código, las pruebas y los procesos de desarrollo, asegurando una calidad creciente a lo largo del tiempo.

</td>
  </tr>
  <tr>
   <td>

**Requerimiento**

</td>
   <td>

Necesidad o condición que el software debe cumplir, definida antes del desarrollo, y validada a través de pruebas unitarias y análisis de resultados.

</td>
  </tr>
</table>

 \


# **Apuntes**

____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________



![](image23.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 

![](image1.png)



**Guía de aprendizaje** 

**Integración y aplicación de pruebas unitarias en microservicios con JUnit**

**Exp 2 – Semana 6**

# **Desarrollo Backend II (PBY2202)**

**Facilitador disciplinar:** Ányelo Castellón Ríos

**Asesor par:** Ignacio Villarroel Sánchez \


**Índice**

[Introducción a la semana	4]()

[Resultado de aprendizaje	5]()

[Conceptos relevantes	5]()

[Preguntas activadoras	6]()

[Actividad	6]()

[Reforcemos conceptos previos antes de JUnit	7]()

[JUnit: definición de Pruebas unitarias y su rol en microservicios	7]()

[Configuración básica de JUnit	9]()

[Generación y lectura de resultados con JUnit	13]()

[Análisis de resultados de pruebas unitarias	14]()

[Interpretación de reportes de pruebas	15]()

[Métricas y calidad de las pruebas	17]()

[Identificación de Patrones de fallos	18]()

[Estrategias para mejorar la calidad a partir del análisis	21]()

[Refactorización orientada por pruebas	21]()

[Ajuste y mejora de casos de prueba	24]()

[Integración Continua (CI) y Monitoreo Permanente	25]()

[Ejemplo práctico	26]()

[Paso a paso del ejemplo	27]()

[Creación de Pruebas unitarias	30]()

[Ejecución de Pruebas Unitarias	32]()

[Análisis de resultados	32]()

[Cierre de la semana	33]()

[Referencias	34]()

[Glosario	35]()

[Apuntes	38]()



 \


# **![](image3.png)**Introducción a la semana**

En esta semana, nos enfocaremos en el análisis profundo de los resultados de las pruebas unitarias. A partir de la información generada por herramientas estándar en la industria, como JUnit, aprenderás a interpretar reportes, métricas y patrones de fallos, de modo que puedas evaluar si el código cumple con los requisitos y se mantiene en línea con las expectativas de calidad.

Además de aprender cómo leer e interpretar los resultados de las pruebas, aprenderás a identificar áreas de mejora y aplicar los hallazgos encontrados para optimizar tanto el código como las propias pruebas, lo que evitará que errores no detectados se propaguen a otras etapas del desarrollo y/o producción.

Los contenidos clave que encontrarás en esta guía son:

* Interpretación de reportes de pruebas unitarias generados por JUnit

* Identificación de fallos, métricas y tendencias en los resultados

* Acciones de mejora a partir del análisis, como refactorización del código o ajuste de casos de prueba

 **\**


# **Resultado de aprendizaje**

### **El estudiante será capaz de:**

**RA2.** Crea pruebas unitarias en los microservicios, usando tecnologías como JUnit, para asegurar calidad del desarrollo elaborado, de acuerdo a requerimientos y estándares de la industria.

### **Indicador de logro:**

**IL5.** Configura un entorno de pruebas unitarias en microservicios, utilizando diversas herramientas de testing y frameworks, garantizando la cobertura y automatización de las pruebas. 

**IL6.** Diseña pruebas unitarias para microservicios utilizando JUnit, cumpliendo con los requerimientos especificados y estándares de la industria.

**IL7.** Ejecuta pruebas unitarias en microservicios desarrollados utilizando JUnit y herramientas actuales, garantizando la calidad y funcionalidad del código.

**IL8.** Realiza análisis de los resultados de las pruebas unitarias en microservicios para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

# **Conceptos relevantes**

<table>
  <tr>
   <td>

JUnit

</td>
   <td>

Análisis de resultados

</td>
   <td>

Métricas

</td>
  </tr>
  <tr>
   <td>

Reportes

</td>
   <td>

Fallos

</td>
   <td>

Requerimientos

</td>
  </tr>
  <tr>
   <td>

Calidad

</td>
   <td>

Refactorización

</td>
   <td>

Mejora continua

</td>
  </tr>
</table>

 **\**


# **Preguntas activadoras**

* ¿De qué manera el análisis de los reportes generados por JUnit te permite identificar problemas de calidad en el código y priorizar su resolución?

* ¿Cómo influye la interpretación de las métricas de prueba (tiempo de ejecución, porcentaje de éxito, cobertura) en la toma de decisiones para mejorar la calidad del servicio?

* ¿De qué forma reconocer patrones de fallos en las pruebas unitarias puede orientarte hacia una refactorización más efectiva de módulos específicos en el entorno de microservicios?

* ¿Cómo integrarías el análisis continuo de los resultados de las pruebas unitarias en tu flujo de desarrollo para asegurar una mejora constante en la calidad del producto?

# **Actividad**

En esta sexta semana, realizarás una actividad sumativa grupal (2 a 3 integrantes), llamada &quot;Integrando, aplicando y analizando resultados de pruebas unitarias en microservicios con JUnit&quot;, donde deberás integrar un entorno de pruebas unitarias en microservicios utilizando JUnit, garantizando la cobertura y automatización.

 **\**


# **Reforcemos conceptos previos antes de JUnit**

Antes de profundizar en el uso de JUnit y su aplicación en microservicios, es importante repasar algunos conceptos clave que servirán como base para entender y aprovechar mejor las herramientas y estrategias que se abordarán en esta guía. Estos conceptos conectan directamente con los pasos que realizaremos más adelante y ayudan a garantizar una transición fluida hacia el uso práctico de JUnit.



## **JUnit: definición de Pruebas unitarias y su rol en microservicios**

### **¿Qué son las Pruebas unitarias?**

Las pruebas unitarias son un tipo de verificación enfocada en evaluar la funcionalidad de las partes más pequeñas y aisladas del código (por lo general, una función o método específico). Su principal propósito es asegurar que cada unidad de software cumpla con los requerimientos esperados de manera independiente. De esta forma, se detectan errores desde etapas tempranas del desarrollo, lo que reduce costos y esfuerzo de corrección en fases posteriores. \


**Beneficios de las Pruebas unitarias en el Ciclo de desarrollo**

* **Detección temprana de errores:** localizar problemas en fases tempranas facilita su corrección con menor impacto en tiempo y recursos.

* **Fomento de la confianza en el código:** contar con una batería sólida de pruebas unitarias permite realizar cambios y refactorizaciones con la certeza de no introducir errores de forma inadvertida.

* **Documentación viva:** las pruebas unitarias sirven como una especificación técnica, ya que describen la funcionalidad esperada del código y pueden utilizarse como referencia para nuevos desarrolladores.

**El rol de las pruebas unitarias en la arquitectura de microservicios**

Los microservicios dividen la aplicación en componentes independientes, cada uno con su propia lógica de negocio y recursos. Este paradigma favorece la escalabilidad, el despliegue continuo y la resiliencia. Sin embargo, también requiere un mayor esfuerzo de pruebas. En este contexto, las pruebas unitarias resultan cruciales para:

* **Asegurar integridad interna:** antes de integrar un microservicio con otros componentes, es fundamental verificar que cada pieza cumpla su cometido de manera aislada.

* **Promover la independencia:** al garantizar el correcto funcionamiento de cada microservicio individualmente, se facilita la identificación de errores y su corrección sin afectar el sistema en su totalidad.

* **Facilitar la escalabilidad:** una arquitectura de microservicios acompañada de un conjunto sólido de pruebas unitarias permite agregar o modificar servicios con mayor agilidad, reduciendo el riesgo de generar regresiones. \


## **Configuración básica de JUnit**



Como ya sabemos, JUnit es un framework de pruebas unitarias para Java que entrega una forma estructurada de crear, ejecutar y reportar pruebas, permitiendo a los desarrolladores verificar el comportamiento del código de forma automatizada. Las principales ventajas de JUnit incluyen:

* **Simplicidad de uso**: cuenta con anotaciones e infraestructura sencillas, lo que facilita su implementación en proyectos existentes.

* **Integración con herramientas de desarrollo**: JUnit se integra fácilmente con IDEs (como IntelliJ, Eclipse) y servidores de integración continua (como Jenkins o GitHub Actions), favoreciendo un flujo de desarrollo continuo.

* **Reportes claros y métricas de cobertura**: su compatibilidad con herramientas de reporte y análisis permite visualizar resultados y métricas, indispensables para el posterior análisis de la calidad del software.

Ahora que hemos revisado rápidamente sus ventajas, podemos comenzar a utilizar JUnit en un proyecto Java. Para hacerlo, sigue los siguientes pasos:  **\**


**Paso 1 - Dependencia:** incluir la dependencia de JUnit (por ejemplo, JUnit 5) en el archivo de configuración del proyecto (pom.xml en Maven, build.gradle en Gradle).

**Figura 1**

*Dependencia JUnit 4*



*Nota.* Configuración de Maven para usar JUnit. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

**Paso 2 - Estructura de pruebas:** ubicar las clases de prueba en un directorio dedicado (generalmente src/test/java).

**Figura 2**

*Estructura de pruebas en el proyecto*



*Nota.* Organización de pruebas unitarias en el paquete com.test.test.microservices. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 **\**


**Paso 3 - Creación de casos de prueba:** cada caso de prueba se define como un método anotado con @Test. Dentro de ese método se implementan aserciones para validar el comportamiento esperado.

**Figura 3**

*Ejemplo de Test para un cálculo del total en un pedido*



*Nota.* Verifica que el total del pedido coincida con la suma de los precios de los ítems añadidos. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 **\**


**Paso 4 - Ejecución:** las pruebas se ejecutan directamente desde el IDE o mediante comandos de construcción (ej: mvn test o gradle test). Al final, JUnit presenta un reporte indicando qué pruebas pasaron, cuáles fallaron y por qué.

**Figura 4**

*Ejecución de pruebas y compilación*



*Nota.* Todas las pruebas se ejecutaron con éxito, sin fallos ni errores, y la compilación fue exitosa. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

Una vez que hayas ejecutado tu prueba, es momento de analizar los resultados entregados por JUnit, lo que veremos a continuación.

 **\**


## **Generación y lectura de resultados con JUnit**



Una vez que las pruebas han sido ejecutadas, JUnit puede entregar los resultados de las pruebas en distintos formatos, tales como texto plano, HTML o XML, dependiendo de la configuración empleada. Sin embargo, más allá de las diferencias en los formatos, los resultados incluidos en los reportes consideran:

* **Listado de pruebas exitosas y fallidas:** permite identificar rápidamente qué partes del código presentan problemas.

* **Mensajes de error y stack traces:** ofrecen pistas sobre la causa raíz del fallo, acelerando la localización del error.

* **Métricas adicionales (con herramientas externas):** integrando JUnit con herramientas de cobertura como JaCoCo, por ejemplo, es posible visualizar el porcentaje de líneas cubiertas, ayudando a evaluar la eficacia de las pruebas y la necesidad de agregar más casos.

Los conceptos que hemos revisado en la sección sientan las bases para comprender y analizar los resultados de las pruebas unitarias en microservicios. Dominar estos fundamentos, desde la definición de pruebas unitarias hasta la configuración básica de JUnit y la interpretación de reportes iniciales, es un paso esencial para avanzar hacia el análisis profundo de resultados y la mejora continua de la calidad del software. 

 **\**


# **Análisis de resultados de pruebas unitarias**

Ahora que hemos repasado los conceptos fundamentales relacionados con las pruebas unitarias —su importancia, uso de herramientas como JUnit y la relevancia de cubrir los principales escenarios— daremos el siguiente paso para centrarnos específicamente en la necesidad de realizar análisis de los resultados de las pruebas unitarias en microservicios para verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo.

En esta nueva sección, profundizaremos en:

* **Cómo interpretar reportes y métricas** que arrojan las pruebas unitarias,

* **Identificación de patrones de fallos** para distinguir entre defectos reales y problemas en las propias pruebas, y

* **Estrategias de mejora continua**, como refactorización y ajustes en el set de pruebas, que se derivan de dicho análisis.

De esta manera, pasamos de una visión teórica de las pruebas unitarias a un enfoque práctico de **evaluación y refinamiento** del código, y sentamos las bases para implementar un ciclo de mejora continua que asegure la calidad y confiabilidad de cada microservicio. \


## **Interpretación de reportes de pruebas**

Los reportes generados tras la ejecución de pruebas unitarias muestran el estado general del software, presentando información sobre el total de pruebas realizadas, resultado (aprobadas o fallidas), errores detectados y tiempo de ejecución. Estos reportes se generan de forma automática y suelen estar disponibles en formatos legibles y para herramientas de integración continua.

**Figura 5**

*Test en consola*



*Nota.* Todas las pruebas se ejecutaron con éxito, sin fallos ni errores, y la compilación fue exitosa. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

### **Elementos clave de los reportes**

* Cantidad total de pruebas ejecutadas.

* Número de pruebas exitosas y fallidas.

* Mensajes de error y *stack traces* asociados a cada fallo.

* Tiempo total y por prueba.

* Enlaces o secciones detalladas para profundizar en el análisis de cada caso.  **\**


**Tabla 1**

*Ejemplo de información en un reporte*

<table>
  <tr>
   <td>

**Elemento**

</td>
   <td>

**Descripción**

</td>
   <td>

**Ejemplo**

</td>
  </tr>
  <tr>
   <td>

Pruebas totales

</td>
   <td>

Cantidad de casos ejecutados

</td>
   <td>

30 pruebas en total

</td>
  </tr>
  <tr>
   <td>

Exitosas

</td>
   <td>

Casos sin fallas

</td>
   <td>

28 aprobadas

</td>
  </tr>
  <tr>
   <td>

Fallidas

</td>
   <td>

Casos con error

</td>
   <td>

2 con AssertionError

</td>
  </tr>
  <tr>
   <td>

Mensajes de error

</td>
   <td>

Detalles del fallo

</td>
   <td>

expected &lt;100&gt; but was &lt;90&gt;

</td>
  </tr>
  <tr>
   <td>

Tiempo de ejecución

</td>
   <td>

Duración de cada prueba

</td>
   <td>

testCalcularTotal (5ms)

</td>
  </tr>
</table>

![](image12.png)

 \


## **Métricas y calidad de las pruebas**



### **Cobertura de código**

La cobertura indica qué porcentaje del código es ejercitado por las pruebas. Una cobertura alta sugiere mayor probabilidad de detectar defectos, aunque no garantiza calidad absoluta. De igual forma, una cobertura baja señala áreas del código sin validar, potencialmente riesgosas.

### **Número de pruebas superadas y fallidas**

La relación entre pruebas aprobadas y fallidas aporta una visión general de la estabilidad del código. Un alto número de fallos puede reflejar problemas lógicos, mala configuración de pruebas o casos no contemplados en el diseño del software.

### **Tiempo de ejecución de las pruebas**

Pruebas excesivamente lentas pueden indicar ineficiencias en el código, dependencias externas no simuladas o diseños que no favorecen el aislamiento de componentes. Reducir el tiempo de ejecución optimiza el flujo de desarrollo e integración continua.

### **Inestabilidad de las pruebas (test flakiness)**

Las pruebas inestables, que en ocasiones son exitosas y en otras fallan sin cambios en el código, generan desconfianza en el suite de pruebas. Esta situación suele deberse a factores externos, entornos no deterministas, datos variables y/o dependencias mal simuladas. **\**


**Tabla 2**

*Tabla de métricas y acciones sugeridas*

<table>
  <tr>
   <td>

**Métrica**

</td>
   <td>

**Descripción**

</td>
   <td>

**Acción Potencial**

</td>
  </tr>
  <tr>
   <td>

Cobertura 80%

</td>
   <td>

80% del código se ejercita en pruebas

</td>
   <td>

Añadir pruebas para casos no cubiertos

</td>
  </tr>
  <tr>
   <td>

2 fallos en 30 Pruebas

</td>
   <td>

Falta de robustez en ciertas funciones

</td>
   <td>

Revisión del módulo fallido o la prueba

</td>
  </tr>
  <tr>
   <td>

Tiempo total 300ms

</td>
   <td>

Pruebas lentas señalan ineficiencias

</td>
   <td>

Refactorizar para mejorar rendimiento

</td>
  </tr>
  <tr>
   <td>

Prueba inestable

</td>
   <td>

Resultado impredecible

</td>
   <td>

Asegurar datos deterministas y entornos aislados

</td>
  </tr>
</table>

## **Identificación de Patrones de fallos**

### **Patrones recurrentes**

Si una misma prueba o un conjunto de pruebas relacionadas fallan sistemáticamente, existe un patrón de fallo. Este patrón puede apuntar a defectos crónicos en el código o a un mal diseño de las pruebas.

### **Falsos positivos y falsos negativos**

* **Falso positivo:** la prueba indica un error, pero el código está correcto. Esto suele ocurrir cuando las condiciones de prueba están mal definidas o las aserciones no reflejan el requerimiento real.

* **Falso negativo:** la prueba no detecta un error que sí existe en el código. Esto ocurre cuando las pruebas no cubren adecuadamente el caso fallido o las aserciones son insuficientes.

 * \


**Tabla 3**

*Falso positivo y falso negativo*

<table>
  <tr>
   <td>

**Tipo de error**

</td>
   <td>

**Escenario de ejemplo**

</td>
   <td>

**Causa probable**

</td>
   <td>

**Cómo solucionarlo**

</td>
  </tr>
  <tr>
   <td>

Falso Positivo

</td>
   <td>

La prueba indica que esMayorDeEdad(17) debe retornar false, pero curiosamente la prueba marca “error” al afirmar que debería ser true.

</td>
   <td>

**Aserción mal planteada:** el test espera true cuando el requerimiento real es false (la persona tiene 17 años).

</td>
   <td>

Corregir la aserción para que coincida con la lógica de negocio real (17 años -&gt; menor de edad).

</td>
  </tr>
  <tr>
   <td>

</td>
   <td>

</td>
   <td>

Datos de prueba incorrectos o mal interpretados.

</td>
   <td>

Ajustar la documentación o los datos usados en la prueba.

</td>
  </tr>
  <tr>
   <td>

Falso Negativo

</td>
   <td>

La prueba pasa exitosamente cuando esMayorDeEdad(20) retorna false, sin detectar que la lógica está mal implementada y en realidad debería retornar true.

</td>
   <td>

**Condición insuficiente:** la prueba no contempla adecuadamente que 20 años es, por definición, mayor de edad.

</td>
   <td>

Revisar la lógica del método y las condiciones de la prueba.

</td>
  </tr>
  <tr>
   <td>

</td>
   <td>

</td>
   <td>

Aserciones incompletas o lógica demasiado laxa.

</td>
   <td>

Asegurar que el test verifique correctamente los límites y valores esperados (≥ 18 años).

</td>
  </tr>
</table>

 \


### **Distinción entre defecto real y problema en la prueba**

Para diferenciar si el origen del fallo está en el código o en la propia prueba, se debe analizar el mensaje de error, el *stack trace* y las condiciones del test. Ajustar datos de entrada, revisar aserciones o aislar dependencias puede ayudar a aclarar si se trata de un defecto funcional o de una falla en el escenario de prueba.

**Tabla 4**

*Patrones de fallo*

<table>
  <tr>
   <td>

**Tipo de fallo**

</td>
   <td>

**Descripción**

</td>
   <td>

**Ejemplo**

</td>
   <td>

**Acción**

</td>
  </tr>
  <tr>
   <td>

Defecto real del código

</td>
   <td>

El método no cumple lo esperado

</td>
   <td>

calcTotal() retorna 90 en vez de 100

</td>
   <td>

Refactorizar la lógica

</td>
  </tr>
  <tr>
   <td>

Problema en la prueba

</td>
   <td>

Las aserciones no reflejan el requerimiento

</td>
   <td>

Aserción espera 100, requerimiento es 90

</td>
   <td>

Ajustar la prueba acorde al requerimiento

</td>
  </tr>
  <tr>
   <td>

Falso Positivo

</td>
   <td>

Error reportado sin razón

</td>
   <td>

Aserción incorrecta provoca fallo

</td>
   <td>

Corregir las aserciones

</td>
  </tr>
  <tr>
   <td>

Falso Negativo

</td>
   <td>

No se detecta un error existente

</td>
   <td>

Prueba no contempla caso límite

</td>
   <td>

Agregar casos faltantes

</td>
  </tr>
</table>

La interpretación de reportes, el análisis de métricas y la identificación de patrones de fallo permiten comprender a fondo el desempeño del software que estemos probando. Esto facilita la toma de decisiones informadas para mejorar la calidad del código, asegurar el cumplimiento de los requerimientos y avanzar hacia la creación de pruebas unitarias que permitan asegurar la calidad del desarrollo acorde a los requerimientos y estándares de la industria, así como también realizar un análisis que permita verificar el cumplimiento de los requerimientos y mejorar la calidad del desarrollo. 

 \


# **Estrategias para mejorar la calidad a partir del análisis**



Una vez que hayas realizado el análisis de los resultados de las pruebas unitarias, el siguiente paso es aplicar acciones concretas para elevar la calidad del desarrollo. Las estrategias que presentamos a continuación son tres: 

* Refactorización orientada a pruebas.

* Ajuste y mejora de casos de prueba.

* Integración Continua (CI) y Monitoreo Permanente.

Cada una de estas estrategias parten de la información obtenida del análisis previo, transformándola en ajustes al código, a las pruebas y a la infraestructura de integración continua.

## **Refactorización orientada por pruebas**

Consiste en realizar mejoras al código basadas en la información obtenida del análisis de resultados de las pruebas. Estas mejoras no alteran la funcionalidad final, sino que buscan optimizar la estructura interna, la claridad y la mantenibilidad del software. Dado que las pruebas unitarias ofrecen retroalimentación constante, la refactorización se guía por datos objetivos sobre calidad y rendimiento.  \


En este punto, puedes utilizar la metodología Test Driven Development (TDD), que se caracteriza porque en lugar de escribir códigos y probar su funcionamiento, se escriben primero las pruebas y luego se desarrolla el código.

Para ello, se sigue un ciclo iterativo al que se conoce como **rojo-verde-refactorizar** o r**ed green refactor.** El primer paso es escribir la prueba que define una funcionalidad o un comportamiento deseado. Después, se implementa el código que se necesita para pasar la prueba. En último lugar, se refactoriza dicho código para mejorar su calidad o estructura sin cambiar su comportamiento.    

**Figura 6**

*Ciclo TDD: Rojo - Verde - Refactorizar*



*Nota.* La imagen representa el ciclo del Desarrollo Dirigido por Pruebas (TDD), que consta de tres fases: Rojo, Verde y Refactorizar. Rocabado, M. (2018*). Introducción al Desarrollo Guiado por Pruebas (TDD).* [https://mindwaresrl.com/2015/10/18/introduccion-al-desarrollo-guiado-por-pruebas-tdd/](https://mindwaresrl.com/2015/10/18/introduccion-al-desarrollo-guiado-por-pruebas-tdd/)

 \


La ventaja que ofrece el TDD frente a otros enfoques, es que el código se prueba desde el comienzo. Por tanto, si hay errores, se pueden identificar con rapidez para corregirlos.  

### **Acciones clave en la refactorización**

* **Eliminar código redundante**: remover funciones o variables duplicadas para reducir complejidad.

* **Simplificar lógica compleja**: dividir métodos muy extensos en otros más pequeños y legibles.

* **Mejorar nomenclatura:** usar nombres descriptivos para métodos, clases y variables.

* **Adoptar buenas prácticas de diseño (SOLID):** asegurar que la arquitectura resulte flexible y escalable.

**Tabla 5**

*Posibles problemas identificados*

<table>
  <tr>
   <td>

**Problema detectado**

</td>
   <td>

**Acción de refactorización**

</td>
   <td>

**Resultado esperado**

</td>
  </tr>
  <tr>
   <td>

**Lógica repetida en 3 clases**

</td>
   <td>

Extraer la lógica a un método común

</td>
   <td>

Menor duplicación y mantenimiento

</td>
  </tr>
  <tr>
   <td>

**Métodos con más de 100 líneas**

</td>
   <td>

Dividir en sub-métodos lógicos

</td>
   <td>

Mayor legibilidad y simplicidad

</td>
  </tr>
  <tr>
   <td>

**Variables con nombres genéricos**

</td>
   <td>

Renombrar variables según su función

</td>
   <td>

Código más claro e intuitivo

</td>
  </tr>
</table>

 \


## **Ajuste y mejora de casos de prueba**

No solo el código productivo se beneficia del análisis, también las propias pruebas. Ajustar y mejorar las pruebas garantiza que reflejen adecuadamente los requerimientos, cubran escenarios omitidos, eliminen redundancias y mantengan un equilibrio entre cantidad y calidad.

### **Acciones clave para mejorar las pruebas**

* **Agregar escenarios omitidos:** incluir casos límite, valores nulos, datos extremos.

* **Eliminar redundancias:** quitar pruebas duplicadas o que no aporten información nueva.

* **Clarificar aserciones:** utilizar aserciones descriptivas para facilitar la interpretación de fallos.

* **Revisar Mocks/Stubs:** asegurar que las dependencias simuladas se ajusten al comportamiento real esperado.

**Tabla 6**

*Posibles mejoras identificadas*

<table>
  <tr>
   <td>

**Problema en las pruebas**

</td>
   <td>

**Acción de mejora**

</td>
   <td>

**Resultado esperado**

</td>
  </tr>
  <tr>
   <td>

**Falta de pruebas con datos nulos**

</td>
   <td>

Agregar casos de entrada nula

</td>
   <td>

Detección temprana de fallos

</td>
  </tr>
  <tr>
   <td>

**2 pruebas idénticas**

</td>
   <td>

Eliminar una prueba redundante

</td>
   <td>

Menor tiempo de ejecución

</td>
  </tr>
  <tr>
   <td>

**Aserciones poco específicas**

</td>
   <td>

Usar aserciones claras (ej. assertEquals) con mensajes descriptivos

</td>
   <td>

Mayor facilidad de diagnóstico

</td>
  </tr>
</table>

 \


## **Integración Continua (CI) y Monitoreo Permanente**

La integración continua permite ejecutar automáticamente las pruebas con cada cambio en el código. Esto habilita un monitoreo constante, detección temprana de regresiones y una respuesta rápida a los problemas. De esta manera, el análisis de resultados no se limita a un evento aislado, sino que se transforma en un proceso continuo.

### **Buenas prácticas en CI**

* **Ejecución automática de pruebas:** configurar pipelines que corran las pruebas en cada commit.

* **Análisis de cobertura y código estático**: integrar herramientas para medir cobertura (JaCoCo) y calidad (SonarQube).

* **Notificaciones tempranas:** activar alertas en canales de mensajería cuando las pruebas fallan.

* **Paneles de control (Dashboards):** visualizar métricas de calidad en tiempo real.

**Tabla 7**

*Herramientas y sus funciones*

<table>
  <tr>
   <td>

**Herramienta**

</td>
   <td>

**Función**

</td>
   <td>

**Beneficio**

</td>
  </tr>
  <tr>
   <td>

**Jenkins/GitHub Actions**

</td>
   <td>

Pipeline de CI para ejecutar pruebas

</td>
   <td>

Detección inmediata de fallos

</td>
  </tr>
  <tr>
   <td>

**JaCoCo**

</td>
   <td>

Reporte de cobertura de código

</td>
   <td>

Identifica áreas no testeadas

</td>
  </tr>
  <tr>
   <td>

**SonarQube**

</td>
   <td>

Análisis estático y calidad continua

</td>
   <td>

Mejora integral del código

</td>
  </tr>
</table>

 \


# **Ejemplo práctico**

Siguiendo la dinámica de las semanas anteriores, crearemos un microservicio de ejemplo y le agregaremos las pruebas unitarias con JUnit. Para ello, en el siguiente enlace encontrarás lo necesario para llevar a cabo este ejercicio: [https://ava.duoc.cl/bbcswebdav/xid-5381910_1](https://ava.duoc.cl/bbcswebdav/xid-5381910_1)

En particular, pondremos el foco en un microservicio sencillo (por ejemplo, un microservicio de “Productos”), que expone un servicio REST para administrar el listado de productos en una base de datos simulada. 

* Estructura de directorios (ejemplo con Gradle o Maven).

* Dependencias de Junit.

* Asegúrate de tener la dependencia de JUnit (versión 5 recomendada) en tu pom.xml (Maven) o build.gradle (Gradle).

* Si usas Spring Boot, también tendrás las dependencias de spring-boot-starter-test que incluyen JUnit.

 \


## **Paso a paso del ejemplo**

### **Paso 1: Crea la entidad Product**

En la carpeta “entity” definimos una clase Product, que representará al producto dentro de nuestro microservicio:

**Figura 7**

*Definición de clase Product*



*Nota.* Creación del entity Product con su constructor JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) 

  \


### **Paso 2: Crea la clase de servicio ProductService**

Aquí implementaremos la lógica principal (muy básica en este ejemplo) para administrar nuestra lista de productos:

**Figura 8**

*Creación de Service ProductService*



*Nota.* Service de product y sus métodos. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4)*.* [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows) 

 

 \


### **Paso 3: Crea el controlador ProductController**

En un proyecto con Spring Boot, el controlador podría lucir así: 

**Figura 9**

*Creación del Controller ProductController*



*Nota.* RestController y sus métodos Get y Post. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

 \


![](image19.png)![](image20.png)

**Importante**

Para propósitos de la demostración, estamos omitiendo aspectos como persistencia real en base de datos y configuración de Spring. El objetivo es centrarnos en la lógica de negocio y su prueba unitaria.

 

Ahora, revisemos a partir de lo aprendido con el ejemplo, como se realizaron:

* La creación de pruebas unitarias,

* La ejecución de pruebas unitarias, y

* El análisis de los resultados.

## **Creación de Pruebas unitarias**

Para diseñar nuestras pruebas unitarias, partimos analizando los métodos del servicio y definiendo qué se espera como resultado correcto. De este modo, cubrimos los diferentes escenarios y validamos si el servicio funciona correctamente bajo distintos supuestos.

 \


* **Clase de Test: ProductServiceTest**

En la carpeta test, creamos la clase de prueba correspondiente:

**Figura 10**

*Creación del Test de entity Product*



*Nota.* Test de product y sus distintas formas de métodos de prueba. JetBrains. (2024). *IntelliJ IDEA.* (2024.2.4). [Software]. [https://www.jetbrains.com/idea/download/?section=windows](https://www.jetbrains.com/idea/download/?section=windows)

En cada método de prueba se diseña un escenario para verificar el correcto funcionamiento de la capa de servicio. Así diseñamos pruebas unitarias contemplando diferentes condiciones (producto existente, producto inexistente, lista vacía, etc.).  \


## **Ejecución de Pruebas unitarias**

Para ejecutar las pruebas unitarias, podemos utilizar distintas herramientas:

* IDE: IntelliJ IDEA, Eclipse o VSCode, simplemente dando clic derecho en la clase de prueba y seleccionando “Run Test”.

* Terminal (Maven): mvn test

* Terminal (Gradle): gradle test

Esto generará un reporte del resultado de cada test, indicando cuántas pruebas se ejecutaron, cuántas pasaron y cuántas fallaron (si corresponde).

**![](image19.png)****![](image20.png)**

**Importante**

Asegúrate de tener configurado tu proyecto con las dependencias de prueba adecuadas (JUnit 5, por ejemplo) para que la ejecución sea exitosa.

## **Análisis de resultados**

Una vez ejecutadas las pruebas, revisamos:

* **Cantidades de pruebas exitosas y fallidas:** si alguna prueba falla, revisamos la causa en el log o reporte generado (puede estar relacionado con la lógica de negocio o con supuestos incorrectos en la prueba).

* **Cobertura de pruebas (opcionalmente):** herramientas de cobertura como JaCoCo pueden ayudarnos a revisar qué partes del código se están probando.

* **Mejoras al servicio:** si detectamos fallas lógicas en la capa de negocio, corregimos la lógica y volvemos a ejecutar los tests hasta que pasen satisfactoriamente. \


# **![](image22.png)**Cierre de la semana**

A lo largo de esta semana, hemos explorado en profundidad el análisis de los resultados de las pruebas unitarias en entornos de microservicios, comprendiendo cómo esta etapa es clave para mejorar de forma continua la calidad del desarrollo. 

Iniciamos con la interpretación de reportes y métricas, lo que nos permitió identificar fallos, evaluar el grado de cobertura y detectar patrones que señalaban áreas de mejora tanto en el código como en las propias pruebas. Posteriormente, abordamos estrategias concretas: refactorización guiada por los hallazgos, ajuste y optimización de los casos de prueba y la incorporación de prácticas de integración continua para monitorear permanentemente la calidad.

Al extraer información valiosa de cada ejecución, se adquiere la capacidad de reaccionar con agilidad ante problemas, proponer acciones correctivas informadas y mantener el producto alineado con los estándares de la industria. De este modo, las pruebas unitarias, combinadas con su análisis y mejora continua, se consolidan como un pilar esencial para garantizar la confiabilidad, mantenibilidad y evolución constante del software.

 # **Referencias**

* Bechtold, S., Brannen, S., Link, J., Merdes, M., Philipp, M., de Rancourt, J., &amp; Stein, C. (s.f.). *jUnit 5 User Guide.* [https://junit.org/junit5/docs/current/user-guide/](https://junit.org/junit5/docs/current/user-guide/)

* Mockito. (s.f.). *Mockito.* [https://site.mockito.org/](https://site.mockito.org/) \


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

Verificación de la funcionalidad de la unidad más pequeña del código (generalmente una función o método) para asegurar que opere según lo esperado.

</td>
  </tr>
  <tr>
   <td>

**JUnit**

</td>
   <td>

Framework estándar en Java para la creación, ejecución y reporte de pruebas unitarias, facilitando la verificación automática de la lógica del código.

</td>
  </tr>
  <tr>
   <td>

**Microservicio**

</td>
   <td>

Componente independiente de una arquitectura distribuida, responsable de una función específica, que puede ser desarrollado, probado y desplegado por separado.

</td>
  </tr>
  <tr>
   <td>

**Resultado de Aprendizaje (RA)**

</td>
   <td>

Declaración de lo que el estudiante debe ser capaz de hacer al finalizar una experiencia de aprendizaje, orientando el diseño de contenidos y evaluaciones.

</td>
  </tr>
  <tr>
   <td>

**Indicador de Logro (IL)**

</td>
   <td>

Evidencia concreta que permite medir el grado de logro de un resultado de aprendizaje, mostrando el avance o cumplimiento de un objetivo formativo.

</td>
  </tr>
  <tr>
   <td>

**Cobertura de Código**

</td>
   <td>

Métrica que indica el porcentaje del código ejercitado por las pruebas, evidenciando partes no testeadas que podrían ocultar defectos.

</td>
  </tr>
  <tr>
   <td>

**Asersión (Assertion)**

</td>
   <td>

Condición utilizada dentro de una prueba unitaria para verificar que un resultado obtenido coincide con el resultado esperado.

</td>
  </tr>
  <tr>
   <td>

**Test Flakiness (Inestabilidad)**

</td>
   <td>

Situación donde una prueba a veces pasa y a veces falla sin cambios en el código, dificultando la confianza en el conjunto de pruebas.

</td>
  </tr>
  <tr>
   <td>

**Refactorización**

</td>
   <td>

Proceso de mejorar la estructura interna del código sin modificar su comportamiento externo, incrementando su mantenibilidad y legibilidad.

</td>
  </tr>
  <tr>
   <td>

**Integración Continua (CI)**

</td>
   <td>

Práctica de ejecutar automáticamente las pruebas y análisis de calidad del código con cada modificación, detectando problemas de forma temprana.

</td>
  </tr>
  <tr>
   <td>

**Pipeline de CI**

</td>
   <td>

Secuencia automatizada de pasos (construcción, pruebas, análisis, despliegue) que permite un monitoreo permanente de la calidad y funcionalidad del software.

</td>
  </tr>
  <tr>
   <td>

**Mock**

</td>
   <td>

Objeto simulado que imita el comportamiento de una dependencia externa, facilitando la ejecución de pruebas unitarias aisladas.

</td>
  </tr>
  <tr>
   <td>

**Stub**

</td>
   <td>

Tipo de objeto simulado más simple que un mock, proporcionando datos predefinidos para las pruebas, sin lógica compleja.

</td>
  </tr>
  <tr>
   <td>

**Informe de Pruebas (Reporte)**

</td>
   <td>

Documento generado automáticamente tras la ejecución de las pruebas, detallando casos exitosos, fallidos, tiempo y mensajes de error.

</td>
  </tr>
  <tr>
   <td>

**Métrica**

</td>
   <td>

Valor cuantitativo que permite medir aspectos específicos de la calidad y rendimiento del código y las pruebas (ej. cobertura, tiempo de ejecución).

</td>
  </tr>
  <tr>
   <td>

**Patrones de Fallos**

</td>
   <td>

Tendencias o repeticiones en los errores detectados por las pruebas, que orientan a la identificación de problemas crónicos en código o pruebas.

</td>
  </tr>
  <tr>
   <td>

**Falso Positivo**

</td>
   <td>

Situación en que una prueba señala un error inexistente en el código, comúnmente debido a condiciones de prueba incorrectas.

</td>
  </tr>
  <tr>
   <td>

**Falso Negativo**

</td>
   <td>

Caso en el que una prueba no detecta un defecto real del código, dejando pasar un error sin reportarlo.

</td>
  </tr>
  <tr>
   <td>

**Mejora Continua**

</td>
   <td>

Enfoque sistemático para refinar y optimizar el código, las pruebas y los procesos de desarrollo, asegurando una calidad creciente a lo largo del tiempo.

</td>
  </tr>
  <tr>
   <td>

**Requerimiento**

</td>
   <td>

Necesidad o condición que el software debe cumplir, definida antes del desarrollo, y validada a través de pruebas unitarias y análisis de resultados.

</td>
  </tr>
</table>

 \


# **Apuntes**

____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________



![](image23.png)

Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual. 


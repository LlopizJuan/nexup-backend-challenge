
## Descripción

La aplicación está desarrollada en **Kotlin**, utilizando **Spring Boot** para la creación de servicios REST, con **JPA/Hibernate** para la persistencia y una **base de datos H2 en memoria** .

---

## Funcionalidades Requeridas

### Supermercado

- **Registrar una venta de producto**  
  Permite registrar la venta de un producto específico, actualizando el stock disponible y registrando el monto total de la venta.

- **Obtener cantidad vendida de un producto**  
  Devuelve la cantidad total de unidades vendidas de un producto en un supermercado determinado.

- **Obtener ingresos por producto**  
  Devuelve el total de ingresos generados por la venta de un producto en un supermercado.

- **Obtener ingresos totales del supermercado**  
  Devuelve el monto total de ingresos generados por todas las ventas realizadas en un supermercado.

### Cadena de Supermercados

- **Obtener los 5 productos más vendidos**  
  Devuelve una lista con los cinco productos más vendidos considerando todas las sucursales.

- **Obtener ingresos totales de la cadena**  
  Devuelve el total de ingresos combinados de todos los supermercados.

- **Obtener el supermercado con mayores ingresos**  
  Devuelve el supermercado con el mayor monto total de ingresos, incluyendo su nombre, ID y valor acumulado.

### Opcional

- **Supermercados abiertos según horario**  
  Dado un día y hora, devuelve una lista con los supermercados que se encuentran abiertos en ese momento.

---

## Servicios REST

### SupermercadoController
| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| `POST` | `/api/supermercados/{id}/ventas` | Registra la venta de un producto en un supermercado. Recibe un body JSON con `productoId` y `cantidad`. |
| `GET` | `/api/supermercados/{id}/productos/{productoId}/cantidad-vendida` | Devuelve la cantidad vendida de un producto. |
| `GET` | `/api/supermercados/{id}/productos/{productoId}/ingresos` | Devuelve los ingresos generados por la venta de un producto. |
| `GET` | `/api/supermercados/{id}/ingresos` | Devuelve el total de ingresos del supermercado. |

### CadenaController
| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| `GET` | `/api/cadena/top5-productos` | Devuelve los 5 productos más vendidos de toda la cadena. |
| `GET` | `/api/cadena/ingresos` | Devuelve el total de ingresos de toda la cadena. |
| `GET` | `/api/cadena/mejor-supermercado` | Devuelve el supermercado con mayores ingresos. |
| `GET` | `/api/cadena/abiertos?dia={DIA}&hora={HORA}` | Devuelve los supermercados abiertos en el momento indicado. |

---

## Tecnologías Utilizadas

- **Kotlin** – Lenguaje principal.
- **Spring Boot 3.5.x** – Framework para construir la API REST.
- **Spring Data JPA / Hibernate** – Capa de persistencia y manejo de entidades.
- **H2 Database** – Base de datos en memoria para desarrollo y testing.
- **JUnit 5 + MockMvc** – Pruebas integrales de los controladores.
- **Gradle** – Herramienta de construcción y gestión de dependencias.

--- 

## Arquitectura del Proyecto

El proyecto sigue un **enfoque Clean Architecture**, separando las responsabilidades por capas:

```
ar.com.nexup.backend.challenge
│
├── api.rest.controller          # Controladores REST
│   ├── SupermercadoController
│   └── CadenaController
│
├── domain.entities              # Entidades del dominio
│   ├── Producto
│   ├── Supermercado
│   ├── Stock
│   └── Venta
│
├── domain.exception             # Excepciones personalizadas
│   ├── SupermercadoNotFoundException
│   ├── ProductoNotFoundException
│   ├── ProductoNoDisponibleException
│   └── StockInsuficienteException
│
├── infrastructure.repository     # Repositorios JPA
│   ├── SupermercadoRepository
│   ├── ProductoRepository
│   ├── StockRepository
│   └── VentaRepository
│
├── service                       # Lógica de negocio
│   └── SupermercadoService, CadenaService
│
└── test                          # Pruebas con JUnit y MockMvc
```

Se creó la entidad Venta para aplicar el principio de Responsabilidad Única (SRP), separando la lógica de registro de transacciones del manejo de supermercado, productos y stock.
---

## Enfoque Utilizado

1. **Diseño de la lógica de negocio**  
   Cada servicio encapsula la lógica específica del dominio.  
   Por ejemplo, `SupermercadoService` maneja las ventas, actualiza el stock y calcula ingresos; mientras que `CadenaService` consolida la información global de todos los supermercados.

2. **Simulación de base de datos**  
   Se utiliza una base H2 en memoria con datos cargados en `@BeforeEach` para ejecutar las pruebas sin requerir una base externa.

3. **Validación y manejo de errores**  
   Antes de ejecutar cualquier operación, se valida la existencia de los recursos.  
   En caso contrario, se lanzan excepciones personalizadas manejadas por un `@ControllerAdvice` que traduce los errores en respuestas HTTP adecuadas (404, 400, 500).

4. **Testing aislado**  
   Cada test reinicia el contexto con:
   ```kotlin
   @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
   ```
   Esto asegura una base limpia por test y resultados.

---

## Test

Los tests utilizan MockMvc para invocar los endpoints y validar:
- Códigos de estado HTTP.
- Cuerpos de respuesta.
- Mensajes de error definidos en las excepciones.

---


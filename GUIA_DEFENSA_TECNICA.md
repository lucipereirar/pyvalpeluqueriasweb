# Guía de Defensa Técnica — Proyecto Peluquería Backend
### Evaluación Sumativa 3 · DSY1103 Desarrollo FullStack 1

> Documento de estudio personal para preparar la defensa técnica individual.
> Cubre los conceptos clave del proyecto con ejemplos del propio código.

---

## Índice

1. Visión general del proyecto
2. Bloque 1 — Pruebas unitarias con JUnit 5 + Mockito (IE 3.1.2 = 7%, IE 3.1.3 = 13%)
3. Bloque 2 — Levantar y configurar los servicios (IE 3.3.7 = 10%, IE 3.3.6 = 6%)
4. Preguntas tipo defensa con respuestas modelo
5. Cheat sheet (referencia rápida)

---

## 1. Visión general del proyecto

Sistema de e-commerce de productos de belleza/peluquería con **arquitectura de microservicios** (Spring Boot + Spring Cloud).

**10 servicios en total:**

| Servicio | Puerto | Función |
|----------|--------|---------|
| eureka-server | 8761 | Registro de servicios (Service Discovery) |
| api-gateway | 8080 | Puerta de entrada y enrutamiento |
| ms-auth | 8081 | Autenticación JWT y usuarios |
| ms-productos | 8082 | Catálogo de productos |
| ms-carrito | 8083 | Carrito de compras (llama a ms-productos) |
| ms-pedidos | 8084 | Pedidos, calcula subtotal/IVA/total (llama a ms-productos) |
| ms-pago | 8085 | Procesamiento de pagos (al aprobar: notifica y crea despacho) |
| ms-despacho | 8086 | Despachos y tracking (al crear/cambiar estado: notifica) |
| ms-notificaciones | 8087 | Notificaciones por usuario |
| ms-reportes | 8089 | Analítica de ventas: consulta ms-pedidos y ms-pago (Feign), KPIs en JSON y exportación a Excel (Apache POI) |

> **Nota sobre la evolución del sistema:** originalmente había 11 servicios. Se **descartó `ms-certificacion`** (puerto 8088) por no aportar valor al negocio: emitía "certificaciones" con fecha de vencimiento y código de verificación —un dominio de diplomas/cursos— que no encaja en un e-commerce de productos de belleza y que además vivía aislado (sin integración con pedidos ni pagos). En su lugar se reforzó la integración real entre servicios (ver 2.8).

**Patrón de arquitectura por servicio (CSR):**
`controller` → `service` (interface + impl) → `repository` → `model`, con `dto`, `mapper`, `exception` y `client` (Feign) donde aplica.

---

## 2. Bloque 1 — Pruebas unitarias con JUnit 5 + Mockito

### 2.1 ¿Por qué "unitaria" y por qué mocks?

Una prueba **unitaria** valida UNA unidad de lógica (un método del `Service`) **aislada** de sus dependencias. El `ProductoServiceImpl` depende de `ProductoRepository`, que habla con MySQL. Si la prueba usara la base de datos real, sería de integración: lenta y frágil.

La solución es el **mock**: un objeto falso que simula al repositorio. Le decimos "cuando te llamen así, responde esto" y verificamos que el servicio reaccione correctamente, sin tocar la base de datos.

### 2.2 Las 3 anotaciones clave

```java
@ExtendWith(MockitoExtension.class)   // 1. Activa Mockito en JUnit 5
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;  // 2. Repositorio FALSO

    @InjectMocks
    private ProductoServiceImpl productoService;     // 3. Servicio REAL con el mock inyectado
```

| Anotación | Qué hace | Cómo se explica |
|-----------|----------|-----------------|
| `@ExtendWith(MockitoExtension.class)` | Integra Mockito con el ciclo de vida de JUnit 5 | "Le digo a JUnit que use la extensión de Mockito para gestionar mis mocks" |
| `@Mock` | Crea un doble de prueba del repositorio, sin lógica real | "Simulo el repositorio para no depender de la base de datos" |
| `@InjectMocks` | Instancia la clase real a probar e inyecta los `@Mock` | "Es mi servicio real, pero con el repositorio falso adentro" |

### 2.3 La estructura Given–When–Then

```java
@Test
void crear_Success() {
    // GIVEN (Dado) — preparo el escenario y configuro el mock
    when(productoRepository.save(any(Producto.class))).thenReturn(producto);

    // WHEN (Cuando) — ejecuto el método que pruebo
    ProductoResponseDTO response = productoService.crear(requestDTO);

    // THEN (Entonces) — verifico el resultado
    assertNotNull(response);
    assertEquals(producto.getNombre(), response.getNombre());
    verify(productoRepository, times(1)).save(any(Producto.class));
}
```

- **Given:** `when(...).thenReturn(...)` configura el mock (*stubbing*).
- **When:** llamo al método real.
- **Then:** dos tipos de comprobación:
  - `assertEquals/assertNotNull` → verifican el **valor devuelto**.
  - `verify(...)` → verifica el **comportamiento** (que se llamó al método).

### 2.4 Métodos clave de Mockito

| Método | Para qué |
|--------|----------|
| `when(x).thenReturn(y)` | "si pasa x, responde y" |
| `when(x).thenThrow(...)` | simula que la dependencia lanza excepción |
| `when(x).thenAnswer(inv -> ...)` | respuesta que depende del argumento recibido |
| `verify(mock).metodo()` | confirma que se llamó al método |
| `verify(mock, times(1))` | ...exactamente N veces |
| `verify(mock, never())` | ...que NUNCA se llamó (clave en tests de error) |
| `any()`, `anyLong()`, `anyString()` | "cualquier argumento de ese tipo" |
| `assertThrows(Exc.class, () -> ...)` | verifica que el método lanza una excepción |

### 2.5 Relacionar cada test con su regla de negocio

```java
@Test
void crear_CategoriaInvalida() {
    // GIVEN: una categoría que no existe en el enum Categoria
    requestDTO.setCategoria("INVALIDA");

    // WHEN / THEN: el servicio debe rechazarla
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> productoService.crear(requestDTO));

    assertTrue(exception.getMessage().contains("Categoría inválida"));
    verify(productoRepository, never()).save(any(Producto.class)); // NO debe guardar
}
```

**Cómo se defiende:** "Esta prueba valida la regla de que solo se aceptan categorías del enum. Verifico dos cosas: que lanza `IllegalArgumentException`, y con `verify(..., never())` que NO llegó a guardar, porque la validación corta antes."

### 2.6 Caso especial: comunicación entre microservicios (ms-pedidos)

Mockea **dos** dependencias, incluido el Feign Client (cliente REST a ms-productos).

```java
@Mock private PedidoRepository pedidoRepository;
@Mock private ProductoFeignClient productoFeignClient;  // cliente REST a ms-productos
@InjectMocks private PedidoServiceImpl pedidoService;

@Test
void testCrear_Success() {
    // GIVEN: simulo la respuesta REMOTA de ms-productos
    ProductoClientDTO productoClientDTO =
        new ProductoClientDTO(productoId, "Shampoo", new BigDecimal("10.00"), 50, true);
    when(productoFeignClient.buscarPorId(productoId)).thenReturn(productoClientDTO);
    when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
        Pedido p = inv.getArgument(0); p.setId(1L); return p;
    });

    // WHEN
    PedidoResponseDTO responseDTO = pedidoService.crear(requestDTO);

    // THEN: verifico el cálculo de subtotal, IVA (19%) y total
    assertEquals(0, new BigDecimal("20.00").compareTo(responseDTO.getSubtotal())); // 2 × 10.00
    assertEquals(0, new BigDecimal("3.80").compareTo(responseDTO.getImpuesto()));  // 19% de 20
    assertEquals(0, new BigDecimal("23.80").compareTo(responseDTO.getTotal()));    // 20 + 3.80
}
```

Puntos para la defensa:
- Mockeo el `ProductoFeignClient` porque en una prueba unitaria no quiero que llame de verdad a ms-productos; simulo qué me respondería.
- Así pruebo MI lógica de negocio (cálculo de subtotal, impuesto, total) de forma aislada.
- `thenAnswer` se usa cuando la respuesta depende del argumento recibido (aquí, asignar un ID al pedido guardado).
- Se usa `compareTo` en vez de `equals` con `BigDecimal` porque `equals` distingue `20.0` de `20.00` (escala); `compareTo` compara solo el valor.

### 2.7 Escribir una prueba NUEVA en vivo (IE 3.1.3 = 13%)

Receta de 5 pasos:

1. Anota el método con `@Test` y nombre descriptivo: `void testMetodo_Escenario()`.
2. **GIVEN** — configura los mocks con `when(...).thenReturn(...)`.
3. **WHEN** — llama al método del servicio.
4. **THEN** — `assert...` sobre el resultado y/o `verify(...)` sobre el comportamiento.
5. Ejecútalo y confirma que pasa en verde.

Ejemplo — probar `cancelar()` cuando el pedido SÍ se puede cancelar:

```java
@Test
void testCancelar_Success() {
    // GIVEN: un pedido PENDIENTE (sí se puede cancelar)
    Long id = 1L;
    Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE).build();
    when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
    when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

    // WHEN
    pedidoService.cancelar(id);

    // THEN: el estado cambia a CANCELADO y se guarda
    assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
    verify(pedidoRepository).save(pedido);
}
```

**Truco mental:** pregúntate siempre "¿qué necesita encontrar el método para funcionar?" (eso va en el Given con `when`) y "¿qué debería pasar como resultado?" (eso va en el Then con `assert`/`verify`).

### 2.8 Comunicación entre servicios: flujo de compra y analítica

Además del caso pedidos→productos, el sistema tiene comunicación Feign en el flujo de compra y en los reportes. Es material típico de defensa sobre arquitectura de microservicios.

**Flujo de compra (coreografía vía Feign):**

```
pago aprobado ──► notificación (tipo PAGO)
              └─► despacho creado ──► notificación (tipo DESPACHO)
cambio de estado del despacho ──► notificación (tipo DESPACHO)
```

- **ms-pago**, al aprobar un pago, llama por Feign a **ms-notificaciones** (avisa al usuario) y a **ms-despacho** (genera el despacho con su código de tracking).
- **ms-despacho**, al crear el despacho y al cambiar su estado (EN_CAMINO, ENTREGADO…), llama por Feign a **ms-notificaciones**.

**Punto clave de defensa — resiliencia (best-effort):** estas llamadas son *efectos secundarios* envueltos en `try/catch` y registradas con log. Si ms-notificaciones o ms-despacho están caídos, **el pago igual se confirma**; no se propaga el fallo a la operación principal. Es una decisión de diseño consciente: la transacción central (el pago) no debe depender de que un servicio secundario esté disponible.

```java
// ms-pago: PagoServiceImpl.procesar(...)
Pago guardado = pagoRepository.save(pago);
notificarPagoAprobado(guardado);   // best-effort: try/catch interno
crearDespacho(guardado, dto);      // best-effort: try/catch interno
return PagoMapper.toDTO(guardado);
```

**Cómo se testea:** en `PagoServiceImplTest` se mockean `NotificacionFeignClient` y `DespachoFeignClient`, y se verifica que al procesar un pago se dispara el flujo:

```java
@Mock private NotificacionFeignClient notificacionClient;
@Mock private DespachoFeignClient despachoClient;
// ...
verify(notificacionClient, times(1)).crear(any(NotificacionClientDTO.class));
verify(despachoClient, times(1)).crear(any(DespachoClientDTO.class));
```

**Analítica de ventas (ms-reportes):** ms-reportes consume por Feign a **ms-pedidos** (`GET /api/pedidos`) y **ms-pago** (`GET /api/pagos`) para calcular indicadores:

- `GET /api/reportes/ventas/resumen?desde=&hasta=` → KPIs en JSON: total de ventas (pagos aprobados), ticket promedio, ventas por método de pago, pedidos por estado y ranking de productos más vendidos.
- `GET /api/reportes/ventas/excel?desde=&hasta=` → el mismo reporte como archivo `.xlsx` generado con **Apache POI** (hojas de Resumen, Método de pago, Pedidos por estado y Top productos).

Esto atiende dos necesidades del negocio: el **dueño** consulta sus ventas (endpoint JSON) y el **equipo de desarrollo** entrega un reporte periódico descargable (endpoint Excel).

---

## 3. Bloque 2 — Levantar y configurar los servicios

### 3.1 Arquitectura y orden de arranque

```
   MySQL (3306)  ← cada microservicio tiene su propia base de datos
        ▲
   Eureka Server (8761)  ← REGISTRO: todos los servicios se anotan aquí
        ▲
   API Gateway (8080)  ← PUERTA DE ENTRADA: pregunta a Eureka dónde está cada servicio
        ▲
   8 Microservicios (8081–8087, 8089)
```

| Orden | Servicio | Por qué |
|-------|----------|---------|
| 1º | MySQL | Los microservicios abren conexión JPA al arrancar. Sin BD, fallan. |
| 2º | Eureka Server | Es el registro. Si no está, no hay dónde registrarse. |
| 3º | Microservicios | Se registran en Eureka y conectan a su BD. |
| 4º | API Gateway | Necesita preguntar a Eureka para enrutar. |

> El sistema es **tolerante al desorden**: si un microservicio arranca antes que Eureka, no se cae; reintenta registrarse cada 30 s. El orden recomendado solo lo hace más rápido.

### 3.2 Flujo de registro en Eureka (visto en los logs)

```
1. Setting initial instance status as: STARTING
2. Registering application MS-AUTH with eureka with status UP
3. Tomcat started on port 8081 (http)
   Started MsAuthApplication in N seconds
```

**Explicación:** el servicio se inicializa como STARTING, se registra en Eureka con su nombre (MS-AUTH) y estado UP, y Tomcat queda escuchando en su puerto. Desde ahí el Gateway ya puede enrutarle.

Verificación: `http://localhost:8761` muestra la tabla "Instances currently registered with Eureka" con los 9 servicios (8 ms + gateway). **Eureka no se lista a sí mismo** (es normal).

### 3.3 Cómo arrancar los servicios — dos formas

**Forma A — IDE / Maven (una por una):**
```bash
cd ms-auth/ms-auth
mvn spring-boot:run
```
O en IntelliJ: ▶ sobre la clase `MsAuthApplication`.

**Forma B — JAR empaquetado (lo que usa Docker):**
```bash
mvn clean package -DskipTests          # genera el .jar
java -jar target/ms-auth-1.0.0.jar     # lo ejecuta
```

**Diferencia clave:** `mvn spring-boot:run` compila y ejecuta en un paso (desarrollo). `java -jar` ejecuta el "fat JAR" ya empaquetado, que incluye Tomcat y todas las dependencias — es lo que corre dentro del contenedor Docker (`ENTRYPOINT ["java","-jar","/app.jar"]`).

### 3.4 Diagnóstico de errores (correcciones en vivo)

**Fallo 1 — Puerto ocupado:**
```
Web server failed to start. Port 8081 was already in use.
```
```powershell
Get-NetTCPConnection -LocalPort 8081 -State Listen   # ver qué lo usa
taskkill /F /IM java.exe                              # matar procesos Java
```

**Fallo 2 — MySQL caído o BD inexistente:**
```
Communications link failure          → MySQL no está corriendo
Unknown database 'peluqueria_auth'   → la BD no existe
```
```sql
CREATE DATABASE peluqueria_auth;
```
Explicación: cada microservicio tiene su propia base de datos (patrón *database-per-service*). Si falta una, solo falla ese servicio.

**Fallo 3 — Eureka no disponible:**
```
Connection refused: connect   (localhost:8761)
```
No es fatal: el servicio reintenta. Al registrar verás `registration status: 204` (204 = OK).

**Fallo 4 — Gateway devuelve 503 Service Unavailable:**
Causa: el microservicio destino aún no está registrado en Eureka.
Solución: esperar a que aparezca en `http://localhost:8761` o revisar que arrancó sin errores.

### 3.5 Checklist de verificación

```powershell
# 1. Eureka y registrados → navegador: http://localhost:8761

# 2. Un servicio directo
Invoke-WebRequest http://localhost:8082/api/productos

# 3. El Gateway enrutando (la prueba más potente)
Invoke-WebRequest http://localhost:8080/api/productos

# 4. Health de un servicio
Invoke-WebRequest http://localhost:8081/actuator/health   # → {"status":"UP"}
```

El punto 3 es el más fuerte: si el Gateway (8080) devuelve lo mismo que el servicio directo (8082), demuestras que Eureka + load balancing + enrutamiento funcionan juntos.

---

## 4. Preguntas tipo defensa con respuestas modelo

**P1. ¿Por qué arrancas Eureka antes que los microservicios?**
Porque Eureka es el registro de servicios. Los microservicios se registran en él al arrancar; si no está disponible, reintentan cada 30 s. Arrancarlo primero evita esos reintentos y acelera el arranque.

**P2. Si arranco ms-pedidos y MySQL está apagado, ¿qué pasa?**
El arranque falla porque JPA no puede abrir la conexión. En el log aparece `Communications link failure`. Se soluciona arrancando MySQL y reintentando. Solo afecta a los servicios con base de datos.

**P3. El Gateway me da 503 al pedir /api/productos. ¿Qué reviso primero?**
Que ms-productos esté registrado en Eureka (`http://localhost:8761`). El 503 significa que el Gateway recibió la petición pero no encontró una instancia viva del servicio destino.

**P4. ¿Diferencia entre `mvn spring-boot:run` y `java -jar`?**
`spring-boot:run` compila y ejecuta desde el código fuente (desarrollo). `java -jar` ejecuta el JAR ya empaquetado con Tomcat y dependencias incluidas; es lo que corre en Docker.

**P5. ¿Cómo sé que ms-auth se registró sin abrir el navegador?**
En su log busco `Registering application MS-AUTH with eureka with status UP` y `registration status: 204`.

**P6. ¿Por qué usas mocks en las pruebas?**
Para aislar la lógica del servicio de sus dependencias (repositorio, Feign Client). Así la prueba es rápida, no depende de la base de datos ni de otros servicios, y prueba solo mi lógica de negocio.

**P7. ¿Qué diferencia hay entre `assert` y `verify`?**
`assert` comprueba el valor devuelto por el método. `verify` comprueba el comportamiento: que el servicio llamó (o no) a un método de una dependencia.

**P8. ¿Qué pasa en tu sistema cuando se aprueba un pago?**
ms-pago guarda el pago como APROBADO y, por Feign, dispara dos efectos: notifica al usuario (ms-notificaciones) y crea el despacho (ms-despacho), que a su vez genera otra notificación con el código de tracking. Es una **coreografía**: cada servicio reacciona sin un orquestador central. Las llamadas son *best-effort* (envueltas en try/catch): si un servicio secundario está caído, el pago igual se confirma.

**P9. Eliminaste un microservicio, ¿por qué y cómo?**
Eliminé `ms-certificacion` porque no aportaba valor al negocio (emitía certificaciones tipo diploma, ajenas a un e-commerce, y estaba aislado). Al quitarlo actualicé todas sus referencias: módulo del `pom.xml` padre, `docker-compose.yml`, `init.sql`, scripts de arranque, la ruta del API Gateway (renumerando los índices para no dejar huecos) y el README. Tras el cambio, `mvn compile` de los 10 módulos sigue en verde.

---

## 5. Cheat sheet (referencia rápida)

**Puertos:** Eureka 8761 · Gateway 8080 · auth 8081 · productos 8082 · carrito 8083 · pedidos 8084 · pago 8085 · despacho 8086 · notificaciones 8087 · reportes 8089 · *(8088 libre: ms-certificacion descartado)*

**Arranque:** MySQL → Eureka → microservicios → Gateway

**Comandos:**
```bash
mvn clean package -DskipTests     # empaquetar
mvn spring-boot:run               # ejecutar desde fuente
java -jar target/<nombre>.jar     # ejecutar JAR
mvn test                          # correr pruebas
```

**Mockito esencial:**
```java
when(mock.metodo(arg)).thenReturn(valor);   // configurar
verify(mock).metodo(arg);                    // verificar llamada
verify(mock, never()).metodo(any());         // verificar NO llamada
assertThrows(Exc.class, () -> servicio.x()); // verificar excepción
```

**URLs útiles:**
- Eureka: `http://localhost:8761`
- Swagger (cualquier ms): `http://localhost:808X/swagger-ui/index.html`
- Gateway: `http://localhost:8080/api/<recurso>`

**Pendiente de repasar:** Bloque Swagger/OpenAPI (IE 3.2.2) y Bloque YAML + despliegue (IE 3.3.5, 3.3.6).

# Pyval Peluquerías — E-commerce Full Stack (Microservicios + Web)

Sistema de e-commerce para venta de productos de belleza y peluquería: backend de **microservicios (Spring Boot + Spring Cloud)** y **frontend web (React + Vite)** con tienda pública, checkout y panel de administración.

## Integrantes del Equipo

| Nombre | Rol |
|--------|-----|
| Luciano Alonso Pereira Rodriguez | Desarrollador Backend |
| _(Nombre del segundo integrante)_ | Desarrollador Backend |

> **Asignatura:** DSY1103 — Desarrollo FullStack 1  
> **Evaluación:** Examen Final Transversal (EFT)  
> **Repositorio:** https://github.com/lucipereirar/pyvalpeluqueriasweb (público)

---

## Descripción del Proyecto

Plataforma de comercio electrónico orientada al rubro de la belleza y peluquería. Permite a los clientes explorar un catálogo de productos, gestionar un carrito de compras, realizar pedidos y pagos, recibir notificaciones y rastrear sus despachos. El sistema cuenta con autenticación JWT y generación de reportes exportables en Excel.

---

## Microservicios Implementados

| # | Microservicio | Puerto (Local) | Descripción |
|---|--------------|----------------|-------------|
| 1 | **eureka-server** | 8761 | Servidor de descubrimiento de servicios (Service Registry) |
| 2 | **api-gateway** | 8080 | Puerta de enlace central para enrutamiento de solicitudes |
| 3 | **ms-auth** | 8081 | Autenticación y autorización con JWT. Gestión de usuarios |
| 4 | **ms-productos** | 8082 | Catálogo de productos con categorías y búsqueda por nombre |
| 5 | **ms-carrito** | 8083 | Carrito de compras activo por usuario. Consulta ms-productos |
| 6 | **ms-pedidos** | 8084 | Creación y gestión de pedidos. Calcula subtotal, IVA y total |
| 7 | **ms-pago** | 8085 | Procesamiento de pagos (TARJETA, TRANSFERENCIA, EFECTIVO) |
| 8 | **ms-despacho** | 8086 | Despacho y rastreo de envíos con código de tracking |
| 9 | **ms-notificaciones** | 8087 | Notificaciones internas por usuario con estado de lectura |
| 10 | **ms-reportes** | 8089 | Analítica de ventas: consulta ms-pedidos y ms-pago (Feign), resumen de KPIs y exportación a Excel (Apache POI) |

---

## Frontend Web (React + Vite)

Aplicación en `frontend/` que consume el API Gateway (en desarrollo, `/api` se proxea a `http://localhost:8080`).

**Storefront (público):** inicio, quiénes somos, servicios, catálogo con carrito, contacto, login/registro.
**Con sesión:** checkout (pedido + pago + datos de envío), confirmación de compra, "Mis pedidos" con seguimiento del despacho (tracking).
**Panel de administración (rol ADMIN, `/admin`):** CRUD de productos, gestión de pedidos y despachos (cambios de estado que notifican al cliente) y reportes de venta con descarga de Excel.

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (requiere el backend levantado)
npm run build      # build de producción en dist/
```

---

## Seguridad

- **JWT en el API Gateway:** el gateway valida el token emitido por `ms-auth` (secreto compartido vía `JWT_SECRET`) antes de enrutar. Rutas públicas: `/api/auth/**`, `GET /api/productos/**`, `GET /api/despachos/tracking/**` y documentación. Las rutas administrativas (gestión de catálogo, reportes, usuarios, listados globales, cambios de estado) exigen **rol ADMIN** (401/403 en caso contrario). El gateway propaga `X-User-Id`, `X-User-Email` y `X-User-Rol` a los servicios.
- **Credenciales externalizadas:** usuario/contraseña de MySQL (`DB_USERNAME`, `DB_PASSWORD`), secreto JWT (`JWT_SECRET`) y orígenes CORS (`CORS_ALLOWED_ORIGINS`) se configuran por variables de entorno, con valores por defecto solo para desarrollo local.
- **CORS** configurado en el gateway para los orígenes del frontend.

> Pendientes planificados (ver `docs/Plan_de_Accion.md`): pasarela de pago real (Webpay/Flow), facturación electrónica SII, courier con tracking automático, notificaciones email/SMS/WhatsApp y despliegue en cloud.

---

## Rutas Principales del API Gateway

Todas las solicitudes pasan por `http://localhost:8080` y son enrutadas al microservicio correspondiente mediante load balancing (Eureka).

| Prefijo de Ruta | Microservicio Destino |
|-----------------|----------------------|
| `/api/auth/**` | ms-auth |
| `/api/usuarios/**` | ms-auth |
| `/api/productos/**` | ms-productos |
| `/api/carrito/**` | ms-carrito |
| `/api/pedidos/**` | ms-pedidos |
| `/api/pagos/**` | ms-pago |
| `/api/despachos/**` | ms-despacho |
| `/api/notificaciones/**` | ms-notificaciones |
| `/api/reportes/**` | ms-reportes |

### Analítica de ventas (ms-reportes)

Endpoints que consolidan datos de ms-pedidos y ms-pago para el dueño del negocio y para entregas periódicas:

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/reportes/ventas/resumen?desde=&hasta=` | Resumen de KPIs en JSON: total de ventas, ticket promedio, ventas por método de pago, pedidos por estado y ranking de productos más vendidos. Rango de fechas opcional (`yyyy-MM-dd`). |
| `GET` | `/api/reportes/ventas/excel?desde=&hasta=` | Descarga el mismo reporte como archivo `.xlsx` (Apache POI) con hojas de Resumen, Método de pago, Pedidos por estado y Top productos. |

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su propia UI de Swagger. Acceder con el servicio levantado:

| Microservicio | URL Swagger (Local) |
|---------------|---------------------|
| ms-auth | http://localhost:8081/swagger-ui/index.html |
| ms-productos | http://localhost:8082/swagger-ui/index.html |
| ms-carrito | http://localhost:8083/swagger-ui/index.html |
| ms-pedidos | http://localhost:8084/swagger-ui/index.html |
| ms-pago | http://localhost:8085/swagger-ui/index.html |
| ms-despacho | http://localhost:8086/swagger-ui/index.html |
| ms-notificaciones | http://localhost:8087/swagger-ui/index.html |
| ms-reportes | http://localhost:8089/swagger-ui/index.html |

---

## Instrucciones de Ejecución Local (desde el IDE)

### Requisitos previos

- Java 21 (JDK)
- Maven 3.9+
- MySQL 8.0 corriendo en `localhost:3306`
- IntelliJ IDEA o VS Code

### 1. Crear las bases de datos

Ejecutar en MySQL Workbench o cliente equivalente:

```sql
CREATE DATABASE peluqueria_auth;
CREATE DATABASE peluqueria_productos;
CREATE DATABASE peluqueria_carrito;
CREATE DATABASE peluqueria_pedidos;
CREATE DATABASE peluqueria_pago;
CREATE DATABASE peluqueria_despacho;
CREATE DATABASE peluqueria_notificaciones;
CREATE DATABASE peluqueria_reportes;
```

### 2. Levantar los servicios en orden

Ejecutar cada microservicio desde su directorio o mediante el script incluido:

```powershell
# Opción rápida con scripts incluidos:
powershell -ExecutionPolicy Bypass -File .\setup_admin.ps1
powershell -ExecutionPolicy Bypass -File .\start_all_services.ps1
```

**Orden recomendado de arranque manual:**
1. `eureka-server/eureka-server` → verificar en http://localhost:8761
2. `API Gateway/API-Gateway`
3. Todos los microservicios de negocio (orden libre)

### 3. Verificar el sistema

- Eureka Dashboard: http://localhost:8761
- Gateway: http://localhost:8080/api/productos
- Swagger de cualquier servicio: http://localhost:808X/swagger-ui/index.html

---

## Instrucciones de Ejecución con Docker (Despliegue Local Containerizado)

### Requisitos previos

- Docker Desktop instalado y corriendo
- Maven 3.9+ instalado

### 1. Compilar los JARs de todos los microservicios

Desde la raíz del proyecto, compilar cada módulo:

```bash
# Compilar todos los módulos desde la raíz
mvn clean package -DskipTests
```

O compilar individualmente cada microservicio:

```bash
cd ms-auth/ms-auth && mvn clean package -DskipTests
cd ms-productos/ms-productos && mvn clean package -DskipTests
# ... repetir para cada servicio
```

### 2. Levantar el ecosistema completo con Docker Compose

```bash
# Desde la raíz del proyecto
docker-compose up --build
```

Para levantar en segundo plano:

```bash
docker-compose up --build -d
```

### 3. Verificar contenedores activos

```bash
docker-compose ps
```

### 4. Verificar el sistema

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- Swagger (via puerto directo): http://localhost:808X/swagger-ui/index.html

### 5. Detener el sistema

```bash
docker-compose down
```

Para eliminar también los volúmenes (datos):

```bash
docker-compose down -v
```

---

## Pruebas Unitarias y Cobertura

Cada microservicio incluye pruebas unitarias (JUnit 5 + Mockito) sobre su capa de servicio, con estructura *Given–When–Then* y mocks de repositorios y clientes Feign.

```bash
# Ejecutar todas las pruebas del proyecto
mvn test
```

La cobertura se mide con **JaCoCo** (configurado en el `pom.xml` padre). Al ejecutar `mvn test` se genera un reporte HTML por módulo en:

```
<microservicio>/target/site/jacoco/index.html
```

Se excluyen del cálculo las clases sin lógica de negocio (entidades, DTOs, mappers, configuración, excepciones, clientes Feign y clases `Application`), de modo que el porcentaje refleje la **capa de servicio**. La cobertura de instrucciones de la lógica de negocio supera el **80%** exigido (≈ **94%** global; cada microservicio ≥ 87%).

---

## Despliegue Remoto (Railway / Render u otra plataforma)

El sistema puede desplegarse en una plataforma cloud que soporte contenedores o JARs de Spring Boot. Pasos generales:

1. **Base de datos:** provisionar una instancia MySQL administrada y crear las 8 bases (`peluqueria_auth`, `peluqueria_productos`, …).
2. **Variables de entorno** (por servicio): `DB_USERNAME`, `DB_PASSWORD`, la URL del datasource, `JWT_SECRET` (el mismo en `ms-auth` y el Gateway) y `CORS_ALLOWED_ORIGINS` con el dominio del frontend.
3. **Eureka y Gateway:** desplegar primero el `eureka-server`; configurar `eureka.client.service-url.defaultZone` de cada servicio apuntando a la URL pública de Eureka; desplegar el `api-gateway` como punto de entrada.
4. **Microservicios:** desplegar cada uno (imagen Docker de su `Dockerfile`, o el JAR generado con `mvn package`).
5. **Frontend:** publicar `frontend/` (build de Vite) apuntando `VITE_API_URL` a la URL pública del Gateway.

> Los `Dockerfile` y el `docker-compose.yml` incluidos sirven de base tanto para el despliegue local containerizado como para la construcción de imágenes en el registro de la plataforma elegida.

---

## Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.x | Framework base |
| Spring Cloud Gateway | 4.x | API Gateway |
| Spring Cloud Netflix Eureka | 4.x | Service Discovery |
| Spring Cloud OpenFeign | 4.x | Comunicación entre servicios |
| Spring Security + JWT | — | Autenticación y autorización |
| Spring Data JPA | — | Acceso a datos |
| MySQL 8.0 | — | Base de datos relacional |
| springdoc-openapi | 2.6.0 | Documentación Swagger/OpenAPI |
| JUnit 5 + Mockito | — | Pruebas unitarias |
| JaCoCo | 0.8.12 | Cobertura de pruebas (reporte por módulo) |
| SLF4J | — | Logs estructurados y trazabilidad entre capas |
| Apache POI | — | Exportación a Excel (ms-reportes) |
| Docker | — | Containerización |
| Lombok | — | Reducción de código boilerplate |
| React + Vite | 18 / 5 | Frontend web (storefront y panel admin) |
| React Router | 6 | Enrutamiento del frontend |

---

## Documentación del Proyecto

La carpeta [`docs/`](docs/) contiene la documentación de gestión y negocio: Acta de Constitución, Propuesta de Solución, Plan de Proyecto (con Carta Gantt), Presupuesto detallado (con fuentes), Plan de Marketing, **Plan de Gestión de Riesgos**, **Plan de Pruebas** y **Plan de Acción** (incluye los pendientes que requieren trámites o pagos: pasarela real, SII, courier, WhatsApp/LLM y cloud).

---

## Arquitectura del Sistema

```
Frontend Web (React :5173)  /  Postman
        │
        ▼  (JWT validado en el gateway)
  [API Gateway :8080]
        │
        ├──► [ms-auth :8081]         ← JWT, usuarios
        ├──► [ms-productos :8082]    ← catálogo de productos
        ├──► [ms-carrito :8083]      ── llama a ms-productos (Feign)
        ├──► [ms-pedidos :8084]      ── llama a ms-productos (Feign)
        ├──► [ms-pago :8085]         ── al aprobar: notifica + crea despacho (Feign)
        ├──► [ms-despacho :8086]     ── al crear/cambiar estado: notifica (Feign)
        ├──► [ms-notificaciones :8087]
        └──► [ms-reportes :8089]     ── consulta ms-pedidos y ms-pago (Feign)

  [Eureka Server :8761] ← todos los servicios se registran aquí
  [MySQL :3306]         ← cada servicio tiene su propia BD

Flujo de compra (coreografía vía Feign):
  pago aprobado ──► notificación (PAGO)
                └─► despacho creado ──► notificación (DESPACHO)
  cambio de estado del despacho ──► notificación (DESPACHO)
```

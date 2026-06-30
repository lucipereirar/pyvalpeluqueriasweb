# Pyval Peluquerías — Backend de Microservicios

Sistema de e-commerce para venta de productos de belleza y peluquería, construido con arquitectura de microservicios usando Spring Boot y Spring Cloud.

## Integrantes del Equipo

| Nombre | Rol |
|--------|-----|
| Luciano Alonso Pereira Rodriguez | Desarrollador Backend |
| _(Nombre del segundo integrante)_ | Desarrollador Backend |

> **Asignatura:** DSY1103 — Desarrollo FullStack 1  
> **Evaluación:** Sumativa 3

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
| 10 | **ms-certificacion** | 8088 | Emisión y verificación de certificaciones de compra |
| 11 | **ms-reportes** | 8089 | Generación de reportes exportables a Excel (Apache POI) |

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
| `/api/certificaciones/**` | ms-certificacion |
| `/api/reportes/**` | ms-reportes |

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
| ms-certificacion | http://localhost:8088/swagger-ui/index.html |
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
CREATE DATABASE peluqueria_certificacion;
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
| Apache POI | — | Exportación a Excel (ms-reportes) |
| Docker | — | Containerización |
| Lombok | — | Reducción de código boilerplate |

---

## Arquitectura del Sistema

```
Cliente (Postman / Frontend)
        │
        ▼
  [API Gateway :8080]
        │
        ├──► [ms-auth :8081]         ← JWT, usuarios
        ├──► [ms-productos :8082]    ← catálogo de productos
        ├──► [ms-carrito :8083]      ── llama a ms-productos (Feign)
        ├──► [ms-pedidos :8084]      ── llama a ms-productos (Feign)
        ├──► [ms-pago :8085]
        ├──► [ms-despacho :8086]
        ├──► [ms-notificaciones :8087]
        ├──► [ms-certificacion :8088]
        └──► [ms-reportes :8089]

  [Eureka Server :8761] ← todos los servicios se registran aquí
  [MySQL :3306]         ← cada servicio tiene su propia BD
```

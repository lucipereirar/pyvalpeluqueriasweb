# Guía de Arranque Manual — Pyval

Cómo levantar todo el sistema (backend de microservicios + frontend) paso a paso en Windows.

---

## 0. Requisitos previos (una sola vez)

- **Java 21 (JDK)** — comprobar: `java -version`
- **Maven 3.9+** — comprobar: `mvn -version`
- **Node.js 18+** (para el frontend) — comprobar: `node -v`
- **MySQL 8.0** corriendo en `localhost:3306`
- Las **9 bases de datos** creadas (ver paso 1)

> El proyecto ya trae Dockerfiles y `docker-compose.yml`, pero esta guía es para el **arranque manual sin Docker**. Para el modo Docker necesitas instalar **Docker Desktop** (no basta `pip install docker`).

---

## 1. Preparar la base de datos (solo la primera vez, o si faltan BDs)

Con MySQL corriendo, ejecuta en MySQL Workbench o consola:

```sql
CREATE DATABASE IF NOT EXISTS peluqueria_auth;
CREATE DATABASE IF NOT EXISTS peluqueria_productos;
CREATE DATABASE IF NOT EXISTS peluqueria_carrito;
CREATE DATABASE IF NOT EXISTS peluqueria_pedidos;
CREATE DATABASE IF NOT EXISTS peluqueria_pago;
CREATE DATABASE IF NOT EXISTS peluqueria_despacho;
CREATE DATABASE IF NOT EXISTS peluqueria_notificaciones;
CREATE DATABASE IF NOT EXISTS peluqueria_reportes;
```

(También existe `init.sql` en la raíz con estas mismas sentencias.)

> **Credenciales de BD:** por defecto los servicios usan usuario `root` sin contraseña. Si tu MySQL tiene otra credencial, defínela antes de arrancar (ver "Variables de entorno" al final).

---

## 2. Orden de arranque

**Importante:** respeta este orden. Cada servicio se abre en su **propia ventana de terminal** y debe quedar corriendo (no cierres la ventana).

```
1º  MySQL              (ya debe estar corriendo)
2º  Eureka Server      → esperar a que esté arriba antes de seguir
3º  API Gateway
4º  Los 8 microservicios (orden libre entre ellos)
5º  Frontend
```

---

## 3. Arranque paso a paso

> Todos los comandos se ejecutan desde la raíz del proyecto:
> `C:\Users\laper\peluqueria-backend`
>
> Abre **una terminal (PowerShell o CMD) por servicio**.

### Paso 2 — Eureka Server (registro de servicios)
```powershell
cd "C:\Users\laper\peluqueria-backend\eureka-server\eureka-server"
mvn spring-boot:run
```
✅ **Espera** hasta ver `Started ...Application` y verifica en el navegador: **http://localhost:8761**
(Aún sin instancias registradas: es normal.)

### Paso 3 — API Gateway
```powershell
cd "C:\Users\laper\peluqueria-backend\API Gateway\API-Gateway"
mvn spring-boot:run
```

### Paso 4 — Microservicios (una terminal cada uno)

**ms-auth** (8081)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-auth\ms-auth"
mvn spring-boot:run
```
**ms-productos** (8082)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-productos\ms-productos"
mvn spring-boot:run
```
**ms-carrito** (8083)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-carrito\ms-carrito"
mvn spring-boot:run
```
**ms-pedidos** (8084)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-pedidos\ms-pedidos"
mvn spring-boot:run
```
**ms-pago** (8085)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-pago\ms-pago"
mvn spring-boot:run
```
**ms-despacho** (8086)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-despacho\ms-despacho"
mvn spring-boot:run
```
**ms-notificaciones** (8087)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-notificaciones\ms-notificaciones"
mvn spring-boot:run
```
**ms-reportes** (8089)
```powershell
cd "C:\Users\laper\peluqueria-backend\ms-reportes\ms-reportes"
mvn spring-boot:run
```

### Paso 5 — Frontend (React + Vite)
```powershell
cd "C:\Users\laper\peluqueria-backend\frontend"
npm install     # solo la primera vez
npm run dev
```
✅ Abre **http://localhost:5173**

---

## 4. Atajo: script que abre todo automáticamente

En vez de abrir 11 terminales a mano, existe el script `start_all_services.ps1` (levanta Eureka, Gateway y los 8 microservicios, cada uno en su ventana, con pausas entre ellos):

```powershell
cd "C:\Users\laper\peluqueria-backend"
powershell -ExecutionPolicy Bypass -File .\start_all_services.ps1
```

Luego, en otra terminal, levanta el frontend (paso 5). *(El script no incluye el frontend.)*

---

## 5. Verificar que todo levantó

1. **Eureka** → http://localhost:8761 debe listar **9 instancias** (Gateway + 8 microservicios).
2. **Catálogo público vía Gateway** → http://localhost:8080/api/productos → responde `200`.
3. **Swagger de cualquier servicio** → `http://localhost:808X/swagger-ui/index.html`
   (ej. pagos: http://localhost:8085/swagger-ui/index.html)
4. **Frontend** → http://localhost:5173

> Los microservicios tardan ~20–40 s en registrarse en Eureka. Si el Gateway devuelve `503`, espera a que el servicio destino aparezca en el dashboard de Eureka.

---

## 6. Usuarios de prueba

Puedes registrarte desde el frontend (queda como CLIENTE). Para el **panel de administración** (`/admin`) necesitas un usuario con rol **ADMIN**. El registro público siempre crea CLIENTE por seguridad; para crear un ADMIN, hazlo directamente en la BD o vía la ruta `/api/usuarios` (protegida). Los usuarios de prueba `admin.smoke@pyval.cl` y `smoke.test@pyval.cl` (si conservas esos datos) quedaron creados en pruebas anteriores.

---

## 7. Cómo DETENER todo

- En cada ventana de terminal: **Ctrl + C**.
- Si algún puerto queda ocupado (proceso Java colgado), libéralo en PowerShell:

```powershell
# Ver qué proceso usa un puerto (ej. 8080)
Get-NetTCPConnection -LocalPort 8080 -State Listen | Select-Object OwningProcess

# Matar todos los procesos Java (cuidado: cierra TODO Java)
taskkill /F /IM java.exe
```

---

## 8. Variables de entorno (opcional / producción)

Los servicios funcionan con valores por defecto para desarrollo local. Para sobrescribirlos (por ejemplo, contraseña real de MySQL o secreto JWT), define **antes de arrancar** cada servicio:

| Variable | Qué controla | Default (dev) |
|----------|--------------|---------------|
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | *(vacío)* |
| `JWT_SECRET` | Secreto para firmar/validar JWT (ms-auth y gateway deben coincidir) | valor de desarrollo |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos del frontend | `http://localhost:5173,...` |

Ejemplo en PowerShell (en la ventana del servicio, antes de `mvn spring-boot:run`):
```powershell
$env:DB_PASSWORD = "mi_password"
$env:JWT_SECRET  = "mi_secreto_base64"
mvn spring-boot:run
```

---

## Resumen de puertos

| Servicio | Puerto |
|----------|--------|
| Eureka Server | 8761 |
| API Gateway | 8080 |
| ms-auth | 8081 |
| ms-productos | 8082 |
| ms-carrito | 8083 |
| ms-pedidos | 8084 |
| ms-pago | 8085 |
| ms-despacho | 8086 |
| ms-notificaciones | 8087 |
| ms-reportes | 8089 |
| Frontend (Vite) | 5173 |

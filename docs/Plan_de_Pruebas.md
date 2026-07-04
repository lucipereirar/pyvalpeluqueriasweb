# Plan de Pruebas

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Pyval |
| **Versión del documento** | 1.0 |
| **Fecha de emisión** | 04-07-2026 |
| **Autor** | Luciano Alonso Pereira Rodríguez |
| **Documentos relacionados** | Plan de Proyecto (F6), Propuesta de Solución (§11) |

---

## 1. Objetivo y alcance

Definir la estrategia, niveles, casos y criterios de aceptación de las pruebas del sistema Pyval: backend de microservicios, frontend web (storefront + panel de administración) e integraciones externas.

**Fuera de alcance:** pruebas de las plataformas de terceros en sí (Transbank, SII, Meta, courier); solo se prueban las integraciones con ellas.

## 2. Niveles de prueba

| Nivel | Qué valida | Herramientas | Responsable | Cuándo |
|-------|------------|--------------|-------------|--------|
| **Unitarias** | Lógica de cada service (mocks de repositorio y Feign) | JUnit 5 + Mockito | Dev | En cada cambio (CI) |
| **Integración** | Comunicación entre servicios vía Feign/Gateway | Spring Boot Test, Postman | Dev | Por feature |
| **End-to-end (E2E)** | Flujo completo de compra desde el navegador | Manual guiado + Postman collection | Dev | Antes de cada release |
| **Conversacional** | Agente WhatsApp: comprensión, datos reales, derivación | Batería de mensajes reales | Dev + Dueño | F5/F6 |
| **Carga / rendimiento** | Respuesta bajo concurrencia del flujo de compra | k6 o JMeter | Dev | Pre-producción |
| **Seguridad** | Autenticación, autorización, secretos, entradas | Revisión manual + escáner de dependencias | Dev | Pre-producción |
| **UAT (aceptación)** | El sistema cumple lo esperado por el negocio | Guion de aceptación | Dueño | Antes del go-live |

## 3. Criterios

**Criterios de entrada:** código compilando, tests unitarios en verde, ambiente con datos de prueba.

**Criterios de aceptación (salida):**
- 100% de los tests unitarios y de integración en verde.
- Flujo E2E de compra completo sin defectos bloqueantes.
- Cero defectos críticos abiertos; los menores documentados con plan.
- UAT firmada por el dueño.

**Cobertura objetivo:** lógica de negocio de los services ≥ 80%.

## 4. Casos de prueba clave

### 4.1 Autenticación y autorización
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| A1 | Registro con datos válidos | 201 y usuario creado con rol CLIENTE |
| A2 | Registro con email duplicado | 409 Conflict |
| A3 | Login correcto | 200 con token JWT, id y rol |
| A4 | Login con contraseña errónea | 401 |
| A5 | Acceso a ruta protegida sin token | 401 desde el gateway |
| A6 | Acceso a ruta ADMIN con rol CLIENTE | 403 desde el gateway |
| A7 | Token expirado | 401 |

### 4.2 Catálogo y carrito
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| C1 | Listar productos activos sin token | 200 (catálogo público) |
| C2 | Agregar producto al carrito | 200, ítem con precio real de ms-productos |
| C3 | Agregar producto inexistente/inactivo | 404 |
| C4 | Actualizar cantidad a 0 | 400 (mínimo 1) |
| C5 | Vaciar carrito | 204 y carrito sin ítems |

### 4.3 Pedido, pago y flujo posterior
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| P1 | Crear pedido desde carrito | 201 con subtotal + IVA 19% + total correctos |
| P2 | Pedido con stock insuficiente | 400/409 según regla |
| P3 | Procesar pago del pedido | Pago APROBADO con transacción |
| P4 | Pago aprobado dispara despacho | Despacho creado con tracking TRK-* |
| P5 | Pago aprobado dispara notificación | Notificación tipo PAGO para el usuario |
| P6 | Cambio de estado del despacho notifica | Notificación tipo DESPACHO |
| P7 | ms-notificaciones caído durante pago | El pago igual se confirma (best-effort, log de warning) |
| P8 | Reembolso de pago aprobado | Estado REEMBOLSADO |
| P9 | Reembolso de pago no aprobado | Error controlado |

### 4.4 Reportes (admin)
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| R1 | Resumen de ventas con rango de fechas | KPIs consistentes con los datos |
| R2 | Exportar Excel | Archivo .xlsx válido descargable |
| R3 | Acceso a reportes sin rol ADMIN | 403 |

### 4.5 Frontend (E2E manual guiado)
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| F1 | Navegar catálogo, filtrar y buscar | Productos visibles y filtrables |
| F2 | Compra completa: carrito → checkout → pago → confirmación | Pedido visible en "Mis pedidos" |
| F3 | Seguimiento del pedido con tracking | Estado del despacho visible |
| F4 | Panel admin: CRUD de producto | Cambios reflejados en el catálogo |
| F5 | Panel admin: ver ventas y descargar Excel | Reporte descargado |
| F6 | Responsivo móvil (viewport 375px) | Navegación y compra usables |

### 4.6 Integraciones externas (cuando se activen)
| ID | Caso | Resultado esperado |
|----|------|--------------------|
| X1 | Webpay ambiente de integración: pago aprobado/rechazado con tarjetas de prueba | Estados reflejados correctamente |
| X2 | Webhook del courier duplicado o fuera de orden | Idempotencia: estado no retrocede ni duplica |
| X3 | Agente WhatsApp: 20 preguntas frecuentes reales | ≥80% respondidas correctamente con datos reales |
| X4 | Agente WhatsApp: solicitud fuera de alcance | Derivación a humano |
| X5 | Emisión de boleta electrónica en certificación SII | DTE aceptado |

## 5. Datos y ambientes

- **Local/dev:** MySQL local, datos de prueba sembrados (productos demo, usuario admin y cliente).
- **Staging (futuro cloud):** réplica de producción con credenciales de integración de terceros.
- Las credenciales de prueba **nunca** se comparten con producción.

## 6. Gestión de defectos

Los hallazgos se registran como tarjetas en el tablero (Notion/Trello) con severidad: **Crítico** (bloquea release) · **Mayor** (funcionalidad degradada) · **Menor** (cosmético). Un release no sale con críticos abiertos.
